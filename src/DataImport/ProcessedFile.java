package DataImport;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.PriorityQueue;

public class ProcessedFile {
    Map<String, PriorityQueue<GrantEvent>> _employeeGrantEvents;
    LocalDate _footerDate;
    BigDecimal _footerPrice;

    public ProcessedFile(Map<String, PriorityQueue<GrantEvent>> employeeGrantEvents, LocalDate date, BigDecimal price){
        _employeeGrantEvents = employeeGrantEvents;
        _footerDate = date;
        _footerPrice = price;
    }

    public Map<String, PriorityQueue<GrantEvent>> getEmployeeGrantEvents() {
        return _employeeGrantEvents;
    }

    public LocalDate getFooterDate() {
        return _footerDate;
    }

    public BigDecimal getFooterPrice() {
        return _footerPrice;
    }
}
