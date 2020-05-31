package Test.DataImport;

import DataImport.DiDataRowValidator;
import DataImport.InvalidFileException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DiDataRowValidatorTest {
    List<String> input;
    DiDataRowValidator validator;

    @BeforeEach
    public void setup() {
        input = new ArrayList<>();
        input.add("VEST,EE#1,20000121,2,5.21");
        validator = new DiDataRowValidator();
    }

    @AfterEach
    public void teardown() {
        input = null;
        validator = null;
    }

    @Test
    public void validateRows_AllValidRows_ReturnsSuccess() {
        input.add("VEST,EE#1,20000122,3,5.23");
        boolean actual = false;
        try {
            actual = validator.validateRows(input);
        } catch (InvalidFileException e) {
            fail(e.getMessage());
        }
        assertTrue(actual);
    }

    @Test
    public void validateRows_InvalidRow_FirstElementInvalid_ReturnsInvalidFileException_IncorrectlyFormatted() {
        input.add("NOTHING,EE#1,20000221,2,5.21");
        boolean actual = false;
        try {
            actual = validator.validateRows(input);
        } catch (InvalidFileException e) {
            assertEquals("Data Row 2 is incorrectly formatted\n",e.getMessage());
        }
        assertFalse(actual);
    }

    @Test
    public void validateRows_MultipleInvalidRows_FirstElementInvalid_ReturnsInvalidFileException_MessageContainsDataRow2and3() {
        input.add("VEST,EE#1,20000231,2,5.21");
        input.add("VEST,EE#1,20000221,2,ABC");
        boolean actual = false;
        try {
            actual = validator.validateRows(input);
        } catch (InvalidFileException e) {
            boolean hasDataRow2 = e.getMessage().contains("Data Row 2:");
            assertTrue(hasDataRow2, "expected to contain \"Data Row 2\" but got " + e.getMessage());
            boolean hasDataRow3 = e.getMessage().contains("Data Row 3:");
            assertTrue(hasDataRow2, "expected to contain \"Data Row 3\" but got " + e.getMessage());
        }
        assertFalse(actual);
    }
}