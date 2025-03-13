package uk.gov.laa.gpfd.services.excel.formatting;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import uk.gov.laa.gpfd.model.FieldAttributes;

import static uk.gov.laa.gpfd.utils.ConsumerUtil.applyIfPresent;

/**
 * The interface extends the {@link Formatting} interface and provides
 * a default implementation for applying cell formatting based on the {@link FieldAttributes#getFormat()} value.
 * This strategy is specifically designed to set the data format of a cell in a {@link Sheet}.
 */
public interface CellFormatting extends Formatting {

    /**
     * Applies cell formatting to the specified {@link Cell} based on the format string provided by the
     * {@link FieldAttributes}. If a format string is present, a new {@link CellStyle} is created and applied
     * to the cell.
     *
     * @param sheet         the sheet containing the cell
     * @param cell          the cell to which formatting will be applied
     * @param fieldAttribute the {@link FieldAttributes} that define the formatting rules for the cell
     **/
    @Override
    default void apply(Sheet sheet, Cell cell, FieldAttributes fieldAttribute) {
        var workbook = sheet.getWorkbook();

        applyIfPresent(fieldAttribute.getFormat(), format -> {
            var cellStyle = workbook.createCellStyle();
            cellStyle.setDataFormat(workbook.createDataFormat().getFormat(format));
            cell.setCellStyle(cellStyle);
        });
    }

}

