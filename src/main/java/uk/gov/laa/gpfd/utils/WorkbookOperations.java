package uk.gov.laa.gpfd.utils;

import org.apache.poi.ss.usermodel.Workbook;
import uk.gov.laa.gpfd.services.excel.copier.SheetCopier;

import java.util.function.BiFunction;
import java.util.function.Consumer;

import static uk.gov.laa.gpfd.services.excel.copier.SheetCopierFactory.createCopier;

/**
 * Provides utility operations for manipulating Excel workbooks.
 * This interface specializes in transferring sheets between workbooks while maintaining
 * immutability and composability of operations.
 */
public interface WorkbookOperations {

    /**
     * Creates a function that produces a SheetCopier for transferring a specific sheet
     * between workbooks.
     *
     * @param sheetName the name of the sheet to be copied
     * @return a BiFunction that takes source and target workbooks and returns a SheetCopier
     */
    static BiFunction<Workbook, Workbook, SheetCopier> createSheetTransfer(String sheetName) {
        return (source, target) -> createCopier(source, target, sheetName);
    }

    /**
     * Consumer that executes the sheet copying operation.
     */
    Consumer<SheetCopier> COPY_SHEET = SheetCopier::copySheet;

    /**
     * Transfers a specified sheet from a source workbook to a target workbook.
     *
     * @param sourceWorkbook the workbook containing the original sheet
     * @param targetWorkbook the workbook to receive the copied sheet
     * @param sheetName the name of the sheet to transfer
     * @throws NullPointerException if any parameter is null
     * @throws IllegalArgumentException if the sheet doesn't exist in source workbook
     */
    default void transferSheet(Workbook sourceWorkbook, Workbook targetWorkbook, String sheetName) {
        COPY_SHEET.accept(
                createSheetTransfer(sheetName)
                        .apply(sourceWorkbook, targetWorkbook)
        );
    }
}