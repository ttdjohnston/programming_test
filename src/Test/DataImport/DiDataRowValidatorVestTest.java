package Test.DataImport;

import DataImport.DiDataRowValidatorVest;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DiDataRowValidatorVestTest {
    DiDataRowValidatorVest validator;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        validator = new DiDataRowValidatorVest();
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        validator = null;
    }

    @org.junit.jupiter.api.Test
    void validateRow_ValidRow_ReturnsTrue() {
        String rowString = "VEST,EE#1,20000121,2,5.21";
        List<String> row = Arrays.asList(rowString.split(","));
        boolean actual = false;

        try {
            actual = validator.validateRow(row);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        assertTrue(actual);
    }

    @org.junit.jupiter.api.Test
    void validateRow_BlankEmplNum_Exception() {
        String rowString = "VEST,,20000121,2,5.21";
        List<String> row = Arrays.asList(rowString.split(","));
        boolean actual = false;

        try {
            actual = validator.validateRow(row);
        } catch (Exception e) {
            assertEquals("The Employee Number must not be blank",e.getMessage());
        }
        assertFalse(actual);
    }

    @org.junit.jupiter.api.Test
    void validateRow_InvalidDate_NonDigitCharacter_Exception() {
        String rowString = "VEST,EE#1,2000&121,2,5.21";
        List<String> row = Arrays.asList(rowString.split(","));
        boolean actual = false;

        try {
            actual = validator.validateRow(row);
        } catch (Exception e) {
            assertEquals("The vest date is invalid and must be in the form yyyyMMdd",e.getMessage());
        }
        assertFalse(actual);
    }

    @org.junit.jupiter.api.Test
    void validateRow_TooManyCharactersInDate_Exception() {
        String rowString = "VEST,EE#1,200000121,2,5.21";
        List<String> row = Arrays.asList(rowString.split(","));
        boolean actual = false;

        try {
            actual = validator.validateRow(row);
        } catch (Exception e) {
            assertEquals("The vest date is invalid and must be in the form yyyyMMdd",e.getMessage());
        }
        assertFalse(actual);

    }

    @org.junit.jupiter.api.Test
    void validateRow_InvalidLeapDay_Exception() {
        String rowString = "VEST,EE#1,20010229,2,5.21";
        List<String> row = Arrays.asList(rowString.split(","));
        boolean actual = false;

        try {
            actual = validator.validateRow(row);
        } catch (Exception e) {
            assertEquals("The vest date is invalid and must be in the form yyyyMMdd",e.getMessage());
        }
        try {
            SimpleDateFormat _simpleDateFormatter;
            _simpleDateFormatter = new SimpleDateFormat("yyyyMMdd");
            _simpleDateFormatter.setLenient(false);
            assertFalse(actual, "Found " + _simpleDateFormatter.parse("20010229").toString() + " instead of the expected");
        } catch (Exception e) {

        }
    }

    @org.junit.jupiter.api.Test
    void validateRow_NondigitVestingUnits_Exception() {
        String rowString = "VEST,EE#1,20000121,%,5.21";
        List<String> row = Arrays.asList(rowString.split(","));
        boolean actual = false;

        try {
            actual = validator.validateRow(row);
        } catch (Exception e) {
            assertEquals("The number of vesting units must be a positive, non-zero integer",e.getMessage());
        }
        assertFalse(actual);
    }

    @org.junit.jupiter.api.Test
    void validateRow_NegativeVestingUnits_Exception() {
        String rowString = "VEST,EE#1,20000121,-3,5.21";
        List<String> row = Arrays.asList(rowString.split(","));
        boolean actual = false;

        try {
            actual = validator.validateRow(row);
        } catch (Exception e) {
            assertEquals("The number of vesting units must be a positive, non-zero integer",e.getMessage());
        }
        assertFalse(actual);
    }

    @org.junit.jupiter.api.Test
    void validateRow_ZeroVestingUnits_Exception() {
        String rowString = "VEST,EE#1,20000121,0,5.21";
        List<String> row = Arrays.asList(rowString.split(","));
        boolean actual = false;

        try {
            actual = validator.validateRow(row);
        } catch (Exception e) {
            assertEquals("The number of vesting units must be a positive, non-zero integer",e.getMessage());
        }
        assertFalse(actual);
    }

    @org.junit.jupiter.api.Test
    void validateRow_NegativeGrantPrice_Exception() {
        String rowString = "VEST,EE#1,20000121,2,-5.21";
        List<String> row = Arrays.asList(rowString.split(","));
        boolean actual = false;

        try {
            actual = validator.validateRow(row);
        } catch (Exception e) {
            assertEquals("The grant price must be a positive, non-zero decimal (no symbols)",e.getMessage());
        }
        assertFalse(actual);
    }

    @org.junit.jupiter.api.Test
    void validateRow_ZeroGrantPrice_Valid() {
        String rowString = "VEST,EE#1,20000121,2,0.00";
        List<String> row = Arrays.asList(rowString.split(","));
        boolean actual = false;

        try {
            actual = validator.validateRow(row);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        assertTrue(actual);
    }

    @org.junit.jupiter.api.Test
    void validateRow_GrantPriceWithDollarSign_Exception() {
        String rowString = "VEST,EE#1,20000121,0,$5.21";
        List<String> row = Arrays.asList(rowString.split(","));
        boolean actual = false;

        try {
            actual = validator.validateRow(row);
        } catch (Exception e) {
            assertEquals("The number of vesting units must be a positive, non-zero integer",e.getMessage());
        }
        assertFalse(actual);
    }


}