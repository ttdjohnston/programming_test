package Test.SaleModeller;

import DataImport.DiDataRowType;
import DataImport.GrantEvent;
import DataImport.ProcessedFile;
import SaleModeller.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SaleModellerTest {
    final String ee1Name = "ee1";
    final String ee2Name = "ee2";
    final Comparator<GrantEvent> eventComparator = new Comparator<GrantEvent>() {
        @Override
        public int compare(GrantEvent o1, GrantEvent o2) {
            if (o1.getDate().compareTo(o2.getDate()) == 0) {
                return o1.getType().compareTo(o2.getType());
            }
            return o1.getDate().compareTo(o2.getDate());
        }
    };
    ProcessedFile file;
    LocalDate modellingDate;
    BigDecimal modellingPrice;
    SaleModeller saleModeller;



    @BeforeEach
    public void setup() {
        Map<String, PriorityQueue<GrantEvent>> eeGrantEvents = new HashMap<>();
        eeGrantEvents.put(ee1Name, new PriorityQueue<>(eventComparator));
        modellingDate = LocalDate.of(2015,06,17);
        modellingPrice = new BigDecimal(1.23);
        file = new ProcessedFile(eeGrantEvents, modellingDate, modellingPrice);
        saleModeller = new SaleModeller();
    }

    @AfterEach
    public void teardown() {
        modellingPrice = null;
        modellingDate = null;
        file = null;
        saleModeller = null;
    }

    @Test
    public void modelSaleFromFile_vestEventsOnly_modelsCorrectly(){
        //setup
        PriorityQueue<GrantEvent> events = file.getEmployeeGrantEvents().get(ee1Name);
        addVestEventToQueue(events,2014,6,19,0.55,150);
        addVestEventToQueue(events,2015,1,20,0.45,120);
        addVestEventToQueue(events,2016,2,21,0.68,200);
        events = addEmployeeToFileAndGetEvents(ee2Name, file);
        addVestEventToQueue(events,2015,6,15,0.55,150);
        addVestEventToQueue(events,2016,1,20,0.45,120);
        addVestEventToQueue(events,2016,2,23,0.68,200);

        Map<String, EmployeeModelResult> result = null;
        try {
            result = saleModeller.modelSaleFromFile(file);
        } catch (GrantEventException e) {
            fail(e.toString());
        }
        if (result == null) {
            fail("result was null");
        }
        assertEquals(ee1Name, result.get(ee1Name).getEmployeeNumber());
        assertEquals(BigDecimal.valueOf(195.60).setScale(2), result.get(ee1Name).getTotalGainAvailable());
        assertEquals(BigDecimal.ZERO.setScale(2), result.get(ee1Name).getTotalGainFromSale());
        assertEquals(ee2Name, result.get(ee2Name).getEmployeeNumber());
        assertEquals(BigDecimal.valueOf(102.00).setScale(2), result.get(ee2Name).getTotalGainAvailable());
        assertEquals(BigDecimal.ZERO.setScale(2), result.get(ee2Name).getTotalGainFromSale());
        assertEquals(2, result.size());
    }

    @Test
    public void modelSaleFromFile_vestEventsOnly_vestUnderWater_availableValueIsZero(){
        //setup
        PriorityQueue<GrantEvent> events = file.getEmployeeGrantEvents().get(ee1Name);
        addVestEventToQueue(events,2014,6,19,2.55,150);

        Map<String, EmployeeModelResult> result = null;
        try {
            result = saleModeller.modelSaleFromFile(file);
        } catch (GrantEventException e) {
            fail(e.toString());
        }
        if (result == null) {
            fail("result was null");
        }
        assertEquals(ee1Name, result.get(ee1Name).getEmployeeNumber());
        assertEquals(BigDecimal.ZERO.setScale(2), result.get(ee1Name).getTotalGainAvailable());
        assertEquals(BigDecimal.ZERO.setScale(2), result.get(ee1Name).getTotalGainFromSale());
        assertEquals(1, result.size());
    }

    @Test
    public void modelSaleFromFile_vestAndPerfEventsOnly_modelsCorrectly(){
        //setup
        PriorityQueue<GrantEvent> events = file.getEmployeeGrantEvents().get(ee1Name);
        addVestEventToQueue(events,2014,6,19,0.55,150);
        addPerfEventToQueue(events,2014,6,19,1.5);
        events = addEmployeeToFileAndGetEvents(ee2Name, file);
        addVestEventToQueue(events,2014,6,19,0.55,150);
        addPerfEventToQueue(events,2015,6,19,1.5);

        Map<String, EmployeeModelResult> result = null;
        try {
            result = saleModeller.modelSaleFromFile(file);
        } catch (GrantEventException e) {
            fail(e.toString());
        }
        if (result == null) {
            fail("result was null");
        }
        assertEquals(ee1Name, result.get(ee1Name).getEmployeeNumber());
        assertEquals(BigDecimal.valueOf(153.00).setScale(2), result.get(ee1Name).getTotalGainAvailable());
        assertEquals(BigDecimal.ZERO.setScale(2), result.get(ee1Name).getTotalGainFromSale());
        assertEquals(ee2Name, result.get(ee2Name).getEmployeeNumber());
        assertEquals(BigDecimal.valueOf(102.00).setScale(2), result.get(ee2Name).getTotalGainAvailable());
        assertEquals(BigDecimal.ZERO.setScale(2), result.get(ee2Name).getTotalGainFromSale());
        assertEquals(2, result.size());
    }

    @Test
    public void modelSaleFromFile_vestAndPerfEventsOnly_perfBeforeVest_availableValueUnchanged(){
        //setup
        PriorityQueue<GrantEvent> events = file.getEmployeeGrantEvents().get(ee1Name);
        addPerfEventToQueue(events,2014,6,18,1.5);
        addVestEventToQueue(events,2014,6,19,0.55,150);

        Map<String, EmployeeModelResult> result = null;
        try {
            result = saleModeller.modelSaleFromFile(file);
        } catch (GrantEventException e) {
            fail(e.toString());
        }
        if (result == null) {
            fail("result was null");
        }
        assertEquals(ee1Name, result.get(ee1Name).getEmployeeNumber());
        assertEquals(BigDecimal.valueOf(102.00).setScale(2), result.get(ee1Name).getTotalGainAvailable());
        assertEquals(BigDecimal.ZERO.setScale(2), result.get(ee1Name).getTotalGainFromSale());
        assertEquals(1, result.size());
    }

    @Test
    public void modelSaleFromFile_vestAndSaleEventsOnly_modelsCorrectly(){
        //setup
        PriorityQueue<GrantEvent> events = file.getEmployeeGrantEvents().get(ee1Name);
        addVestEventToQueue(events,2014,6,19,0.55,150);
        addSaleEventToQueue(events,2014,6,20,0.65, 70);

        Map<String, EmployeeModelResult> result = null;
        try {
            result = saleModeller.modelSaleFromFile(file);
        } catch (GrantEventException e) {
            fail(e.toString());
        }
        if (result == null) {
            fail("result was null");
        }
        assertEquals(ee1Name, result.get(ee1Name).getEmployeeNumber());
        assertEquals(BigDecimal.valueOf(54.40).setScale(2), result.get(ee1Name).getTotalGainAvailable());
        assertEquals(BigDecimal.valueOf(7.00).setScale(2), result.get(ee1Name).getTotalGainFromSale());
        assertEquals(1, result.size());
    }

    @Test
    public void modelSaleFromFile_vestAndSaleEventsOnly_SaleBeforeVest_Exception(){
        //setup
        PriorityQueue<GrantEvent> events = file.getEmployeeGrantEvents().get(ee1Name);
        addSaleEventToQueue(events,2014,6,15,0.65, 70);
        addVestEventToQueue(events,2014,6,19,0.55,150);


        Map<String, EmployeeModelResult> result = null;
        try {
            result = saleModeller.modelSaleFromFile(file);
        } catch (GrantEventException e) {
            assertEquals("Unable to process sale.  Not enough vested shares available", e.getMessage());

        }
        if (result != null) {
            fail("Did not trigger GrantEventException");
        }
    }

    @Test
    public void modelSaleFromFile_3VestsAnd1SaleEventOnly_SellsLeastExpensiveVestBeforeSaleDate(){
        //setup
        PriorityQueue<GrantEvent> events = file.getEmployeeGrantEvents().get(ee1Name);
        addVestEventToQueue(events,2014,5,18,0.50,120);
        addVestEventToQueue(events,2014,6,19,0.55,150);
        addSaleEventToQueue(events,2014,6,20,1.5, 70);
        addVestEventToQueue(events,2014,7,23,0.1,80);

        Map<String, EmployeeModelResult> result = null;
        try {
            result = saleModeller.modelSaleFromFile(file);
        } catch (GrantEventException e) {
            fail(e.toString());
        }
        if (result == null) {
            fail("result was null");
        }
        assertEquals(ee1Name, result.get(ee1Name).getEmployeeNumber());
        assertEquals(BigDecimal.valueOf(228.90).setScale(2), result.get(ee1Name).getTotalGainAvailable());
        assertEquals(BigDecimal.valueOf(70.00).setScale(2), result.get(ee1Name).getTotalGainFromSale());
        assertEquals(1, result.size());
    }

    @Test
    public void modelSaleFromFile_3VestsAnd1SaleEventOnly_SellsMostExpensiveVestBeforeSaleDate(){
        //setup
        PriorityQueue<GrantEvent> events = file.getEmployeeGrantEvents().get(ee1Name);
        addVestEventToQueue(events,2014,5,18,0.50,120);
        addVestEventToQueue(events,2014,6,19,0.55,150);
        addSaleEventToQueue(events,2014,6,20,1.05, 70);
        addVestEventToQueue(events,2014,7,23,0.1,80);

        Map<String, EmployeeModelResult> result = null;
        try {
            result = saleModeller.modelSaleFromFile(file);
        } catch (GrantEventException e) {
            fail(e.toString());
        }
        if (result == null) {
            fail("result was null");
        }
        assertEquals(ee1Name, result.get(ee1Name).getEmployeeNumber());
        assertEquals(BigDecimal.valueOf(232.40).setScale(2), result.get(ee1Name).getTotalGainAvailable());
        assertEquals(BigDecimal.valueOf(35.00).setScale(2), result.get(ee1Name).getTotalGainFromSale());
        assertEquals(1, result.size());
    }

    @Test
    public void modelSaleFromFile_3VestsAnd1SaleEvent_SellDrawsFromMultipleVests(){
        //setup
        PriorityQueue<GrantEvent> events = file.getEmployeeGrantEvents().get(ee1Name);
        addVestEventToQueue(events,2014,5,18,0.50,30);
        addVestEventToQueue(events,2014,6,19,0.55,20);
        addVestEventToQueue(events,2014,6,23,0.1,20);
        addSaleEventToQueue(events,2014,7,20,1.05, 70);

        Map<String, EmployeeModelResult> result = null;
        try {
            result = saleModeller.modelSaleFromFile(file);
        } catch (GrantEventException e) {
            fail(e.toString());
        }
        if (result == null) {
            fail("result was null");
        }
        assertEquals(ee1Name, result.get(ee1Name).getEmployeeNumber());
        assertEquals(BigDecimal.ZERO.setScale(2), result.get(ee1Name).getTotalGainAvailable());
        assertEquals(BigDecimal.valueOf(45.50).setScale(2), result.get(ee1Name).getTotalGainFromSale());
        assertEquals(1, result.size());
    }

    @Test
    public void modelSaleFromFile_3Vests1PerfAnd1SaleEvent_PerfCausesSellMostExpensiveVestBeforeSaleDate(){
        //setup
        PriorityQueue<GrantEvent> events = file.getEmployeeGrantEvents().get(ee1Name);
        addVestEventToQueue(events,2014,5,18,0.50,120);
        addVestEventToQueue(events,2014,6,19,0.55,150);
        addSaleEventToQueue(events,2014,6,20,1.5, 70);
        addVestEventToQueue(events,2014,7,23,0.1,80);
        addPerfEventToQueue(events, 2014, 7, 24, 2.0);

        Map<String, EmployeeModelResult> result = null;
        try {
            result = saleModeller.modelSaleFromFile(file);
        } catch (GrantEventException e) {
            fail(e.toString());
        }
        if (result == null) {
            fail("result was null");
        }
        assertEquals(ee1Name, result.get(ee1Name).getEmployeeNumber());
        assertEquals(BigDecimal.valueOf(464.80).setScale(2), result.get(ee1Name).getTotalGainAvailable());
        assertEquals(BigDecimal.valueOf(66.50).setScale(2), result.get(ee1Name).getTotalGainFromSale());
        assertEquals(1, result.size());
    }

    private PriorityQueue<GrantEvent> addEmployeeToFileAndGetEvents(String employeeNumber, ProcessedFile processedFile) {
        PriorityQueue<GrantEvent> queue = new PriorityQueue<>(eventComparator);
        processedFile.getEmployeeGrantEvents().put(employeeNumber, queue);
        return queue;
    }

    private void addVestEventToQueue(PriorityQueue<GrantEvent> events, int year, int month, int day, double price, int units) {
        events.add(GrantEvent.newBuilder()
                .setType(new DiDataRowType("VEST"))
                .setPrice(BigDecimal.valueOf(price))
                .setDate(LocalDate.of(year,month,day))
                .setUnits(units)
                .build());
    }

    private void addPerfEventToQueue(PriorityQueue<GrantEvent> events, int year, int month, int day, double multiplier) {
        events.add(GrantEvent.newBuilder()
                .setType(new DiDataRowType("PERF"))
                .setPerfMultiplier(BigDecimal.valueOf(multiplier))
                .setDate(LocalDate.of(year,month,day))
                .build());
    }

    private void addSaleEventToQueue(PriorityQueue<GrantEvent> events, int year, int month, int day, double price, int units) {
        events.add(GrantEvent.newBuilder()
                .setType(new DiDataRowType("SALE"))
                .setPrice(BigDecimal.valueOf(price))
                .setUnits(units)
                .setDate(LocalDate.of(year,month,day))
                .build());
    }
}