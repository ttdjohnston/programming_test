package DataImport;

import java.time.LocalDate;

public class DiDataRow {
    private DiDataRowType _type;
    private String _empNum;
    private LocalDate _date;
    private Integer _unitsVested;
    private Double _grantPrice;

    public DiDataRow(DiDataRowType type, String empNum, LocalDate date, Integer unitsVested, Double grantPrice) {
        _type = type;
        _empNum = empNum;
        _date = date;
        _unitsVested = unitsVested;
        _grantPrice = grantPrice;

    }

    public DiDataRowType getType() { return _type; }
    public String getEmpNum() { return _empNum; }
    public LocalDate getDate() { return _date; }
    public Integer getUnitsVested() {        return _unitsVested;    }
    public Double getGrantPrice() {        return _grantPrice;    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DiDataRow) {
            DiDataRow castedObj = (DiDataRow) obj;
            return _type.equals(castedObj.getType())
                    && _empNum.equals(castedObj.getEmpNum())
                    && _date.equals(castedObj.getDate())
                    && _unitsVested.equals(castedObj.getUnitsVested())
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
                + "\nUnits: " + _unitsVested
                + "\nPrice: "  + _grantPrice;
    }
}
