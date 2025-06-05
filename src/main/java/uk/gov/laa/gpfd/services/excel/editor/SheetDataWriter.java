package uk.gov.laa.gpfd.services.excel.editor;

import org.apache.commons.math3.util.Pair;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import uk.gov.laa.gpfd.model.*;
import uk.gov.laa.gpfd.services.excel.formatting.CellFormatter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * The {@code SheetDataWriter} interface is a functional interface designed to write data to a {@link Sheet}
 * in a structured manner. It provides methods to write a list of data rows (represented as {@link Map}s)
 * to a sheet, using a specified set of {@link FieldAttributes} to determine how each field is mapped and formatted.
 */
@FunctionalInterface
public interface SheetDataWriter {

    /**
     * Writes the provided data to the specified {@link Sheet} using the given {@link FieldAttributes} to map
     * and format the data. This method is the primary entry point for writing data to a sheet.
     *
     * @param sheet          the sheet to which the data will be written
     * @param data          the data to be written, represented as a list of maps where each map corresponds to a row
     */
    void writeDataToSheet(Mapping report, Sheet sheet, Stream<Map<String, Object>> data);

    /**
     * Writes the provided data to the specified {@link Sheet} using the given {@link CellValueSetter} and
     * {@link CellFormatter} to set cell values and apply formatting. This method provides a default implementation
     * that iterates over the data and writes each row to the sheet, starting from the second row (row index 1).
     *
     * @param cellValueSetter the {@link CellValueSetter} used to set values in the cells
     * @param cellFormatter   the {@link CellFormatter} used to apply formatting to the cells
     * @param sheet           the sheet to which the data will be written
     * @param stream            the data to be written, represented as a list of maps where each map corresponds to a row
     * @param fieldAttributes the collection of {@link FieldAttributes} that define how each field is mapped and formatted
     */
    default void writeDataToSheet(Mapping report, CellValueSetter cellValueSetter, Sheet sheet, Stream<Map<String, Object>> stream) {
        final AtomicInteger rowNum = new AtomicInteger(1);

        var columnMapping = new HashMap<String, Integer>();
        List<Pair<String, String>> list = report.getFieldAttributes().stream()
                .map(e -> new Pair<>(e.getSourceName(), e.getMappedName()))
                .toList();

        for (Cell headerCell : sheet.getRow(0)) {
            Pair<String, String> stringStringPair = list.stream()
                    .filter(e -> e.getValue().equals(headerCell.getStringCellValue()))
                    .findFirst().get();
            columnMapping.put(stringStringPair.getFirst(), headerCell.getColumnIndex());
        }

        stream.forEach(data -> {
            if (rowNum.get() % 500 == 0) {
                System.out.println(rowNum.get() + ": " + data);
            }
            var row = sheet.createRow(rowNum.getAndIncrement());
            for (Map.Entry<String, Object> stringObjectEntry : data.entrySet()) {
                String key = stringObjectEntry.getKey();
                Object value = stringObjectEntry.getValue();
                if (columnMapping.containsKey(key)) {
                    Integer column = columnMapping.get(key);
                    Cell cell = row.createCell(column);
                    if (value == null) {
                        cellValueSetter.setCellValue(cell, "");
                    }else {
                        cellValueSetter.setCellValue(cell, value);
                    }
                }
            }
        });
    }

    /**
     * Writes a single row of data to the specified {@link Row} using the provided {@link FieldAttributes},
     * {@link CellValueSetter}, and {@link CellFormatter}. This method iterates over the field attributes,
     * retrieves the corresponding value from the row data, and writes it to the appropriate cell.
     *
     * @param row             the row to which the data will be written
     * @param rowData         the data for the current row, represented as a map of field names to values
     * @param fieldAttributes the collection of {@link FieldAttributes} that define how each field is mapped and formatted
     * @param cellValueSetter the {@link CellValueSetter} used to set values in the cells
     * @param cellFormatter   the {@link CellFormatter} used to apply formatting to the cells
     */
//    private void writeRowData(Row row, Map<String, Object> rowData,                              CellValueSetter cellValueSetter) {
//        final AtomicInteger cellIndex = new AtomicInteger(0);
//
//        rowData.forEach((s, o) -> {
//            Cell cell = row.createCell(cellIndex.getAndIncrement());
//            row.
//        });
//        for (var fieldAttribute : fieldAttributes) {
//            var value = rowData.get(fieldAttribute.getSourceName());
//            var cell = row.createCell(cellIndex++);
//            if (value == null) {
//                continue;
//            }
//            cellValueSetter.setCellValue(cell, value);
//        }
//    }

}
