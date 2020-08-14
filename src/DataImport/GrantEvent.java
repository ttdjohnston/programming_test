package DataImport;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GrantEvent {
    DiDataRowType type;
    LocalDate date;
    BigDecimal price;
    BigDecimal units;
    BigDecimal perfMultiplier;

    public GrantEvent(DiDataRowType type, LocalDate date, BigDecimal price, Integer units, BigDecimal perfMultiplier) {
        this.type = type;
        this.date = date;
        this.price = price;
        if (units != null)
            this.units = new BigDecimal(units);
        this.perfMultiplier = perfMultiplier;
    }

    public DiDataRowType getType() {
        return type;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getUnits() { return units; }

    public BigDecimal getPerfMultiplier() { return perfMultiplier; }

    public void updateUnits(BigDecimal newUnits) { units = newUnits; }

    public static GrantEventBuilder newBuilder() {
        return new GrantEventBuilder();
    }



    public static class GrantEventBuilder {
        private DiDataRowType _type;
        private LocalDate _date;
        private BigDecimal _price;
        private Integer _units;
        private BigDecimal _perfMultiplier;

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
        public GrantEventBuilder setPrice(BigDecimal price) {
            _price = price;
            return this;
        }
        public GrantEventBuilder setUnits(Integer units) {
            _units = units;
            return this;
        }
        public GrantEventBuilder setPerfMultiplier(BigDecimal perfMultiplier) {
            _perfMultiplier = perfMultiplier;
            return this;
        }
    }
}
