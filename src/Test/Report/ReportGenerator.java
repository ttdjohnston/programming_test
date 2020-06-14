package Test.Report;

import DataImport.GrantEvent;
import DataImport.ProcessedFile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

public class ReportGenerator {

    ProcessedFile processedFile;

    public ReportGenerator(ProcessedFile processedFile){
        this.processedFile = processedFile;
    }

    public List<String> generateReport() {
        List<String> report = new ArrayList<>();

        for (String employeeNum : processedFile.getEmployeeGrantEvents().keySet()) {
            double currentValueAvailable = 0.0;
            PriorityQueue<GrantEvent> orderedEvents = processedFile.getEmployeeGrantEvents().get(employeeNum);
            GrantEvent nextEvent;
            double eventValue;
            while (((nextEvent = orderedEvents.poll()) != null) && (nextEvent.getDate().compareTo(processedFile.getFooterDate()) < 1)) {
                eventValue = nextEvent.getUnits() * (processedFile.getFooterPrice() - nextEvent.getPrice());
                if (eventValue > 0.0)
                    currentValueAvailable += eventValue;
            }
            report.add(employeeNum + "," + currentValueAvailable);
        }
        return report;
    }
}
