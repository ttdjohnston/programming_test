package Test.DataImport;

import DataImport.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
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
        importedFile.add("PERF,EE#1,20120201,1.67");
        importedFile.add("SALE,EE#1,20120307,100,2.81");
        importedFile.add("VEST,EE#2,20120101,10000,0.01");
        importedFile.add("20120401,5.57");

        List<DiDataRow> rows = new ArrayList<>();
        rows.add(DiDataRow.newBuilder().setType(new DiDataRowType("VEST")).setEmpNum("EE#1").setDate(LocalDate.of(2012,1,1)).setUnits(1231).setGrantPrice(BigDecimal.valueOf(1.17)).build());
        rows.add(DiDataRow.newBuilder().setType(new DiDataRowType("PERF")).setEmpNum("EE#1").setDate(LocalDate.of(2012,2,1)).setPerformanceMultiplier(BigDecimal.valueOf(1.67)).build());
        rows.add(DiDataRow.newBuilder().setType(new DiDataRowType("SALE")).setEmpNum("EE#1").setDate(LocalDate.of(2012,3,1)).setUnits(100).setSalePrice(BigDecimal.valueOf(2.81)).build());
        rows.add(DiDataRow.newBuilder().setType(new DiDataRowType("VEST")).setEmpNum("EE#2").setDate(LocalDate.of(2012,1,1)).setUnits(10000).setGrantPrice(BigDecimal.valueOf(0.01)).build());
        DiFile expected = new DiFile(4, rows, new FileFooter(LocalDate.of(2012,4,1), BigDecimal.valueOf(5.57)));

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
