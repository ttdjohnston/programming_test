package DataImport;

import java.util.*;

public class DiFileProcessor {
    private DiFileInterpreter _diFileInterpreter;
    private Map<String, PriorityQueue<GrantEvent>> _employeeEvents;

    public DiFileProcessor() {
        _diFileInterpreter = new DiFileInterpreter();
        _employeeEvents = new HashMap<>();
    }

    public ProcessedFile processFile(List<String> importedFile) throws InvalidFileException {
        DiFile interpretedFile = _diFileInterpreter.interpretFile(importedFile);
        for (DiDataRow row : interpretedFile.getDataRows()) {
            GrantEvent event = GrantEvent.newBuilder()
                    .setType(row.getType())
                    .setDate(row.getDate())
                    .setUnits(row.getUnits())
                    .setPrice(row.getGrantPrice())
                    .build();
            if (_employeeEvents.get(row.getEmpNum()) == null) {
                _employeeEvents.put(row.getEmpNum(), new PriorityQueue<>(new Comparator<GrantEvent>() {
                    @Override
                    public int compare(GrantEvent o1, GrantEvent o2) {
                        if (o1.getDate().compareTo(o2.getDate()) == 0) {
                            return o1.getType().compareTo(o2.getType());
                        }
                        return o1.getDate().compareTo(o2.getDate());
                    }
                }));
            }
            _employeeEvents.get(row.getEmpNum()).add(event);
        }

        return new ProcessedFile(_employeeEvents, interpretedFile.getFooter().getFooterDate(), interpretedFile.getFooter().getMarketPrice());
    }
}
