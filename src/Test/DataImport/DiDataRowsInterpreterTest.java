package Test.DataImport;

import DataImport.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DiDataRowsInterpreterTest {
    List<String> input;
    DiDataRowsInterpreter validator;

    @BeforeEach
    public void setup() {
        input = new ArrayList<>();
        input.add("VEST,EE#1,20000121,2,5.21");
        validator = new DiDataRowsInterpreter(",");
    }

    @AfterEach
    public void teardown() {
        input = null;
        validator = null;
    }

    @Test
    public void validateRows_AllValidRows_ReturnsSuccess() {
        input.add("VEST,EE#2,20000122,3,5.23");
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
        assertEquals(Integer.valueOf(2), ((DiDataRowVest) actual.get(0)).getUnitsVested());
        assertEquals(Double.valueOf(5.21), ((DiDataRowVest) actual.get(0)).getGrantPrice());

        assertEquals(new DiDataRowType("VEST"), actual.get(1).getType());
        assertEquals("EE#2", actual.get(1).getEmpNum());
        assertEquals(2000, actual.get(1).getDate().getYear());
        assertEquals(Month.JANUARY, actual.get(1).getDate().getMonth());
        assertEquals(22, actual.get(1).getDate().getDayOfMonth());
        assertEquals(Integer.valueOf(3), ((DiDataRowVest) actual.get(1)).getUnitsVested());
        assertEquals(Double.valueOf(5.23), ((DiDataRowVest) actual.get(1)).getGrantPrice());

    }

    @Test
    public void validateRows_InvalidRow_FirstElementInvalid_ReturnsInvalidFileException_IncorrectlyFormatted() {
        input.add("NOTHING,EE#2,20000221,2,5.21");
        List<DiDataRow> actual = null;
        try {
            actual = validator.interpretRows(input);
        } catch (InvalidFileException e) {
            assertEquals("Data Row 2 is incorrectly formatted\n",e.getMessage());
        }
        assertNull(actual);
    }

    @Test
    public void validateRows_MultipleInvalidRows_FirstElementInvalid_ReturnsInvalidFileException_MessageContainsDataRow2and3() {
        input.add("VEST,EE#2,20000231,2,5.21");
        input.add("VEST,EE#1,20000221,2,ABC");
        List<DiDataRow> actual = null;
        try {
            actual = validator.interpretRows(input);
        } catch (InvalidFileException e) {
            boolean hasDataRow2 = e.getMessage().contains("Data Row 2:");
            assertTrue(hasDataRow2, "expected to contain \"Data Row 2\" but got " + e.getMessage());
            boolean hasDataRow3 = e.getMessage().contains("Data Row 3:");
            assertTrue(hasDataRow2, "expected to contain \"Data Row 3\" but got " + e.getMessage());
        }
        assertNull(actual);
    }
}