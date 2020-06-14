package DataImport;

import java.time.LocalDate;

public class GrantEventBuilder {
    private DiDataRowType _type;
    private LocalDate _date;
    private Double _price;
    private Integer _units;

    private GrantEventBuilder() {}

    public static GrantEventBuilder getBuilder() {  return new GrantEventBuilder();    }
    public GrantEvent build() {
        return new GrantEvent(_type, _date, _price, _units);
    }
    public GrantEventBuilder setType(DiDataRowType type) {
        _type = type;
        return this;
    }
    public GrantEventBuilder setDate(LocalDate date) {
        _date = date;
        return this;
    }
    public GrantEventBuilder setPrice(Double price) {
        _price = price;
        return this;
    }
    public GrantEventBuilder setUnits(Integer units) {
        _units = units;
        return this;
    }

}
