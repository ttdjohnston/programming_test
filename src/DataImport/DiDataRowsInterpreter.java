package DataImport;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static DataImport.DiDataRowType.RowType.*;

public class DiDataRowsInterpreter {
    private String _deliminator;
    private final static int SIZE_OF_DATA_ROW_VEST = 5;
    private final static int SIZE_OF_DATA_ROW_PERF = 4;
    private final static int SIZE_OF_DATA_ROW_SALE = 5;
    private final static int ROW_TYPE_POS = 0;
    private final static int EMPL_NUM_POS = 1;
    private final static int VESTDATE_POS = 2;
    private final static int NUM_UNITS_POS = 3;
    private final static int PERFORMANCE_MULTIPLIER_POS = 3;
    private final static int GRANT_PRICE_POS = 4;
    private final static int SALE_PRICE_POS = 4;
    private final static String DATE_PATTERN = "yyyyMMdd";
    private final static String EMPL_NUM_ERR_MSG = "The Employee Number must not be blank";
    private final static String VEST_DATE_ERR_MSG = "The vest date is invalid and must be in the form yyyyMMdd";
    private final static String SALE_DATE_ERR_MSG = "The sale date is invalid and must be in the form yyyyMMdd";
    private final static String VESTING_UNITS_ERR_MSG = "The number of vesting units must be a positive, non-zero integer";
    private final static String SALE_UNITS_ERR_MSG = "The number of units being sold must be a positive, non-zero integer";
    private final static String GRANT_PRICE_ERR_MSG = "The grant price must be a positive decimal (no symbols)";
    private final static String SALE_PRICE_ERR_MSG = "The sale price must be a positive, non-zero decimal (no symbols)";
    private final static String PERF_MULTIPLIER_ERR_MSG = "The multiplier for a performance bonus must be a single decimal, greater than 1.0";
    private final DateTimeFormatter _dateTimeFormatter;

    public DiDataRowsInterpreter(String deliminator) {
        _deliminator = deliminator;
        _dateTimeFormatter = DateTimeFormatter.BASIC_ISO_DATE;
        _dateTimeFormatter.withResolverStyle(ResolverStyle.STRICT);
    }


