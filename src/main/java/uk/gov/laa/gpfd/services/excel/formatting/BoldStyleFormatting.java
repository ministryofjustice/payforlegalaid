package uk.gov.laa.gpfd.services.excel.formatting;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import uk.gov.laa.gpfd.model.excel.ExcelMappingProjection;

/**
 * A {@link Formatting} implementation that applies bold styling to Excel cells.
 * <p>
 * This formatter creates and applies a cell style with bold font formatting to the target cell.
 * The style is created on-demand for each cell to avoid potential issues with shared styles.
 * </p>
 */
public interface BoldStyleFormatting extends Formatting {

    /**
     * Applies bold formatting to the specified cell.
     * <p>
     * Creates a new cell style with bold font and applies it to the target cell.
     * The original cell content and other formatting attributes are preserved.
     * </p>
     *
     * @param sheet the parent sheet of the cell (used to access the workbook)
     * @param cell the target cell to format
     * @param fieldAttribute the field attributes (not used in this implementation)
     * @throws NullPointerException if any parameter is null
     */
    @Override
    default void apply(Sheet sheet, Cell cell, ExcelMappingProjection fieldAttribute) {
        cell.setCellStyle(createBoldStyle(sheet.getWorkbook()));
    }

    private CellStyle createBoldStyle(Workbook workbook) {
        var style = workbook.createCellStyle();
        style.setFont(createBoldFont(workbook));
        return style;
    }

    private Font createBoldFont(Workbook workbook) {
        var font = workbook.createFont();
        font.setBold(true);
        return font;
    }
}
