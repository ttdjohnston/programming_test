package DataImport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DiFileValidator {
    private final static int NUM_HEADER_ROWS = 1;
    private final static int NUM_FOOTER_ROWS = 1;
    private final static int SIZE_OF_FOOTER = 2;
    private final static String HEADER_ERROR_MSG = "The header must contain a single integer with the number of data rows to follow";
    private final static String FOOTER_ERROR_MSG = "The footer must contain a date in the form \"YYYYMMDD\" and the market price as a decimal separated by a comma";
    private final static int FOOTER_DATE_POSITION = 0;
    private final static int FOOTER_PRICE_POSITION = 0;



    public void validateFile(List<String> importedFile) throws InvalidFileException {
        if (importedFile.size() < (NUM_HEADER_ROWS + NUM_FOOTER_ROWS + 1)) {
            throw new InvalidFileException("An import file must have the following structure: \n   Header Rows - " + NUM_HEADER_ROWS + "\n   Data Rows - >0 \n   Footer Rows - " + NUM_FOOTER_ROWS);
        }

        validateHeader(importedFile);
        validateFooter(importedFile);
        validateDataRows(importedFile);
    }

    private void validateDataRows(List<String> importedFile) throws InvalidFileException {
        List<String> rows = importedFile.subList(NUM_HEADER_ROWS, importedFile.size() - (1 + NUM_FOOTER_ROWS));
        DiDataRowValidator rowValidator = new DiDataRowValidator();
        rowValidator.validateRows(rows);
    }



    private void validateFooter(List<String> importedFile) throws InvalidFileException {
        try {
            List<String> splitFooter = Arrays.asList(importedFile.get(importedFile.size() - NUM_FOOTER_ROWS).split(","));
            if (splitFooter.size() != SIZE_OF_FOOTER) {
                throw new InvalidFileException(FOOTER_ERROR_MSG);
            }
            Date footerDate = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).parse(splitFooter.get(FOOTER_DATE_POSITION));
            Double marketPrice = Double.parseDouble(splitFooter.get(FOOTER_PRICE_POSITION));
        } catch (NumberFormatException | ParseException e) {
            throw new InvalidFileException(FOOTER_ERROR_MSG);
        }
    }

    private void validateHeader(List<String> importedFile) throws InvalidFileException {
        try {
            Integer numDataRows = Integer.valueOf(importedFile.get(0));
            if (numDataRows != (importedFile.size() - NUM_FOOTER_ROWS - NUM_HEADER_ROWS)) {
                throw new InvalidFileException(HEADER_ERROR_MSG);
            }

        } catch (NumberFormatException e) {
            throw new InvalidFileException(HEADER_ERROR_MSG);
        }
    }
}
