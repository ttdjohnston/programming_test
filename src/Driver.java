import DataImport.DiFileProcessor;
import DataImport.InvalidFileException;
import DataImport.ProcessedFile;
import SaleModeller.GrantEventException;
import SaleModeller.SaleModeller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Driver {

    public static void main(String[] args) {
        List<String> importedFile = new ArrayList<>();

        if (args.length == 2 && "-f".equals(args[0])) {
            try {
                importedFile = readImportFromScanner(new Scanner(new File(args[1])));
            } catch (FileNotFoundException e) {
                System.out.println("Could not find file");
                System.exit(-1);
            }
        } else {
            importedFile = readImportFromSI();
        }

        DiFileProcessor _diFileProcessor = new DiFileProcessor();
        ProcessedFile processedFile = null;
        try {
            processedFile = _diFileProcessor.processFile(importedFile);
        } catch (InvalidFileException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        try {
            List<String> report = (new SaleModeller()).modelSaleFromFile(processedFile);
            displayReportToSO(report);
        } catch (GrantEventException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static List<String> readImportFromSI() {
        return readImportFromScanner(new Scanner(System.in));
    }

    private static List<String> readImportFromScanner(Scanner scanner) {
        List<String> importedData = new ArrayList<>();
        while (scanner.hasNextLine()) {
            importedData.add(scanner.nextLine());
        }
        scanner.close();
        return importedData;
    }

    private static void displayReportToSO(List<String> output) {
        output.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        for (String line : output) {
            System.out.println(line);
        }
    }

    private static void usage() {
        System.out.println("java -jar Driver.jar [-f inputFileName.txt]");
    }
}
