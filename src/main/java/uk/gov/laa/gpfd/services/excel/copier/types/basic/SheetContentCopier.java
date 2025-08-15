package uk.gov.laa.gpfd.services.excel.copier.types.basic;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import uk.gov.laa.gpfd.exception.ReportGenerationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static uk.gov.laa.gpfd.services.excel.copier.types.basic.CellValueCopier.copyValue;

/**
 * A utility class for copying content (cells, styles, merged regions, etc.)
 * between Excel worksheets using Apache POI.
 */
public abstract class SheetContentCopier {

    /**
     * Creates a {@link BiConsumer} that copies cell content (values, styles) from source to target sheet.
     */
    public static BiConsumer<Sheet, Sheet> createContentCopier() {
        return new ContentCopier();
    }

    /**
     * Creates a {@link BiConsumer} that copies column widths from source to target sheet.
     */
    public static BiConsumer<Sheet, Sheet> createColumnWidthCopier() {
        return new ColumnWidthCopier();
    }

    /**
     * Creates a {@link BiConsumer} that copies merged regions from source to target sheet.
     */
    public static BiConsumer<Sheet, Sheet> createMergedRegionCopier() {
        return new MergedRegionCopier();
    }

    /**
     * Applies all copiers (merged regions, column widths, cell content) in the recommended order.
     */
    public static void copyAll(Sheet sourceSheet, Sheet targetSheet) {
        List.of(createMergedRegionCopier(), createColumnWidthCopier(), createContentCopier())
                .forEach(copier -> copier.accept(sourceSheet, targetSheet));
    }

    /**
     * Copies cell content including values and styles between sheets.
     * <p>
     * Implements efficient style caching to prevent creating duplicate styles
     * in the target workbook.
     * </p>
     */
    static class ContentCopier implements BiConsumer<Sheet, Sheet> {
        private final Map<CellStyle, CellStyle> styleCache = new HashMap<>();

        /**
         * Copies all rows and cells from source to target sheet.
         *
         * @param sourceSheet the sheet to copy from (must not be null)
         * @param targetSheet the sheet to copy to (must not be null)
         */
        @Override
        @SuppressWarnings("java:S127") // "for" loop stop conditions should be invariant
        public void accept(Sheet sourceSheet, Sheet targetSheet) {
            for (int i = 0; i <= sourceSheet.getLastRowNum(); ) {
                var sourceRow = sourceSheet.getRow(i);
                if (sourceRow != null) {
                    var targetRow = targetSheet.createRow(i);
                    copyRowProperties(sourceRow, targetRow);
                    copyCells(sourceRow, targetRow);
                }
                // Intentional placement for performance improvement
                i++;
            }
        }

        /**
         * Copies row height properties from source to target row.
         *
         * @param sourceRow the row to copy from
         * @param targetRow the row to copy to
         */
        private void copyRowProperties(Row sourceRow, Row targetRow) {
            targetRow.setHeight(sourceRow.getHeight());
            targetRow.setZeroHeight(sourceRow.getZeroHeight());
            targetRow.setHeightInPoints(sourceRow.getHeightInPoints());
        }

        /**
         * Copies all cells from source to target row.
         *
         * @param sourceRow the row containing source cells
         * @param targetRow the row to create target cells in
         */
        @SuppressWarnings("java:S127") // "for" loop stop conditions should be invariant
        private void copyCells(Row sourceRow, Row targetRow) {
            for (int j = 0; j < sourceRow.getLastCellNum(); ) {
                Cell sourceCell = sourceRow.getCell(j);
                if (sourceCell != null) {
                    Cell targetCell = targetRow.createCell(j);
                    copyCell(sourceCell, targetCell);
                }
                // Intentional placement for performance improvement
                j++;
            }
        }

        /**
         * Copies cell content including style and value.
         *
         * @param sourceCell the cell to copy from
         * @param targetCell the cell to copy to
         */
        private void copyCell(Cell sourceCell, Cell targetCell) {
            copyCellStyle(sourceCell, targetCell);
            copyValue(sourceCell, targetCell);
        }

        /**
         * Copies cell style using caching to optimize performance.
         *
         * @param sourceCell the source of the style
         * @param targetCell the target for the style
         */
        private void copyCellStyle(Cell sourceCell, Cell targetCell) {
            var sourceStyle = sourceCell.getCellStyle();
            var targetStyle = styleCache.computeIfAbsent(sourceStyle, style -> {

                prepareCellBordersForCopying(style);

                CellStyle newStyle = targetCell.getSheet().getWorkbook().createCellStyle();
                newStyle.cloneStyleFrom(style);
                return newStyle;
            });
            targetCell.setCellStyle(targetStyle);
        }

        // Apache POI is not copying the border in the cell style by default - this is due to a flag called applyBorder
        // Excel doesn't usually set applyBorder as it doesn't care about the value, but POI will ignore the border if it is not true
        // So we work around this by - if there is a border - setting that flag ourselves, so POI will copy the border across
        // as part of the cell style in the "cloneStyleFrom" step
        private static void prepareCellBordersForCopying(CellStyle style) {
            var coreXfStyleDetails = ((XSSFCellStyle) style).getCoreXf();
            if (coreXfStyleDetails.getBorderId() == 0) return;
            coreXfStyleDetails.setApplyBorder(true);
        }
    }

    /**
     * Copies column widths from source to target sheet.
     * <p>
     * Uses the first row's cell count to determine how many columns to process.
     * </p>
     */
    static class ColumnWidthCopier implements BiConsumer<Sheet, Sheet> {
        private static final int ERROR_ROW_VALUE = -1;

        /**
         * Copies column widths between sheets.
         *
         * @param sourceSheet the sheet to copy from
         * @param targetSheet the sheet to copy to
         */
        @Override
        @SuppressWarnings("java:S127") // "for" loop stop conditions should be invariant
        public void accept(Sheet sourceSheet, Sheet targetSheet) {
            int firstRowNum = sourceSheet.getFirstRowNum();
            if (firstRowNum != ERROR_ROW_VALUE) {
                var firstRow = sourceSheet.getRow(firstRowNum);
                if (firstRow != null) {
                    for (int i = 0; i < firstRow.getLastCellNum(); ) {
                        targetSheet.setColumnWidth(i, sourceSheet.getColumnWidth(i));
                        // Intentional placement for performance improvement
                        i++;
                    }
                }
            }
        }
    }

    /**
     * Copies merged regions from source to target sheet.
     * <p>
     * Silently handles failures to copy individual merged regions while continuing
     * with remaining regions.
     * </p>
     */
    static class MergedRegionCopier implements BiConsumer<Sheet, Sheet> {
        /**
         * Copies all merged regions between sheets.
         *
         * @param sourceSheet the sheet containing source regions
         * @param targetSheet the sheet to add regions to
         */
        @Override
        @SuppressWarnings("java:S127") // "for" loop stop conditions should be invariant
        public void accept(Sheet sourceSheet, Sheet targetSheet) {
            for (int i = 0; i < sourceSheet.getNumMergedRegions(); ) {
                try {
                    targetSheet.addMergedRegion(sourceSheet.getMergedRegion(i++));
                } catch (Exception e) {
                    throw new ReportGenerationException.SheetCopyException("Failed to copy merged region: ", e);
                }
            }
        }
    }
}