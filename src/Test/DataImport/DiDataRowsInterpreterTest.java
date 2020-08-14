package Test.DataImport;

import DataImport.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DiDataRowsInterpreterTest {
    List<String> input;
    DiDataRowsInterpreter validator;

    @BeforeEach
    public void setup() {
        input = new ArrayList<>();
        validator = new DiDataRowsInterpreter(",");
    }

    @AfterEach
    public void teardown() {
        input = null;
        validator = null;
    }

    @Test
    public void validateRows_AllValidRows_ReturnsSuccess() {
        input.add("VEST,EE#1,20000121,2,5.21");
        input.add("PERF,EE#2,20000122,1.5");
        input.add("SALE,EE#3,20000223,4,5.24");
        List<DiDataRow> actual = null;
        try {
            actual = validator.interpretRows(input);
        } catch (InvalidFileException e) {
            fail(e.getMessage());
        }
        assertEquals(input.size(), actual.size());

        assertEquals(new DiDataRowType("VEST"), actual.get(0).getType());
        assertEquals("EE#1", actual.get(0).getEmpNum());
        assertEquals(2000, actual.get(0).getDate().getYear());
        assertEquals(Month.JANUARY, actual.get(0).getDate().getMonth());
        assertEquals(21, actual.get(0).getDate().getDayOfMonth());
        assertEquals(new Integer(2), (actual.get(0)).getUnits());
        assertEquals(5.21, (actual.get(0)).getGrantPrice().doubleValue());

        assertEquals(new DiDataRowType("PERF"), actual.get(1).getType());
        assertEquals("EE#2", actual.get(1).getEmpNum());
        assertEquals(2000, actual.get(1).getDate().getYear());
        assertEquals(Month.JANUARY, actual.get(1).getDate().getMonth());
        assertEquals(22, actual.get(1).getDate().getDayOfMonth());
        assertEquals(1.5, (actual.get(1)).getPerformanceMultiplier().doubleValue());

        assertEquals(new DiDataRowType("SALE"), actual.get(2).getType());
        assertEquals("EE#3", actual.get(2).getEmpNum());
        assertEquals(2000, actual.get(2).getDate().getYear());
        assertEquals(Month.FEBRUARY, actual.get(2).getDate().getMonth());
        assertEquals(23, actual.get(2).getDate().getDayOfMonth());
        assertEquals(new Integer(4), (actual.get(2)).getUnits());
        assertEquals(5.24, (actual.get(2)).getSalePrice().doubleValue());

    }

    @Test
    public void validateRows_InvalidRow_FirstElementInvalid_ReturnsInvalidFileException_IncorrectlyFormatted() {
        input.add("NOTHING,EE#2,20000221,2,5.21");
        List<DiDataRow> actual = null;
        try {
            actual = validator.interpretRows(input);
        } catch (InvalidFileException e) {
            assertEquals("Data Row 1 is incorrectly formatted\n",e.getMessage());
        }
        assertNull(actual);
    }

    @Test
    public void validateRows_MultipleInvalidRows_FirstElementInvalid_ReturnsInvalidFileException_MessageContainsDataRow1and2() {
        input.add("VEST,EE#2,20000231,2,5.21");
        input.add("VEST,EE#1,20000221,2,ABC");
        List<DiDataRow> actual = null;
        try {
            actual = validator.interpretRows(input);
        } catch (InvalidFileException e) {
            boolean hasDataRow1 = e.getMessage().contains("Data Row 1:");
            assertTrue(hasDataRow1, "expected to contain \"Data Row 1\" but got " + e.getMessage());
            boolean hasDataRow2 = e.getMessage().contains("Data Row 2:");
            assertTrue(hasDataRow2, "expected to contain \"Data Row 2\" but got " + e.getMessage());
        }
        assertNull(actual);
    }

    @Test
    void validateRows_BlankEmplNum_Exception() {
        List<String> input = new ArrayList<>();
        input.add("VEST,,20000121,2,0.01");
        input.add("PERF,,20000121,5.21");
        input.add("SALE,,20000121,2,5.21");

        List<DiDataRow> actual = null;

        try {
            actual = validator.interpretRows(input);
        } catch (Exception e) {
            assertEquals("Data Row 1: The Employee Number must not be blank\n" +
                    "Data Row 2: The Employee Number must not be blank\n" +
                    "Data Row 3: The Employee Number must not be blank\n",
                    e.getMessage());
        }
        assertNull(actual);
    }

    @Test
    void validateRows_InvalidDate_NonDigitCharacter_Exception() {
        List<String> input = new ArrayList<>();
        input.add("VEST,EE#1,2000&121,2,0.01");
        input.add("PERF,EE#1,2000&121,5.21");
        input.add("SALE,EE#1,2000&121,2,5.21");

        List<DiDataRow> actual = null;

        try {
            actual = validator.interpretRows(input);
        } catch (Exception e) {
            assertEquals("Data Row 1: The vest date is invalid and must be in the form yyyyMMdd\n" +
                            "Data Row 2: The vest date is invalid and must be in the form yyyyMMdd\n" +
                            "Data Row 3: The sale date is invalid and must be in the form yyyyMMdd\n",
                            e.getMessage());
        }
        assertNull(actual);
    }

    @Test
    void validateRows_TooManyCharactersInDate_Exception() {
        List<String> input = new ArrayList<>();
        input.add("VEST,EE#1,200001211,2,0.01");
        input.add("PERF,EE#1,200001211,5.21");
        input.add("SALE,EE#1,200001211,2,5.21");
        List<DiDataRow> actual = null;

        try {
            actual = validator.interpretRows(input);
        } catch (Exception e) {
            assertEquals("Data Row 1: The vest date is invalid and must be in the form yyyyMMdd\n" +
                            "Data Row 2: The vest date is invalid and must be in the form yyyyMMdd\n" +
                            "Data Row 3: The sale date is invalid and must be in the form yyyyMMdd\n",
                    e.getMessage());
        }
        assertNull(actual);

    }

    @Test
    void validateRows_InvalidLeapDay_Exception() {
        List<String> input = new ArrayList<>();
        input.add("VEST,EE#1,20010229,2,0.01");
        input.add("PERF,EE#1,20010229,5.21");
        input.add("SALE,EE#1,20010229,2,5.21");
        List<DiDataRow> actual = null;

        try {
            actual = validator.interpretRows(input);
        } catch (Exception e) {
            assertEquals("Data Row 1: The vest date is invalid and must be in the form yyyyMMdd\n" +
                            "Data Row 2: The vest date is invalid and must be in the form yyyyMMdd\n" +
                            "Data Row 3: The sale date is invalid and must be in the form yyyyMMdd\n",
                    e.getMessage());
        }
        assertNull(actual);
    }

    @Test
    void validateRows_NondigitVestingUnits_Exception() {
        List<String> input = new ArrayList<>();
        input.add("VEST,EE#1,20000121,%,5.21");
        input.add("SALE,EE#1,20000121,%,5.21");
        List<DiDataRow> actual = null;

        try {
            actual = validator.interpretRows(input);
        } catch (Exception e) {
            assertEquals("Data Row 1: The number of vesting units must be a positive, non-zero integer\n" +
                    "Data Row 2: The number of units being sold must be a positive, non-zero integer\n"
                    ,e.getMessage());
        }
        assertNull(actual);
    }

    @Test
    void validateRows_NegativeVestingUnits_Exception() {
        List<String> input = new ArrayList<>();
        input.add("VEST,EE#1,20000121,-2,5.21");
        input.add("SALE,EE#1,20000121,-1,5.21");
        List<DiDataRow> actual = null;

        try {
            actual = validator.interpretRows(input);
        } catch (Exception e) {
            assertEquals("Data Row 1: The number of vesting units must be a positive, non-zero integer\n" +
                    "Data Row 2: The number of units being sold must be a positive, non-zero integer\n",e.getMessage());
        }
        assertNull(actual);
    }

    @Test
    void validateRows_ZeroVestingUnits_Exception() {
        List<String> input = new ArrayList<>();
        input.add("VEST,EE#1,20000121,0,5.21");
        input.add("SALE,EE#1,20000121,0,5.21");
        List<DiDataRow> actual = null;

        try {
            actual = validator.interpretRows(input);
        } catch (Exception e) {
            assertEquals("Data Row 1: The number of vesting units must be a positive, non-zero integer\n" +
                    "Data Row 2: The number of units being sold must be a positive, non-zero integer\n",e.getMessage());
        }
        assertNull(actual);
    }

    @Test
    void validateRows_NegativeGrantPrice_Exception() {
        String rowString = "VEST,EE#1,20000121,2,-5.21";
        List<String> row = Arrays.asList(rowString);
        List<DiDataRow> actual = null;

        try {
            actual = validator.interpretRows(row);
        } catch (Exception e) {
            assertEquals("Data Row 1: The grant price must be a positive decimal (no symbols)\n",e.getMessage());
        }
        assertNull(actual);
    }

    @Test
    void validateRows_NegativeSalePrice_Exception() {
        String rowString = "SALE,EE#1,20000121,2,-5.21";
        List<String> row = Arrays.asList(rowString);
        List<DiDataRow> actual = null;

        try {
            actual = validator.interpretRows(row);
        } catch (Exception e) {
            assertEquals("Data Row 1: The sale price must be a positive, non-zero decimal (no symbols)\n",e.getMessage());
        }
        assertNull(actual);
    }

    @Test
    void validateRows_ZeroGrantPrice_Valid() {
        String rowString = "VEST,EE#1,20000121,2,0.00";
        List<String> row = Arrays.asList(rowString);
        List<DiDataRow> expected = Arrays.asList(DiDataRow.newBuilder().setType(new DiDataRowType("VEST"))
                                                                        .setEmpNum("EE#1")
                                                                        .setDate(LocalDate.of(2000,1,21))
                                                                        .setUnits(2)
                                                                        .setGrantPrice(BigDecimal.ZERO)
                                                                        .build());
        List<DiDataRow> actual = null;

        try {
            actual = validator.interpretRows(row);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        assertTrue(actual.get(0).getType().isVest());
        assertEquals("EE#1", actual.get(0).getEmpNum());
        assertEquals(2000, actual.get(0).getDate().getYear());
        assertEquals(Month.JANUARY, actual.get(0).getDate().getMonth());
        assertEquals(21, actual.get(0).getDate().getDayOfMonth());
        assertEquals(2.0, actual.get(0).getUnits().doubleValue());
        assertEquals(0.0, actual.get(0).getGrantPrice().doubleValue());
    }

    @Test
    void validateRows_ZeroSalePrice_Exception() {
        String rowString = "SALE,EE#1,20000121,2,0.0";
        List<String> row = Arrays.asList(rowString);
        List<DiDataRow> actual = null;

        try {
            actual = validator.interpretRows(row);
        } catch (Exception e) {
            assertEquals("Data Row 1: The sale price must be a positive, non-zero decimal (no symbols)\n",e.getMessage());
        }
        assertNull(actual);
    }

    @Test
    void validateRows_GrantPriceWithDollarSign_Exception() {
        String rowString = "VEST,EE#1,20000121,10,$5.21";
        List<String> row = Arrays.asList(rowString);
        List<DiDataRow> actual = null;

        try {
            actual = validator.interpretRows(row);
        } catch (Exception e) {
            assertEquals("Data Row 1: The grant price must be a positive decimal (no symbols)\n",e.getMessage());
        }
        assertNull(actual);
    }

    @Test
    void validateRows_SalePriceWithDollarSign_Exception() {
        String rowString = "SALE,EE#1,20000121,10,$5.21";
        List<String> row = Arrays.asList(rowString);
        List<DiDataRow> actual = null;

        try {
            actual = validator.interpretRows(row);
        } catch (Exception e) {
            assertEquals("Data Row 1: The sale price must be a positive, non-zero decimal (no symbols)\n",e.getMessage());
        }
        assertNull(actual);
    }
}