package DataImport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class DiDataRowValidatorVest {
    private final static int VEST_EMPL_NUM_POS = 1;
    private final static int VEST_VESTDATE_POS = 2;
    private final static int VEST_NUM_UNITS_POS = 3;
    private final static int VEST_GRANT_PRICE_POS = 4;
    private final static String DATE_PATTERN = "yyyyMMdd";
    private final static String EMPL_NUM_ERR_MSG = "The Employee Number must not be blank";
    private final static String VEST_DATE_ERR_MSG = "The vest date is invalid and must be in the form yyyyMMdd";
    private final static String VESTING_UNITS_ERR_MSG = "The number of vesting units must be a positive, non-zero integer";
    private final static String GRANT_PRICE_ERR_MSG = "The grant price must be a positive, non-zero decimal (no symbols)";
    private final SimpleDateFormat _simpleDateFormatter;

    public DiDataRowValidatorVest() {
        _simpleDateFormatter = new SimpleDateFormat(DATE_PATTERN);
        _simpleDateFormatter.setLenient(false);
    }

    public boolean validateRow(List<String> row) throws InvalidRowException {
        if (row.get(VEST_EMPL_NUM_POS).length() == 0) {
            throw new InvalidRowException(EMPL_NUM_ERR_MSG);
        }
        try {
            if (row.get(VEST_VESTDATE_POS).length() != 8) {
                throw new InvalidRowException((VEST_DATE_ERR_MSG));
            }
            Date vestDate = _simpleDateFormatter.parse(row.get(VEST_VESTDATE_POS));
        } catch (ParseException e) {
            throw new InvalidRowException(VEST_DATE_ERR_MSG);
        }
        try {
            Integer vestingUnits = Integer.parseInt(row.get(VEST_NUM_UNITS_POS));
            if (vestingUnits <= 0) {
                throw new InvalidRowException(VESTING_UNITS_ERR_MSG);
            }
        } catch (NumberFormatException e) {
            throw new InvalidRowException(VESTING_UNITS_ERR_MSG);
        }
        try {
            Double grantPrice = Double.parseDouble(row.get(VEST_GRANT_PRICE_POS));
            if (grantPrice < 0) {
                throw new InvalidRowException(GRANT_PRICE_ERR_MSG);
            }
        } catch (NumberFormatException e) {
            throw new InvalidRowException(GRANT_PRICE_ERR_MSG);
        }
        return true;
    }
}
