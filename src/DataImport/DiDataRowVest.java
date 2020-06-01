package DataImport;

import java.time.LocalDate;
import java.util.Date;

public class DiDataRowVest extends DiDataRow {

    private Integer _unitsVested;
    private Double _grantPrice;

    public DiDataRowVest(DiDataRowType type, String empNum, LocalDate date, Integer unitsVested, Double grantPrice) {
        super(type, empNum, date);
        _unitsVested = unitsVested;
        _grantPrice = grantPrice;
    }

    public Integer getUnitsVested() {        return _unitsVested;    }
    public Double getGrantPrice() {        return _grantPrice;    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DiDataRowVest) {
            DiDataRowVest castedObj = (DiDataRowVest) obj;
            return super.equals(castedObj) && _unitsVested.equals(castedObj.getUnitsVested()) && _grantPrice.equals(castedObj.getGrantPrice());
        } else {
            return false;
        }
    }
}
