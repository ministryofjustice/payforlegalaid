package uk.gov.laa.gpfd.services.excel.editor;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.IntStream.range;
import static java.util.stream.Stream.of;
import static org.apache.poi.ss.usermodel.CellType.FORMULA;

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
     * Evaluates all formula cells in the workbook.
     *
     * <p>This method iterates through the workbook and evaluates all formula cells in those sheets.
     *
     * @param workbook the workbook containing the sheets. Must not be {@code null}.
     * @throws IllegalArgumentException if the workbook is {@code null}
     */
    default void evaluateAllFormulaCells(Workbook workbook) {
        if (workbook == null) {
            throw new IllegalArgumentException("Workbook cannot be null");
        }

        var evaluator = workbook.getCreationHelper().createFormulaEvaluator();

        range(0, workbook.getNumberOfSheets())
                .mapToObj(workbook::getSheetAt)
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