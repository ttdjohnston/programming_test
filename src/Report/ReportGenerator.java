package Report;

import DataImport.DiDataRowType;
import DataImport.GrantEvent;
import DataImport.ProcessedFile;

import java.util.*;

public class ReportGenerator {
    String invalidSaleErrMsg = "Unable to process sale.  Not enough vested shares available";
    ProcessedFile processedFile;

    public ReportGenerator(ProcessedFile processedFile){
        this.processedFile = processedFile;
    }

    public List<String> generateReport() throws GrantEventException{
        List<String> report = new ArrayList<>();

        for (String employeeNum : processedFile.getEmployeeGrantEvents().keySet()) {
            double currentValueAvailable = 0.0;
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
                    return (int) Math.round(o1.getUnits() * ((processedFile.getFooterPrice() - o1.getPrice())) - (o2.getUnits() * (processedFile.getFooterPrice() - o2.getPrice())));
                }
            });
            while (((nextEvent = orderedEvents.poll()) != null) && (nextEvent.getDate().compareTo(processedFile.getFooterDate()) < 1)) {
                if (nextEvent.getType().isVest()) {
                    vestEventsByDate.add(nextEvent);
                    vestEventsByValue.add(nextEvent);
                } else if (nextEvent.getType().isPerf()) {
                    applyPerformanceMultiplier(vestEventsByDate, nextEvent);
                } else if (nextEvent.getType().isSale()) {
                    sellLeastProfitable(vestEventsByValue, nextEvent);
                } else {
                    throw new GrantEventException("Could not determine the type of event");
                }
            }
            double eventValue;
            while (((nextEvent = vestEventsByDate.poll()) != null) && (nextEvent.getDate().compareTo(processedFile.getFooterDate()) < 1)) {
                eventValue = nextEvent.getUnits() * (processedFile.getFooterPrice() - nextEvent.getPrice());
                if (eventValue > 0.0)
                    currentValueAvailable += eventValue;
            }
            report.add(employeeNum + "," + currentValueAvailable);
        }
        return report;
    }

    private void applyPerformanceMultiplier(PriorityQueue<GrantEvent> vestEventsByDate, GrantEvent perfEvent) {
        for (GrantEvent vest : vestEventsByDate) {
            if (!vest.getDate().isAfter(processedFile.getFooterDate())) {
                double newUnites = vest.getUnits() * perfEvent.getPerfMultiplier();
                vest.updateUnits(roundHalfUp(newUnites));
            }
            else
                break;
        }
    }

    private void sellLeastProfitable(PriorityQueue<GrantEvent> vestEventsByValue, GrantEvent sellEvent) throws GrantEventException{
        if (vestEventsByValue == null || sellEvent == null)
            throw new GrantEventException(invalidSaleErrMsg);
        double unitsToSell = sellEvent.getUnits();
        for (GrantEvent nextEvent : vestEventsByValue) {
            if (!nextEvent.getDate().isAfter(sellEvent.getDate())) {
                if (nextEvent.getUnits() < unitsToSell) {
                    unitsToSell -= nextEvent.getUnits();
                    nextEvent.updateUnits(0.0);
                }
                else {
                    nextEvent.updateUnits(nextEvent.getUnits() - unitsToSell);
                    unitsToSell = 0.0;
                }
            }
            if (unitsToSell == 0.0) {
                return;
            }
        }
        if (unitsToSell != 0.0) {
            throw new GrantEventException(invalidSaleErrMsg);
        }
    }

    private double roundHalfUp(double input) {
        double floor = Math.floor(input);
        if (input - floor < 0.5) {
            return floor;
        }
        return floor + 1;
    }
}
