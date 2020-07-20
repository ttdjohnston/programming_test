package DataImport;

import java.time.LocalDate;

public class DiDataRow {
    private DiDataRowType _type;
    private String _empNum;
    private LocalDate _date;
    private Integer _units;
    private Double _grantPrice;
    private Double _performanceMultiplier;
    private Double _salePrice;

    private DiDataRow() {}

    private DiDataRow(DiDataRowType type, String empNum, LocalDate date, Integer units, Double grantPrice, Double performanceMultiplier, Double salePrice) {
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
    public Double getGrantPrice() {        return _grantPrice;    }
    public Double getSalePrice() {        return _salePrice;    }
    public Double getPerformanceMultiplier() { return _performanceMultiplier; }

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
                    && _units.equals(castedObj.getUnits())
                    && _grantPrice.equals(castedObj.getGrantPrice());
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
        private Double _grantPrice;
        private Double _performanceMultiplier;
        private Double _salePrice;

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

        public DiDataRowBuilder setGrantPrice(Double _grantPrice) {
            this._grantPrice = _grantPrice;
            return this;
        }

        public DiDataRowBuilder setPerformanceMultiplier(Double _performanceMultiplier) {
            this._performanceMultiplier = _performanceMultiplier;
            return this;
        }

        public DiDataRowBuilder setSalePrice(Double _salePrice) {
            this._salePrice = _salePrice;
            return this;
        }

        public DiDataRow build() {
            return new DiDataRow(_type, _empNum, _date, _units, _grantPrice, _performanceMultiplier, _salePrice);
        }


    }
}
