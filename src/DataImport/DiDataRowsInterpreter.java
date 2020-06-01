package DataImport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiDataRowsInterpreter {
    private final static int ROW_TYPE_POS = 0;
    private final static int SIZE_OF_DATA_ROW_VEST = 5;


    public List<DiDataRow> interpretRows(List<String> rows) throws InvalidFileException {
        StringBuilder err = new StringBuilder();
        List<DiDataRow> interpretedRowData = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            List<String> row = Arrays.asList(rows.get(i).split(","));
            try {
                if (DiDataRowType.isVest(row.get(ROW_TYPE_POS)) && row.size() == SIZE_OF_DATA_ROW_VEST) {
                    DiDataRowInterpreterVest vestValidator = new DiDataRowInterpreterVest();
                    interpretedRowData.add(vestValidator.interpretRow(row));
                } else {
                    err.append("Data Row ").append(i+1).append(" is incorrectly formatted\n");
                }
            } catch (InvalidRowException e) {
                err.append("Data Row ").append(i+1).append(": ").append(e.getMessage()).append("\n");
            }
        }
        if (err.toString().length() > 0) {
            throw new InvalidFileException(err.toString());
        }
        return interpretedRowData;
    }
}
