package DataImport;

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
            return RowType.VEST;
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
}
