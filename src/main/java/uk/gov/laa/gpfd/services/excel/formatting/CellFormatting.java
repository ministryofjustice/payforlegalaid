package uk.gov.laa.gpfd.services.excel.formatting;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import uk.gov.laa.gpfd.model.excel.ExcelMappingProjection;
import uk.gov.laa.gpfd.services.excel.workbook.StyleManager;

import static uk.gov.laa.gpfd.utils.ConsumerUtil.applyIfPresent;

/**
 * The interface extends the {@link Formatting} interface and provides
 * a default implementation for applying cell formatting based on the {@link ExcelMappingProjection#getFormat()} value.
 * This strategy is specifically designed to set the data format of a cell in a {@link Sheet}.
 */
public abstract class CellFormatting implements Formatting {

    private final StyleManager styleManager;

    protected CellFormatting(StyleManager styleManager) {
        this.styleManager = styleManager;
    }

    /**
     * Applies cell formatting to the specified {@link Cell} based on the format string provided by the
     * {@link ExcelMappingProjection}. If a format string is present, a new {@link CellStyle} is created and applied
     * to the cell.
     *
     * @param sheet         the sheet containing the cell
     * @param cell          the cell to which formatting will be applied
     * @param fieldAttribute the {@link ExcelMappingProjection} that define the formatting rules for the cell
     **/
    @Override
    public void apply(Sheet sheet, Cell cell, ExcelMappingProjection fieldAttribute) {
        var workbook = sheet.getWorkbook();
        applyIfPresent(fieldAttribute.getFormat(), format -> {
            var cellStyle = workbook.createCellStyle();
            cellStyle.setDataFormat(workbook.createDataFormat().getFormat(format));
            styleManager.setColumnStyle(cell.getColumnIndex(), sheet.getSheetName(), cellStyle);
        });
    }

}

