package DataImport;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.*;

public class DiFileInterpreter {
    private final static int NUM_HEADER_ROWS = 1;
    private final static int NUM_FOOTER_ROWS = 1;
    private final static int SIZE_OF_FOOTER = 2;
    private final static String HEADER_ERROR_MSG = "The header must contain a single integer with the number of data rows to follow";
    private final static String FOOTER_ERROR_MSG = "The footer must contain a date in the form \"YYYYMMDD\" and the market price as a positive decimal separated by a comma";
    private final static int FOOTER_DATE_POSITION = 0;
    private final static int FOOTER_PRICE_POSITION = 1;
    private final static String DELIMINATOR = ",";

    public DiFileInterpreter() {

    }

    public DiFile interpretFile(List<String> importedFile) throws InvalidFileException {
        if (importedFile.size() < (NUM_HEADER_ROWS + NUM_FOOTER_ROWS + 1)) {
            throw new InvalidFileException("An import file must have the following structure: \n   Header Rows - " + NUM_HEADER_ROWS + "\n   Data Rows - >0 rows \n   Footer Rows - " + NUM_FOOTER_ROWS);
        }

        Integer header = interpretHeader(importedFile);
        FileFooter footer = interpretFooter(importedFile);
        List<DiDataRow> rows = interpretDataRows(importedFile);

        return new DiFile(header, rows, footer);
    }

    private List<DiDataRow> interpretDataRows(List<String> importedFile) throws InvalidFileException {
        List<String> rows = importedFile.subList(NUM_HEADER_ROWS, importedFile.size() - NUM_FOOTER_ROWS);
        DiDataRowsInterpreter rowValidator = new DiDataRowsInterpreter(DELIMINATOR);
        return rowValidator.interpretRows(rows);
    }

    private FileFooter interpretFooter(List<String> importedFile) throws InvalidFileException {
        try {
            List<String> splitFooter = Arrays.asList(importedFile.get(importedFile.size() - NUM_FOOTER_ROWS).split(","));
            if (splitFooter.size() != SIZE_OF_FOOTER || !splitFooter.get(FOOTER_PRICE_POSITION).matches("\\d*\\.?\\d+")) {
                throw new InvalidFileException(FOOTER_ERROR_MSG);
            }
            DateTimeFormatter formatter = DateTimeFormatter.BASIC_ISO_DATE;
            formatter.withResolverStyle(ResolverStyle.STRICT);
            LocalDate footerDate = LocalDate.parse(splitFooter.get(FOOTER_DATE_POSITION), formatter);
            Double marketPrice = Double.valueOf(splitFooter.get(FOOTER_PRICE_POSITION));
            return new FileFooter(footerDate, marketPrice);
        } catch (NumberFormatException | DateTimeParseException e) {
            throw new InvalidFileException(FOOTER_ERROR_MSG);
        }
    }

    private Integer interpretHeader(List<String> importedFile) throws InvalidFileException {
        try {
            Integer numDataRows = Integer.valueOf(importedFile.get(0));
            if (numDataRows != (importedFile.size() - NUM_FOOTER_ROWS - NUM_HEADER_ROWS)) {
                throw new InvalidFileException(HEADER_ERROR_MSG);
            }
            return numDataRows;
        } catch (NumberFormatException e) {
            throw new InvalidFileException(HEADER_ERROR_MSG);
        }
    }
}
