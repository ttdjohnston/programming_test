package Test.DataImport;

import DataImport.DiDataRowInterpreterVest;
import DataImport.DiDataRowType;
import DataImport.DiDataRowVest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DiDataRowsInterpreterVestTest {
    DiDataRowInterpreterVest interpreter;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        interpreter = new DiDataRowInterpreterVest();
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        interpreter = null;
    }

    @org.junit.jupiter.api.Test
    void validateRow_ValidRow_ReturnsTrue() {
        String rowString = "VEST,EE#1,20000121,2,5.21";
        List<String> row = Arrays.asList(rowString.split(","));
        DiDataRowVest expected = new DiDataRowVest(new DiDataRowType(row.get(0)), row.get(1), LocalDate.of(2000, 1, 21), new Integer(row.get(3)), new Double(row.get(4)));
        DiDataRowVest actual = null;

        try {
            actual = interpreter.interpretRow(row);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.getEmpNum(), actual.getEmpNum());
        assertEquals(expected.getDate().getYear(), actual.getDate().getYear());
        assertEquals(expected.getDate().getMonth(), actual.getDate().getMonth());
        assertEquals(expected.getDate().getDayOfMonth(), actual.getDate().getDayOfMonth());
        assertEquals(expected.getUnitsVested(), actual.getUnitsVested());
        assertEquals(expected.getGrantPrice(), actual.getGrantPrice());
    }

    @org.junit.jupiter.api.Test
    void validateRow_BlankEmplNum_Exception() {
        String rowString = "VEST,,20000121,2,5.21";
        List<String> row = Arrays.asList(rowString.split(","));
        DiDataRowVest actual = null;

        try {
            actual = interpreter.interpretRow(row);
        } catch (Exception e) {
            assertEquals("The Employee Number must not be blank",e.getMessage());
        }
        assertNull(actual);
    }

    @org.junit.jupiter.api.Test
    void validateRow_InvalidDate_NonDigitCharacter_Exception() {
        String rowString = "VEST,EE#1,2000&121,2,5.21";
        List<String> row = Arrays.asList(rowString.split(","));
        DiDataRowVest actual = null;

        try {
            actual = interpreter.interpretRow(row);
        } catch (Exception e) {
            assertEquals("The vest date is invalid and must be in the form yyyyMMdd",e.getMessage());
        }
        assertNull(actual);
    }

    @org.junit.jupiter.api.Test
    void validateRow_TooManyCharactersInDate_Exception() {
        String rowString = "VEST,EE#1,200000121,2,5.21";
        List<String> row = Arrays.asList(rowString.split(","));
        DiDataRowVest actual = null;

        try {
            actual = interpreter.interpretRow(row);
        } catch (Exception e) {
            assertEquals("The vest date is invalid and must be in the form yyyyMMdd",e.getMessage());
        }
        assertNull(actual);

    }

    @org.junit.jupiter.api.Test
    void validateRow_InvalidLeapDay_Exception() {
        String rowString = "VEST,EE#1,20010229,2,5.21";
        List<String> row = Arrays.asList(rowString.split(","));
        DiDataRowVest actual = null;

        try {
            actual = interpreter.interpretRow(row);
        } catch (Exception e) {
            assertEquals("The vest date is invalid and must be in the form yyyyMMdd", e.getMessage());
        }
        assertNull(actual);
    }

    @org.junit.jupiter.api.Test
    void validateRow_NondigitVestingUnits_Exception() {
        String rowString = "VEST,EE#1,20000121,%,5.21";
        List<String> row = Arrays.asList(rowString.split(","));
        DiDataRowVest actual = null;

        try {
            actual = interpreter.interpretRow(row);
        } catch (Exception e) {
            assertEquals("The number of vesting units must be a positive, non-zero integer",e.getMessage());
        }
        assertNull(actual);
    }

    @org.junit.jupiter.api.Test
    void validateRow_NegativeVestingUnits_Exception() {
        String rowString = "VEST,EE#1,20000121,-3,5.21";
        List<String> row = Arrays.asList(rowString.split(","));
        DiDataRowVest actual = null;

        try {
            actual = interpreter.interpretRow(row);
        } catch (Exception e) {
            assertEquals("The number of vesting units must be a positive, non-zero integer",e.getMessage());
        }
        assertNull(actual);
    }

    @org.junit.jupiter.api.Test
    void validateRow_ZeroVestingUnits_Exception() {
        String rowString = "VEST,EE#1,20000121,0,5.21";
        List<String> row = Arrays.asList(rowString.split(","));
        DiDataRowVest actual = null;

        try {
            actual = interpreter.interpretRow(row);
        } catch (Exception e) {
            assertEquals("The number of vesting units must be a positive, non-zero integer",e.getMessage());
        }
        assertNull(actual);
    }

    @org.junit.jupiter.api.Test
    void validateRow_NegativeGrantPrice_Exception() {
        String rowString = "VEST,EE#1,20000121,2,-5.21";
        List<String> row = Arrays.asList(rowString.split(","));
        DiDataRowVest actual = null;

        try {
            actual = interpreter.interpretRow(row);
        } catch (Exception e) {
            assertEquals("The grant price must be a positive, non-zero decimal (no symbols)",e.getMessage());
        }
        assertNull(actual);
    }

    @org.junit.jupiter.api.Test
    void validateRow_ZeroGrantPrice_Valid() {
        String rowString = "VEST,EE#1,20000121,2,0.00";
        List<String> row = Arrays.asList(rowString.split(","));
        DiDataRowVest expected = new DiDataRowVest(new DiDataRowType(row.get(0)), row.get(1), LocalDate.of(2000,1,21), new Integer(row.get(3)), new Double(row.get(4)));
        DiDataRowVest actual = null;

        try {
            actual = interpreter.interpretRow(row);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.getEmpNum(), actual.getEmpNum());
        assertEquals(expected.getDate().getYear(), actual.getDate().getYear());
        assertEquals(expected.getDate().getMonth(), actual.getDate().getMonth());
        assertEquals(expected.getDate().getDayOfMonth(), actual.getDate().getDayOfMonth());
        assertEquals(expected.getUnitsVested(), actual.getUnitsVested());
        assertEquals(expected.getGrantPrice(), actual.getGrantPrice());
    }

    @org.junit.jupiter.api.Test
    void validateRow_GrantPriceWithDollarSign_Exception() {
        String rowString = "VEST,EE#1,20000121,0,$5.21";
        List<String> row = Arrays.asList(rowString.split(","));
        DiDataRowVest actual = null;

        try {
            actual = interpreter.interpretRow(row);
        } catch (Exception e) {
            assertEquals("The number of vesting units must be a positive, non-zero integer",e.getMessage());
        }
        assertNull(actual);
    }


}