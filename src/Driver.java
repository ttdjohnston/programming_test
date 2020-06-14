import DataImport.DiFileProcessor;
import DataImport.GrantEvent;
import DataImport.InvalidFileException;
import DataImport.ProcessedFile;
import Test.Report.ReportGenerator;

import java.util.*;

public class Driver {

    public static void main(String[] args) {
        List<String> importedFile = readImportFromSI();

        DiFileProcessor _diFileProcessor = new DiFileProcessor();
        ProcessedFile processedFile = null;
        try {
            processedFile = _diFileProcessor.processFile(importedFile);
        } catch (InvalidFileException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        List<String> report = (new ReportGenerator(processedFile)).generateReport();
        displayReportToSO(report);
    }

    private static List<String> readImportFromSI() {
        List<String> importedData = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            importedData.add(scanner.nextLine());
        }
        scanner.close();
        return importedData;
    }

    private static void displayReportToSO(List<String> output) {
        for (String line : output) {
            System.out.println(line);
        }
    }
}
