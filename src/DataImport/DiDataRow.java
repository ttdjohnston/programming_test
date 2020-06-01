package DataImport;

import java.time.LocalDate;
import java.util.Date;

public class DiDataRow {
    protected DiDataRowType _type;
    protected String _empNum;
    protected LocalDate _date;

    public DiDataRow(DiDataRowType type, String empNum, LocalDate date) {
        _type = type;
        _empNum = empNum;
        _date = date;
    }

    public DiDataRowType getType() { return _type; }
    public String getEmpNum() { return _empNum; }
    public LocalDate getDate() { return _date; }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DiDataRow) {
            DiDataRow castedObj = (DiDataRow) obj;
            return _type.equals(castedObj.getType()) && _empNum.equals(castedObj.getEmpNum()) && _date.equals(castedObj.getDate());
        }
        else
            return false;
    }
}
