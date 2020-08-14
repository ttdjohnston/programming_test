package SaleModeller;

import DataImport.GrantEvent;
import DataImport.ProcessedFile;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

public class SaleModeller {
    String invalidSaleErrMsg = "Unable to process sale.  Not enough vested shares available";
    DecimalFormat decimalFormat = new DecimalFormat();

    public SaleModeller(){
        decimalFormat.setMaximumFractionDigits(2);
    }

    public List<String> modelSaleFromFile(ProcessedFile processedFile) throws GrantEventException{
        List<String> report = new ArrayList<>();

        for (String employeeNum : processedFile.getEmployeeGrantEvents().keySet()) {
            BigDecimal currentValueAvailable = BigDecimal.ZERO.setScale(2);
            BigDecimal valueFromSale = BigDecimal.ZERO.setScale(2);
            PriorityQueue<GrantEvent> orderedEvents = processedFile.getEmployeeGrantEvents().get(employeeNum);
            GrantEvent nextEvent;
            PriorityQueue<GrantEvent> vestEventsByDate = new PriorityQueue<>(new Comparator<GrantEvent>() {
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
            while (((nextEvent = orderedEvents.poll()) != null) && (nextEvent.getDate().compareTo(processedFile.getFooterDate()) < 1)) {
                if (nextEvent.getType().isVest()) {
                    vestEventsByDate.add(nextEvent);
                    vestEventsByValue.add(nextEvent);
                } else if (nextEvent.getType().isPerf()) {
                    applyPerformanceMultiplier(vestEventsByDate, nextEvent, processedFile);
                } else if (nextEvent.getType().isSale()) {
                    valueFromSale = valueFromSale.add(sellLeastProfitable(vestEventsByValue, nextEvent));
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
            report.add(employeeNum + "," + currentValueAvailable + "," + valueFromSale);
        }
        return report;
    }

    private void applyPerformanceMultiplier(PriorityQueue<GrantEvent> vestEventsByDate, GrantEvent perfEvent, ProcessedFile processedFile) {
        for (GrantEvent vest : vestEventsByDate) {
            if (!vest.getDate().isAfter(processedFile.getFooterDate())) {
                BigDecimal newUnites = vest.getUnits().multiply(perfEvent.getPerfMultiplier());
                vest.updateUnits(newUnites.setScale(0, BigDecimal.ROUND_HALF_UP));
            }
            else
                break;
        }
    }

    private BigDecimal sellLeastProfitable(PriorityQueue<GrantEvent> vestEventsByValue, GrantEvent sellEvent) throws GrantEventException{
        if (vestEventsByValue == null || sellEvent == null)
            throw new GrantEventException(invalidSaleErrMsg);
        BigDecimal valueFromSale = BigDecimal.ZERO;
        BigDecimal unitsToSell = sellEvent.getUnits();
        for (GrantEvent nextEvent : vestEventsByValue) {
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
