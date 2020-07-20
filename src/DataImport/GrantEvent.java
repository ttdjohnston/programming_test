package DataImport;

import java.time.LocalDate;

public class GrantEvent {
    DiDataRowType type;
    LocalDate date;
    Double price;
    Double units;
    Double perfMultiplier;

    public GrantEvent(DiDataRowType type, LocalDate date, Double price, Integer units, Double perfMultiplier) {
        this.type = type;
        this.date = date;
        this.price = price;
        this.units = new Double(units);
        this.perfMultiplier = perfMultiplier;
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

    public Double getUnits() { return units; }

    public Double getPerfMultiplier() { return perfMultiplier; }

    public void updateUnits(Double newUnits) { units = newUnits; }

    public static GrantEventBuilder newBuilder() {
        return new GrantEventBuilder();
    }



    public static class GrantEventBuilder {
        private DiDataRowType _type;
        private LocalDate _date;
        private Double _price;
        private Integer _units;
        private Double _perfMultiplier;

        private GrantEventBuilder() {
            _type = null;
            _date = null;
            _price = null;
            _units = null;
            _perfMultiplier = null;
        }

        public GrantEvent build() {
            return new GrantEvent(_type, _date, _price, _units, _perfMultiplier);
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

        public GrantEventBuilder setPerfMultiplier(Double perfMultiplier) {
            _perfMultiplier = perfMultiplier;
            return this;
        }

    }
}
