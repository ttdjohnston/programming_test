package DataImport;

public class DiRowType {
    private final static String VEST = "VEST";

    public static boolean isVest(String type) {
        return type.equals(VEST);
    }
}
