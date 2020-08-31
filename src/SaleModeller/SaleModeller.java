package SaleModeller;

import DataImport.FileFooter;
import DataImport.GrantEvent;
import DataImport.ProcessedFile;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class SaleModeller {
    String invalidSaleErrMsg = "Unable to process sale.  Not enough vested shares available";

    public SaleModeller(){}

    public Map<String, EmployeeModelResult> modelSaleFromFile(ProcessedFile processedFile) throws GrantEventException {
        Map<String, EmployeeModelResult> report = new HashMap<>();

        for (String employeeNum : processedFile.getEmployeeGrantEvents().keySet()) {
            BigDecimal currentValueAvailable = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_DOWN);
            BigDecimal valueFromSale = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_DOWN);
            PriorityQueue<GrantEvent> orderedEvents = processedFile.getEmployeeGrantEvents().get(employeeNum);
            GrantEvent nextEvent;
            PriorityQueue<GrantEvent> vestEventsByDate = new PriorityQueue<>(orderedEvents.size(), new Comparator<GrantEvent>() {
                @Override
                public int compare(GrantEvent o1, GrantEvent o2) {
                    return o1.getDate().compareTo(o2.getDate());
                }
            });
            PriorityQueue<GrantEvent> vestEventsByValue = new PriorityQueue<>(new Comparator<GrantEvent>() {
                @Override
                public int compare(GrantEvent o1, GrantEvent o2) {
                    return (o1.getUnits().multiply((processedFile.getFooterPrice().subtract(o1.getPrice())))
                                    .subtract(o2.getUnits().multiply(processedFile.getFooterPrice().subtract(o2.getPrice()))))
                                    .setScale(0, BigDecimal.ROUND_HALF_UP)
                                    .intValue();
                }
            });
            PriorityQueue<GrantEvent> perfEventsByDate = new PriorityQueue<>(new Comparator<GrantEvent>() {
                @Override
                public int compare(GrantEvent o1, GrantEvent o2) {
                    return o1.getDate().compareTo(o2.getDate());
                }
            });
            while (((nextEvent = orderedEvents.poll()) != null) && (nextEvent.getDate().compareTo(processedFile.getFooterDate()) < 1)) {
                if (nextEvent.getType().isVest()) {
                    vestEventsByDate.add(nextEvent);
                    vestEventsByValue.add(nextEvent);
                } else if (nextEvent.getType().isPerf()) {
                    perfEventsByDate.add(nextEvent);
                    applyPerformanceMultiplier(vestEventsByDate, nextEvent, processedFile);
                } else if (nextEvent.getType().isSale()) {
                    valueFromSale = valueFromSale.add(sellAccordingToOrderedList(vestEventsByValue, nextEvent, shouldSellLeastValuable(nextEvent, perfEventsByDate, processedFile.getFooterDate(), processedFile.getFooterPrice())));
                } else {
                    throw new GrantEventException("Could not determine the type of event");
                }
            }
            BigDecimal eventValue;
            while (((nextEvent = vestEventsByDate.poll()) != null) && (nextEvent.getDate().compareTo(processedFile.getFooterDate()) < 1)) {
                eventValue = nextEvent.getUnits().multiply(processedFile.getFooterPrice().subtract(nextEvent.getPrice()));
                if (eventValue.doubleValue() > 0.0)
                    currentValueAvailable = currentValueAvailable.add(eventValue);
            }
            report.put(employeeNum, new EmployeeModelResult(employeeNum,currentValueAvailable.setScale(2, BigDecimal.ROUND_HALF_DOWN) , valueFromSale.setScale(2, BigDecimal.ROUND_HALF_DOWN)));
        }
        return report;
    }

    private void applyPerformanceMultiplier(PriorityQueue<GrantEvent> vestEventsByDate, GrantEvent perfEvent, ProcessedFile processedFile) {
        for (GrantEvent vest : vestEventsByDate.stream().filter(event -> !event.getDate().isAfter(processedFile.getFooterDate())).collect(Collectors.toList())) {
            BigDecimal newUnites = vest.getUnits().multiply(perfEvent.getPerfMultiplier());
            vest.updateUnits(newUnites.setScale(0, BigDecimal.ROUND_HALF_UP));
        }
    }

    private boolean shouldSellLeastValuable(GrantEvent sellEvent, PriorityQueue<GrantEvent> perfEventsByDate, LocalDate footerDate, BigDecimal footerPrice) {
        BigDecimal sellUnitsAtFooterDate = sellEvent.getUnits();
        List<GrantEvent> perfEventsInRange = perfEventsByDate.stream()
                                                .filter(event -> event.getDate().isAfter(sellEvent.getDate()) && !event.getDate().isAfter(footerDate))
                                                .collect(Collectors.toList());
        for (GrantEvent nextPerfEvent : perfEventsInRange) {
            sellUnitsAtFooterDate = sellUnitsAtFooterDate.multiply(nextPerfEvent.getPerfMultiplier());
        }

        return sellEvent.getUnits().multiply(sellEvent.getPrice()).compareTo(sellUnitsAtFooterDate.multiply(footerPrice)) < 1;
    }

    private BigDecimal sellAccordingToOrderedList(PriorityQueue<GrantEvent> vestEventsByValue, GrantEvent sellEvent, boolean sellLeastValuable) throws GrantEventException{
        if (vestEventsByValue == null || sellEvent == null)
            throw new GrantEventException(invalidSaleErrMsg);
        BigDecimal valueFromSale = BigDecimal.ZERO;
        BigDecimal unitsToSell = sellEvent.getUnits();
        List<GrantEvent> vestEventsToSellInOrder = vestEventsByValue.stream()
                                                    .filter(event -> !event.getDate().isAfter(sellEvent.getDate()))
                                                    .collect(Collectors.toList());
        if (!sellLeastValuable) {
            Collections.reverse(vestEventsToSellInOrder);
        }
        for (GrantEvent nextEvent : vestEventsToSellInOrder) {
            if (!nextEvent.getDate().isAfter(sellEvent.getDate())) {
                if (nextEvent.getUnits().compareTo(unitsToSell) < 0) {
                    valueFromSale = valueFromSale.add(nextEvent.getUnits().multiply(sellEvent.getPrice().subtract(nextEvent.getUnits().multiply(nextEvent.getPrice()))));
                    unitsToSell = unitsToSell.subtract(nextEvent.getUnits());
                    nextEvent.updateUnits(BigDecimal.ZERO);
                }
                else {
                    valueFromSale = valueFromSale.add(unitsToSell.multiply(sellEvent.getPrice()).subtract(nextEvent.getPrice().multiply(unitsToSell)));
                    nextEvent.updateUnits(nextEvent.getUnits().subtract(unitsToSell));
                    unitsToSell = BigDecimal.ZERO;
                }
            }
            if (unitsToSell.compareTo(BigDecimal.ZERO) == 0) {
                return valueFromSale;
            }
        }
        if (unitsToSell.compareTo(BigDecimal.ZERO) != 0) {
            throw new GrantEventException(invalidSaleErrMsg);
        }
        return valueFromSale;
    }
}
