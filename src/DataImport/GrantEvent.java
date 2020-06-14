package DataImport;

import java.time.LocalDate;

public class GrantEvent {
    DiDataRowType type;
    LocalDate date;
    Double price;
    Integer units;

    public GrantEvent(DiDataRowType type, LocalDate date, Double price, Integer units) {
        this.type = type;
        this.date = date;
        this.price = price;
        this.units = units;
    }

    public DiDataRowType getType() {
        return type;
    }

    public LocalDate getDate() {
        return date;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getUnits() {
        return units;
    }
}