    public List<DiDataRow> interpretRows(List<String> rows) throws InvalidFileException {
        StringBuilder err = new StringBuilder();
        List<DiDataRow> interpretedRowData = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            List<String> row = Arrays.asList(rows.get(i).split(_deliminator));
            try {
                DiDataRowType.RowType rowType = DiDataRowType.determineRowType(row.get(ROW_TYPE_POS));
                if (rowType == VEST && row.size() == SIZE_OF_DATA_ROW_VEST) {
                    interpretedRowData.add(interpretVestRow(row));
                } else if (rowType == PERF && row.size() == SIZE_OF_DATA_ROW_PERF) {
                    interpretedRowData.add(interpretPerfRow(row));
                } else if (rowType == SALE && row.size() == SIZE_OF_DATA_ROW_SALE)  {
                    interpretedRowData.add(interpretSaleRow(row));
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

    private LocalDate interpretVestDate(String date) throws InvalidRowException{
        try {
            if (date.length() != DATE_PATTERN.length()) {
                throw new InvalidRowException((VEST_DATE_ERR_MSG));
            }
            return LocalDate.parse(date, _dateTimeFormatter);
        }
        catch (DateTimeParseException e) {
            throw new InvalidRowException(VEST_DATE_ERR_MSG);
        }
    }

    private Integer interpretVestingUnits(String units) throws InvalidRowException {
        try {
            Integer vestingUnits = Integer.valueOf(units);
            if (vestingUnits <= 0) {
                throw new InvalidRowException(VESTING_UNITS_ERR_MSG);
            }
            return vestingUnits;
        } catch (NumberFormatException e) {
            throw new InvalidRowException(VESTING_UNITS_ERR_MSG);
        }
    }

    private Double interpretGrantPrice(String price) throws InvalidRowException{
        try {
            Double grantPrice = Double.valueOf(price);
            if (grantPrice < 0) {
                throw new InvalidRowException(GRANT_PRICE_ERR_MSG);
            }
            return grantPrice;
        } catch (NumberFormatException e) {
            throw new InvalidRowException(GRANT_PRICE_ERR_MSG);
        }
    }

    private boolean validateEmployeeNumber(String eeNum) {
        boolean isValid = true;
        if (eeNum.length() == 0) {
            isValid = false;
        }
        return isValid;
    }

    private Double interpretPerfMultiplier(String factor) throws InvalidRowException{
        try {
            Double multiplyer = Double.valueOf(factor);
            if (multiplyer <= 1.0) {
                throw new InvalidRowException(PERF_MULTIPLIER_ERR_MSG);
            }
            return multiplyer;
        } catch (NumberFormatException e) {
            throw new InvalidRowException(PERF_MULTIPLIER_ERR_MSG);
        }
    }

    private Integer interpretSaleUnits(String units) throws InvalidRowException {
        try {
            Integer vestingUnits = Integer.valueOf(units);
            if (vestingUnits <= 0) {
                throw new InvalidRowException(SALE_UNITS_ERR_MSG);
            }
            return vestingUnits;
        } catch (NumberFormatException e) {
            throw new InvalidRowException(SALE_UNITS_ERR_MSG);
        }
    }

    private LocalDate interpretSaleDate(String date) throws InvalidRowException{
        try {
            if (date.length() != DATE_PATTERN.length()) {
                throw new InvalidRowException((SALE_DATE_ERR_MSG));
            }
            return LocalDate.parse(date, _dateTimeFormatter);
        }
        catch (DateTimeParseException e) {
            throw new InvalidRowException(SALE_DATE_ERR_MSG);
        }
    }

    private Double interpretSalePrice(String price) throws InvalidRowException{
        try {
            Double grantPrice = Double.valueOf(price);
            if (grantPrice <= 0) {
                throw new InvalidRowException(SALE_PRICE_ERR_MSG);
            }
            return grantPrice;
        } catch (NumberFormatException e) {
            throw new InvalidRowException(SALE_PRICE_ERR_MSG);
        }
    }

    private DiDataRow interpretVestRow(List<String> row) throws InvalidRowException {
        if (!validateEmployeeNumber(row.get(EMPL_NUM_POS))) {
            throw new InvalidRowException(EMPL_NUM_ERR_MSG);
        }
        LocalDate vestDate = interpretVestDate(row.get(VESTDATE_POS));
        Integer vestingUnits = interpretVestingUnits(row.get(NUM_UNITS_POS));
        Double grantPrice = interpretGrantPrice(row.get(GRANT_PRICE_POS));
        return DiDataRow.newBuilder()
                .setType(new DiDataRowType(row.get(ROW_TYPE_POS)))
                .setEmpNum(row.get(EMPL_NUM_POS))
                .setDate(vestDate)
                .setUnits(vestingUnits)
                .setGrantPrice(grantPrice)
                .build();
    }

    private DiDataRow interpretPerfRow(List<String> row) throws InvalidRowException {
        if (!validateEmployeeNumber(row.get(EMPL_NUM_POS))) {
            throw new InvalidRowException(EMPL_NUM_ERR_MSG);
        }
        LocalDate vestDate = interpretVestDate(row.get(VESTDATE_POS));
        Double perfMultiplier = interpretPerfMultiplier(row.get(PERFORMANCE_MULTIPLIER_POS));
        return DiDataRow.newBuilder()
                .setType(new DiDataRowType(row.get(ROW_TYPE_POS)))
                .setEmpNum(row.get(EMPL_NUM_POS))
                .setDate(vestDate)
                .setPerformanceMultiplier(perfMultiplier)
                .build();
    }

    private DiDataRow interpretSaleRow(List<String> row) throws InvalidRowException {
        if (!validateEmployeeNumber(row.get(EMPL_NUM_POS))) {
            throw new InvalidRowException(EMPL_NUM_ERR_MSG);
        }
        LocalDate saleDate = interpretSaleDate(row.get(VESTDATE_POS));
        Integer saleUnits = interpretSaleUnits(row.get(NUM_UNITS_POS));
        Double salePrice = interpretSalePrice(row.get(SALE_PRICE_POS));
        return DiDataRow.newBuilder()
                .setType(new DiDataRowType(row.get(ROW_TYPE_POS)))
                .setEmpNum(row.get(EMPL_NUM_POS))
                .setDate(saleDate)
                .setUnits(saleUnits)
                .setSalePrice(salePrice)
                .build();
    }
}
