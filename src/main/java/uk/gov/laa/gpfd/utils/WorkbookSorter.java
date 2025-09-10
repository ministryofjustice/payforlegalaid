package uk.gov.laa.gpfd.utils;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import uk.gov.laa.gpfd.services.excel.workbook.ReportWorkbook;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static java.util.Map.Entry.comparingByValue;
import static java.util.Optional.ofNullable;

/**
 * A functional interface for sorting workbook sheets according to a template specification.
 * Provides factory methods for creating customized sorters and a default implementation.
 */
@FunctionalInterface
public interface WorkbookSorter {

    /**
     * Creates a customized workbook sorter with the specified processing pipeline.
     *
     * @param preprocessor a function to transform the workbook before sorting
     * @param sorter       the operation to perform for each sheet position adjustment
     * @return a new WorkbookSorter instance combining the provided operations
     * @throws NullPointerException if either parameter is null
     */
    static WorkbookSorter create(Function<Workbook, Workbook> preprocessor,
                                 BiConsumer<Workbook, Map.Entry<String, Integer>> sorter) {
        return (workbook, template) -> {
            var target = preprocessor.apply(workbook);
            template.entrySet().stream()
                    .sorted(comparingByValue())
                    .forEach(entry -> sorter.accept(target, entry));
        };
    }

    /**
     * Returns the default sorter implementation with standard preprocessing. The sorting operation will
     * skip sheets not found in the workbook.
     *
     * @return the default sorter implementation
     */
    static WorkbookSorter defaultSorter() {
        return create(
                standardPreprocessor(),
                (wb, entry) -> ofNullable(wb.getSheet(entry.getKey()))
                        .ifPresent(sheet -> wb.setSheetOrder(sheet.getSheetName(), entry.getValue()))
        );
    }

    /**
     * Returns the standard preprocessing pipeline for workbook conversion.
     *
     * @return a composed function representing the standard preprocessing steps
     */
    static Function<Workbook, Workbook> standardPreprocessor() {
        return ((Function<Workbook, Workbook>) WorkbookSorter::handleSXSSF)
                .andThen(WorkbookSorter::handleReportWorkbook);
    }

    /**
     * Handles conversion of SXSSFWorkbook to XSSFWorkbook if needed.
     *
     * @param wb the input workbook
     * @return the XSSFWorkbook for SXSSF inputs, original workbook otherwise
     */
    private static Workbook handleSXSSF(Workbook wb) {
        return wb instanceof SXSSFWorkbook sxssfWb ? sxssfWb.getXSSFWorkbook() : wb;
    }

    /**
     * Handles conversion of ReportWorkbook to XSSFWorkbook if needed.
     *
     * @param wb the input workbook
     * @return the XSSFWorkbook for ReportWorkbook inputs, original workbook otherwise
     */
    private static Workbook handleReportWorkbook(Workbook wb) {
        return wb instanceof ReportWorkbook reportWb ? reportWb.getXSSFWorkbook() : wb;
    }

    /**
     * Sorts the sheets in the workbook according to the template specification.
     * The template maps sheet names to their desired zero-based positions.
     *
     * @param workbook the workbook to sort
     * @param template an ordered mapping of sheet names to target positions
     * @throws NullPointerException if either parameter is null
     */
    void sort(Workbook workbook, LinkedHashMap<String, Integer> template);
}