package DataImport;

import java.util.List;

public class DiFile {
    private Integer numberOfDiRows;
    private List<DiDataRow> dataRows;
    private FileFooter footer;

    public DiFile(Integer numberOfDiRows, List<DiDataRow> dataRows, FileFooter footer) {
        this.numberOfDiRows = numberOfDiRows;
        this.dataRows = dataRows;
        this.footer = footer;
    }

    public Integer getNumberOfDiRows() {        return numberOfDiRows;    }
    public List<DiDataRow> getDataRows() {        return dataRows;    }
    public FileFooter getFooter() {        return footer;    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DiFile) {
            DiFile castedObj = (DiFile) obj;
            return numberOfDiRows.equals(castedObj.getNumberOfDiRows())
                    && footer.equals(castedObj.getFooter())
                    && dataRows.size() == castedObj.getDataRows().size()
                    && dataRows.containsAll(castedObj.getDataRows());
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Number of rows: " + numberOfDiRows + "\n");
        for (DiDataRow row : dataRows) {
            str.append(row.toString() + "\n");
        }
        str.append(footer.toString());

        return str.toString();
    }
}
