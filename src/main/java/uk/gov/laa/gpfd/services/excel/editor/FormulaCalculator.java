package uk.gov.laa.gpfd.services.excel.editor;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.text.MessageFormat.format;
import static java.util.Arrays.stream;
import static java.util.stream.Stream.of;
import static org.apache.poi.ss.usermodel.CellType.FORMULA;
import static uk.gov.laa.gpfd.services.excel.util.SheetUtils.findSheetByName;

/**
 * The {@code FormulaCalculator} interface provides functionality to evaluate formula cells
 * in specified sheets of an Excel workbook. It is designed to work with Apache POI's
 * {@link Workbook} and supports evaluating formulas in one or more sheets by name.
 *
 * @see Workbook
 * @see FormulaEvaluator
 */
public interface FormulaCalculator {

    /**
     * Evaluates all formula cells in the specified sheets of the workbook.
     *
     * <p>This method iterates through the provided sheet names, finds the corresponding sheets,
     * and evaluates all formula cells in those sheets. If a sheet is not found, an
     * {@link IllegalArgumentException} is thrown.
     *
     * @param workbook the workbook containing the sheets. Must not be {@code null}.
     * @param sheets   the names of the sheets to evaluate formulas in. At least one sheet name must be provided.
     * @throws IllegalArgumentException if the workbook is {@code null}, no sheet names are provided,
     *                                  or a specified sheet is not found in the workbook.
     */
    default void evaluateAllFormulaCells(Workbook workbook, String... sheets) {
        if (workbook == null) {
            throw new IllegalArgumentException("Workbook cannot be null");
        }
        if (sheets == null || sheets.length == 0) {
            throw new IllegalArgumentException("At least one sheet name must be provided");
        }

        var evaluator = workbook.getCreationHelper().createFormulaEvaluator();

        stream(sheets)
                .map(sheetName -> findSheetByName(workbook, sheetName)
                        .orElseThrow(() -> new IllegalArgumentException(format("Sheet ''{0}'' not found", sheetName))))
                .flatMap(this::streamRows)
                .flatMap(this::streamCells)
                .filter(cell -> FORMULA == cell.getCellType())
                .forEach(evaluator::evaluateFormulaCell);
    }

    /**
     * Creates a stream of rows from a sheet.
     *
     * <p>This method converts a {@link Sheet} into a stream of {@link Row} objects,
     * allowing for efficient processing of rows using Java Streams.
     *
     * @param sheet the sheet to create a row stream from.
     * @return a stream of rows in the sheet.
     */
    private Stream<Row> streamRows(Sheet sheet) {
        return of(sheet).flatMap(s -> StreamSupport.stream(s.spliterator(), false));
    }

    /**
     * Creates a stream of cells from a row.
     *
     * <p>This method converts a {@link Row} into a stream of {@link Cell} objects,
     * allowing for efficient processing of cells using Java Streams.
     *
     * @param row the row to create a cell stream from.
     * @return a stream of cells in the row.
     */
    private Stream<Cell> streamCells(Row row) {
        return of(row).flatMap(r -> StreamSupport.stream(r.spliterator(), false));
    }
}