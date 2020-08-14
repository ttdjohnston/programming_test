package DataImport;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DiDataRow {
    private DiDataRowType _type;
    private String _empNum;
    private LocalDate _date;
    private Integer _units;
    private BigDecimal _grantPrice;
    private BigDecimal _performanceMultiplier;
    private BigDecimal _salePrice;

    private DiDataRow() {}

    private DiDataRow(DiDataRowType type, String empNum, LocalDate date, Integer units, BigDecimal grantPrice, BigDecimal performanceMultiplier, BigDecimal salePrice) {
        _type = type;
        _empNum = empNum;
        _date = date;
        _units = units;
        _grantPrice = grantPrice;
        _salePrice = salePrice;
        _performanceMultiplier = performanceMultiplier;

    }

    public DiDataRowType getType() { return _type; }
    public String getEmpNum() { return _empNum; }
    public LocalDate getDate() { return _date; }
    public Integer getUnits() {        return _units;    }
    public BigDecimal getGrantPrice() {        return _grantPrice;    }
    public BigDecimal getSalePrice() {        return _salePrice;    }
    public BigDecimal getPerformanceMultiplier() { return _performanceMultiplier; }

    public static DiDataRowBuilder newBuilder() {
        return new DiDataRowBuilder();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DiDataRow) {
            DiDataRow castedObj = (DiDataRow) obj;
            return _type.equals(castedObj.getType())
                    && _empNum.equals(castedObj.getEmpNum())
                    && _date.equals(castedObj.getDate())
                    && ((_units == castedObj.getUnits())
                        || (_units != null && _units.equals(castedObj.getUnits())))
                    && (_grantPrice == castedObj.getGrantPrice())
                        || (_grantPrice != null && _grantPrice.equals(castedObj.getGrantPrice()))
                    && (_salePrice == castedObj.getSalePrice())
                        || (_salePrice != null && _salePrice.equals(castedObj.getSalePrice()))
                    && (_performanceMultiplier == castedObj.getPerformanceMultiplier())
                        || (_performanceMultiplier != null && _performanceMultiplier.equals(castedObj.getPerformanceMultiplier()));
        }
        else
            return false;
    }

    @Override
    public String toString() {
        return "Type: " + _type.toString()
                + "\nEmpNum: " + _empNum
                + "\nDate: " + _date.toString()
                + "\nUnits: " + _units
                + "\nPrice: "  + _grantPrice;
    }

    public static class DiDataRowBuilder {
        private DiDataRowType _type;
        private String _empNum;
        private LocalDate _date;
        private Integer _units;
        private BigDecimal _grantPrice;
        private BigDecimal _performanceMultiplier;
        private BigDecimal _salePrice;

        public DiDataRowBuilder() {
            _type = null;
             _empNum = null;
             _date = null;
             _units = null;
             _grantPrice = null;
             _performanceMultiplier = null;
             _salePrice = null;
        }

        public DiDataRowBuilder setType(DiDataRowType _type) {
            this._type = _type;
            return this;
        }

        public DiDataRowBuilder setEmpNum(String _empNum) {
            this._empNum = _empNum;
            return this;
        }

        public DiDataRowBuilder setDate(LocalDate _date) {
            this._date = _date;
            return this;
        }

        public DiDataRowBuilder setUnits(Integer _units) {
            this._units = _units;
            return this;
        }

        public DiDataRowBuilder setGrantPrice(BigDecimal _grantPrice) {
            this._grantPrice = _grantPrice;
            return this;
        }

        public DiDataRowBuilder setPerformanceMultiplier(BigDecimal _performanceMultiplier) {
            this._performanceMultiplier = _performanceMultiplier;
            return this;
        }

        public DiDataRowBuilder setSalePrice(BigDecimal _salePrice) {
            this._salePrice = _salePrice;
            return this;
        }

        public DiDataRow build() {
            return new DiDataRow(_type, _empNum, _date, _units, _grantPrice, _performanceMultiplier, _salePrice);
        }


    }
}
