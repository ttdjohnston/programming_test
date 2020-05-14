import DataImport.DiFileProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Driver {

    public static void main(String[] args) {
        List<String> importedFile = readImportFromSI();

        DiFileProcessor _diFileProcessor = new DiFileProcessor();
        _diFileProcessor.processFile(importedFile);

        List<String> report = null;
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
