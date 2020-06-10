package DataImport;

import static DataImport.DiDataRowType.RowType.VEST;

public class DiDataRowType {
    public enum RowType {VEST}
    private final static String VEST_STR = "VEST";

    private RowType _type;

    public DiDataRowType(String type) {
        _type = determineRowType(type);
    }

    public static boolean isVest(String type) {
        return type.equals(VEST_STR);
    }

    private RowType determineRowType(String type) {
        if (isVest(type)) {
            return VEST;
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
            default:
                throw new IllegalStateException("Unexpected value: " + _type);
        }
    }
}
