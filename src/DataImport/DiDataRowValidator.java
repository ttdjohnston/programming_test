package DataImport;

import java.util.Arrays;
import java.util.List;

public class DiDataRowValidator {
    private final static int SIZE_OF_DATA_ROW = 5;
    private final static int ROW_TYPE_POS = 0;


    public void validateRows(List<String> rows) throws InvalidFileException {
        StringBuilder err = new StringBuilder();
        for (int i = 0; i < rows.size() - 1; i++) {
            List<String> row = Arrays.asList(rows.get(i).split(","));
            try {
                if (DiRowType.isVest(row.get(ROW_TYPE_POS)) && row.size() == SIZE_OF_DATA_ROW) {
                    DiDataRowValidatorVest vestValidator = new DiDataRowValidatorVest();
                    vestValidator.validateRow(row);
                } else {
                    err.append("Data row ").append(i).append(" is incorrectly formatted \n");
                }
            } catch (InvalidRowException e) {
                err.append("Data row ").append(i).append(": ").append(e.getMessage()).append("\n");
            }

        }
        if (err.length() > 0) {
            throw new InvalidFileException(err.toString());
        }
    }
}
