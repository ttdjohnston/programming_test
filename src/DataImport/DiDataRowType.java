package DataImport;

import java.util.Comparator;

import static DataImport.DiDataRowType.RowType.*;

public class DiDataRowType implements Comparable<DiDataRowType> {
    public enum RowType {VEST, PERF, SALE}
    private final static String VEST_STR = "VEST";
    private final static String PERF_STR = "PERF";
    private final static String SALE_STR = "SALE";

    private RowType _type;

    public DiDataRowType(String type) {
        _type = determineRowType(type);
    }

    public static boolean isVest(String type) {        return type.equals(VEST_STR);    }

    public static boolean isPerf(String type) {       return type.equals(PERF_STR);    }

    public static boolean isSale(String type) {        return type.equals(SALE_STR);    }

    public boolean isVest() { return _type.equals(VEST);   }

    public boolean isPerf() {
        return _type.equals(PERF);
    }

    public boolean isSale() {
        return _type.equals(SALE);
    }

    public static RowType determineRowType(String type) {
        if (isVest(type)) {
            return VEST;
        } else if (isPerf(type)) {
            return PERF;
        } else if (isSale(type)) {
            return SALE;
        }
        else {
            return null;
        }
    }

    private RowType getType() {     return _type;    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DiDataRowType) {
            DiDataRowType castedObj = (DiDataRowType) obj;
            return _type.equals(castedObj.getType());
        }
        else {
            return false;
        }
    }

    @Override
    public String toString() {
        switch (_type) {
            case VEST :
                return VEST_STR;
            case PERF:
                return PERF_STR;
            case SALE:
                return SALE_STR;
            default:
                throw new IllegalStateException("Unexpected value: " + _type);
        }
    }

    @Override
    public int compareTo(DiDataRowType o) {
        return this._type.compareTo(o._type);
    }


}
