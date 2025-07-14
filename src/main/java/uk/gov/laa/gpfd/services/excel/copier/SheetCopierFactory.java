package uk.gov.laa.gpfd.services.excel.copier;

import jakarta.annotation.PostConstruct;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uk.gov.laa.gpfd.services.excel.copier.types.basic.BasicSheetCopier;
import uk.gov.laa.gpfd.services.excel.copier.types.xssf.XSSFSheetCopier;
import uk.gov.laa.gpfd.services.excel.formatting.CellFormatting;

import java.util.Objects;
import java.util.function.BiFunction;

import static uk.gov.laa.gpfd.exception.ReportGenerationException.InvalidWorkbookTypeException;
import static uk.gov.laa.gpfd.exception.ReportGenerationException.SheetCopyException;
import static uk.gov.laa.gpfd.exception.ReportGenerationException.SheetNotFoundException;

/**
 * Factory for creating appropriate {@link SheetCopier} instances based on workbook types.
 * <p>
 * This factory determines the most suitable sheet copier implementation based on the
 * input workbook types, creating either:
 * <ul>
 *   <li>{@link XSSFSheetCopier} for XSSF workbooks (supports advanced features like pivot tables)</li>
 *   <li>{@link BasicSheetCopier} for all other workbook types (basic content copying only)</li>
 * </ul>
 * </p>
 */
@Component
public final class SheetCopierFactory {

    private static SheetCopierFactory INSTANCE;
    private final BiFunction<XSSFSheet, XSSFSheet, SheetCopier> xssCopier;
    private final BiFunction<Sheet, Sheet, SheetCopier> basic;

    SheetCopierFactory(CellFormatting cellFormatter) {
        xssCopier=  (sourceSheet,targetSheet) -> new XSSFSheetCopier(cellFormatter, sourceSheet, targetSheet);
        basic =  (sourceSheet, targetSheet) -> new BasicSheetCopier(cellFormatter, sourceSheet, targetSheet);
    }

    @PostConstruct
    private void init() {
        INSTANCE = this;
    }

    /**
     * Creates a function that produces a SheetCopier for transferring a specific sheet
     * between workbooks.
     *
     * @param sheetName the name of the sheet to be copied
     * @return a BiFunction that takes source and target workbooks and returns a SheetCopier
     */
    public static BiFunction<Workbook, Workbook, SheetCopier> createSheetTransfer(String sheetName) {
        return (source, target) -> INSTANCE.createCopier(source, target, sheetName);
    }

    /**
     * Creates an appropriate SheetCopier instance for the given workbooks.
     *
     * @param sourceWorkbook the source workbook to copy from (must not be null)
     * @param targetWorkbook the target workbook to copy to (must be SXSSFWorkbook)
     * @param sheetName the name of the sheet to copy (must exist in source workbook)
     * @return an appropriate SheetCopier implementation
     * @throws InvalidWorkbookTypeException if target workbook is not SXSSFWorkbook
     * @throws SheetNotFoundException if specified sheet doesn't exist in source workbook
     * @throws IllegalArgumentException if any parameter is null
     */
    private SheetCopier createCopier(Workbook sourceWorkbook, Workbook targetWorkbook, String sheetName) throws SheetNotFoundException {
        Objects.requireNonNull(sourceWorkbook, "Source workbook must not be null");
        Objects.requireNonNull(targetWorkbook, "Target workbook stream must not be null");
        Objects.requireNonNull(sheetName, "Sheet must not be null");

        if (!(targetWorkbook instanceof SXSSFWorkbook)) {
            throw new InvalidWorkbookTypeException("Target workbook must be SXSSFWorkbook but was " + targetWorkbook.getClass().getSimpleName());
        }

        var sourceSheet = sourceWorkbook.getSheet(sheetName);
        if (null == sourceSheet) {
            throw new SheetNotFoundException("Sheet '" + sheetName + "' not found in source workbook");
        }

        try {
            var xssfTargetWorkbook = ((SXSSFWorkbook) targetWorkbook).getXSSFWorkbook();
            var targetSheet = xssfTargetWorkbook.createSheet(sheetName);
            targetSheet.setAutobreaks(false);

            if (sourceWorkbook instanceof XSSFWorkbook) {
                return xssCopier.apply((XSSFSheet) sourceSheet, targetSheet);
            } else {
                return basic.apply(sourceSheet, targetSheet);
            }
        } catch (Exception e) {
            throw new SheetCopyException("Failed to create sheet copier", e);
        }
    }
}