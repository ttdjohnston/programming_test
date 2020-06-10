package DataImport;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;


public class DiDataRowInterpreterVest {
    private final static int ROW_TYPE_POS = 0;
    private final static int EMPL_NUM_POS = 1;
    private final static int VESTDATE_POS = 2;
    private final static int NUM_UNITS_POS = 3;
    private final static int GRANT_PRICE_POS = 4;
    private final static String DATE_PATTERN = "yyyyMMdd";
    private final static String EMPL_NUM_ERR_MSG = "The Employee Number must not be blank";
    private final static String VEST_DATE_ERR_MSG = "The vest date is invalid and must be in the form yyyyMMdd";
    private final static String VESTING_UNITS_ERR_MSG = "The number of vesting units must be a positive, non-zero integer";
    private final static String GRANT_PRICE_ERR_MSG = "The grant price must be a positive, non-zero decimal (no symbols)";
    private final DateTimeFormatter _dateTimeFormatter;

    public DiDataRowInterpreterVest() {
        _dateTimeFormatter = DateTimeFormatter.BASIC_ISO_DATE;
        _dateTimeFormatter.withResolverStyle(ResolverStyle.STRICT);
    }

    public DiDataRowVest interpretRow(List<String> row) throws InvalidRowException {
        if (row.get(EMPL_NUM_POS).length() == 0) {
            throw new InvalidRowException(EMPL_NUM_ERR_MSG);
        }
        LocalDate vestDate;
        try {
            if (row.get(VESTDATE_POS).length() != DATE_PATTERN.length()) {
                throw new InvalidRowException((VEST_DATE_ERR_MSG));
            }
            vestDate = LocalDate.parse(row.get(VESTDATE_POS), _dateTimeFormatter);
        }
        catch (DateTimeParseException e) {
            throw new InvalidRowException(VEST_DATE_ERR_MSG);
        }
        Integer vestingUnits;
        try {
            vestingUnits = Integer.valueOf(row.get(NUM_UNITS_POS));
            if (vestingUnits <= 0) {
                throw new InvalidRowException(VESTING_UNITS_ERR_MSG);
            }
        } catch (NumberFormatException e) {
            throw new InvalidRowException(VESTING_UNITS_ERR_MSG);
        }
        Double grantPrice;
        try {
            grantPrice = Double.valueOf(row.get(GRANT_PRICE_POS));
            if (grantPrice < 0) {
                throw new InvalidRowException(GRANT_PRICE_ERR_MSG);
            }
        } catch (NumberFormatException e) {
            throw new InvalidRowException(GRANT_PRICE_ERR_MSG);
        }
        return new DiDataRowVest(new DiDataRowType(row.get(ROW_TYPE_POS)), row.get(EMPL_NUM_POS), vestDate, vestingUnits, grantPrice);
    }
}
