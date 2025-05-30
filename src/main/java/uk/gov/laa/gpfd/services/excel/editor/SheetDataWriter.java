package uk.gov.laa.gpfd.services.excel.editor;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import uk.gov.laa.gpfd.model.FieldAttributes;
import uk.gov.laa.gpfd.services.excel.formatting.CellFormatter;

import java.util.Collection;
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
     * @param fieldAttributes the collection of {@link FieldAttributes} that define how each field is mapped and formatted
     */
    void writeDataToSheet(Sheet sheet, Stream<Map<String, Object>> data, Collection<FieldAttributes> fieldAttributes);

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
    default void writeDataToSheet(CellValueSetter cellValueSetter, CellFormatter cellFormatter,
                                  Sheet sheet, Stream<Map<String, Object>> stream, Collection<FieldAttributes> fieldAttributes) {
        final AtomicInteger rowNum = new AtomicInteger(1);
        stream.forEach(data -> writeRowData(
                sheet.createRow(rowNum.getAndIncrement()),
                data,
                fieldAttributes,
                cellValueSetter,
                cellFormatter
        ));
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
    private void writeRowData(Row row, Map<String, Object> rowData, Collection<FieldAttributes> fieldAttributes,
                              CellValueSetter cellValueSetter, CellFormatter cellFormatter) {
        var cellIndex = 0;

        for (var fieldAttribute : fieldAttributes) {
            var value = rowData.get(fieldAttribute.getSourceName());
            var cell = row.createCell(cellIndex++);
            if (value == null) {
                continue;
            }
            setCellValueAndFormat(cell, value, fieldAttribute, cellValueSetter, cellFormatter);
        }
    }

    /**
     * Sets the value of a cell and applies formatting based on the provided {@link FieldAttributes}.
     * This method uses the {@link CellValueSetter} to set the cell value and the {@link CellFormatter}
     * to apply formatting to the cell.
     *
     * @param cell            the cell to which the value and formatting will be applied
     * @param value           the value to be set in the cell
     * @param fieldAttribute  the {@link FieldAttributes} that define how the field is formatted
     * @param cellValueSetter the {@link CellValueSetter} used to set the cell value
     * @param cellFormatter   the {@link CellFormatter} used to apply formatting to the cell
     */
    private void setCellValueAndFormat(Cell cell, Object value, FieldAttributes fieldAttribute,
                                       CellValueSetter cellValueSetter, CellFormatter cellFormatter) {
        cellValueSetter.setCellValue(cell, value);
        cellFormatter.applyFormatting(cell.getSheet(), cell, fieldAttribute);
    }
}