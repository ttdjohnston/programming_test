package DataImport;

import java.util.List;

public class DiFileProcessor {
    private DiFileInterpreter _diFileInterpreter;

    public DiFileProcessor() {
        _diFileInterpreter = new DiFileInterpreter();
    }

    public void processFile(List<String> importedFile) throws InvalidFileException {
        _diFileInterpreter.interpretFile(importedFile);
    }
}
