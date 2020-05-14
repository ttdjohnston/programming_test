package DataImport;

import java.util.List;

public class DiFileProcessor {
    private DiFileValidator _diFileValidator = new DiFileValidator();

    public DiFileProcessor() {

    }

    public void processFile(List<String> importedFile) {
        _diFileValidator.validateFile(importedFile);
    }
}
