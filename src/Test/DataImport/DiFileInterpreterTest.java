package Test.DataImport;

import DataImport.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DiFileInterpreterTest {

    public DiFileInterpreter interpreter;



    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        interpreter = new DiFileInterpreter();
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        interpreter = null;
    }

    @Test
    public void interpretFile_AllValid_DiFileConstructedCorrectly() {
        List<String> importedFile = new ArrayList<>();
        importedFile.add("4");
        importedFile.add("VEST,EE#1,20120101,1231,1.17");
        importedFile.add("VEST,EE#1,20120201,987,1.67");
        importedFile.add("VEST,EE#2,20120307,100,2.81");
        importedFile.add("VEST,EE#3,20120101,10000,0.01");
        importedFile.add("20120401,5.57");

        List<DiDataRow> rows = new ArrayList<>();
        rows.add(new DiDataRowVest(new DiDataRowType("VEST"), "EE#1", LocalDate.of(2012,01,01), Integer.valueOf(1231), Double.valueOf(1.17)));
        rows.add(new DiDataRowVest(new DiDataRowType("VEST"), "EE#1", LocalDate.of(2012,02,01), Integer.valueOf(987), Double.valueOf(1.67)));
        rows.add(new DiDataRowVest(new DiDataRowType("VEST"), "EE#2", LocalDate.of(2012,03,07), Integer.valueOf(100), Double.valueOf(2.81)));
        rows.add(new DiDataRowVest(new DiDataRowType("VEST"), "EE#3", LocalDate.of(2012,01,01), Integer.valueOf(10000), Double.valueOf(0.01)));
        DiFile expected = new DiFile(Integer.valueOf(4), rows, new FileFooter(LocalDate.of(2012,04,01), Double.valueOf(5.57)));

        DiFile actual = null;
        try {
            actual = interpreter.interpretFile(importedFile);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertTrue(expected.getNumberOfDiRows().equals(actual.getNumberOfDiRows()), "Expected: " + expected.getNumberOfDiRows() + ", Actual: " + expected.getNumberOfDiRows());
        assertEquals(expected.getDataRows().size(), actual.getDataRows().size());
        assertTrue(expected.getDataRows().containsAll(actual.getDataRows()));
        assertTrue(expected.getFooter().equals(actual.getFooter()), "Expected: " + expected.getFooter().toString() + "\n Actual: " + expected.getFooter().toString());
    }

    @Test
    public void interpretFile_NoRows_InvalidFileException() {
        List<String> importedFile = new ArrayList<>();
        importedFile.add("0");
        importedFile.add("20120401,5.57");

        DiFile actual = null;
        try {
            actual = interpreter.interpretFile(importedFile);
        } catch (InvalidFileException e) {
            assertTrue(e.getMessage().contains("An import file must have the following structure"),
                    "Expected the error message to contain \"An import file must have the following structure\" but was " + e.getMessage());
        }
        assertNull(actual);
    }

    @Test
    public void interpretFile_HeaderIsNotNumber_InvalidFileException() {
        List<String> importedFile = new ArrayList<>();
        importedFile.add("A");
        importedFile.add("VEST,EE#1,20120101,1231,1.17");
        importedFile.add("20120401,5.57");

        DiFile actual = null;
        try {
            actual = interpreter.interpretFile(importedFile);
        } catch (InvalidFileException e) {
            assertTrue(e.getMessage().contains("The header must contain a single integer"),
                    "Expected the error message to contain \"The header must contain a single integer\" but was " + e.getMessage());
        }
        assertNull(actual);
    }

    @Test
    public void interpretFile_HeaderDoesNotMatchNumberOfRows_InvalidFileException() {
        List<String> importedFile = new ArrayList<>();
        importedFile.add("2");
        importedFile.add("VEST,EE#1,20120101,1231,1.17");
        importedFile.add("20120401,5.57");

        DiFile actual = null;
        try {
            actual = interpreter.interpretFile(importedFile);
        } catch (InvalidFileException e) {
            assertTrue(e.getMessage().contains("The header must contain a single integer with the number of data rows to follow"),
                    "Expected the error message to contain \"The header must contain a single integer with the number of data rows to follow\" but was " + e.getMessage());
        }
        assertNull(actual);
    }

    @Test
    public void interpretFile_FooterNotEnoughElements_InvalidFileException() {
        List<String> importedFile = new ArrayList<>();
        importedFile.add("1");
        importedFile.add("VEST,EE#1,20120101,1231,1.17");
        importedFile.add("20120401 5.57");

        DiFile actual = null;
        try {
            actual = interpreter.interpretFile(importedFile);
        } catch (InvalidFileException e) {
            assertTrue(e.getMessage().contains("The footer must contain a date in the form"),
                    "Expected the error message to contain \"The footer must contain a date in the form\" but was " + e.getMessage());
        }
        assertNull(actual);
    }

    @Test
    public void interpretFile_FooterInvalidDateFormat_InvalidFileException() {
        List<String> importedFile = new ArrayList<>();
        importedFile.add("1");
        importedFile.add("VEST,EE#1,20120101,1231,1.17");
        importedFile.add("2012-04-01,5.57");

        DiFile actual = null;
        try {
            actual = interpreter.interpretFile(importedFile);
        } catch (InvalidFileException e) {
            assertTrue(e.getMessage().contains("The footer must contain"),
                    "Expected the error message to contain \"The footer must contain\" but was " + e.getMessage());
        }
        assertNull(actual);
    }

    @Test
    public void interpretFile_FooterInvalidPriceFormat_InvalidFileException() {
        List<String> importedFile = new ArrayList<>();
        importedFile.add("1");
        importedFile.add("VEST,EE#1,20120101,1231,1.17");
        importedFile.add("20120401,$5.57");

        DiFile actual = null;
        try {
            actual = interpreter.interpretFile(importedFile);
        } catch (InvalidFileException e) {
            assertTrue(e.getMessage().contains("The footer must contain"),
                    "Expected the error message to contain \"The footer must contain\" but was " + e.getMessage());
        }
        assertNull(actual);
    }

    @Test
    public void interpretFile_FooterNegativePrice_InvalidFileException() {
        List<String> importedFile = new ArrayList<>();
        importedFile.add("1");
        importedFile.add("VEST,EE#1,20120101,1231,1.17");
        importedFile.add("20120401,-5.57");

        DiFile actual = null;
        try {
            actual = interpreter.interpretFile(importedFile);
        } catch (InvalidFileException e) {
            assertTrue(e.getMessage().contains("The footer must contain"),
                    "Expected the error message to contain \"The footer must contain\" but was " + e.getMessage());
        }
        assertNull(actual);
    }
}
