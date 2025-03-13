package uk.gov.laa.gpfd.services.excel.formatting;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import uk.gov.laa.gpfd.model.FieldAttributes;

import java.util.Optional;

/**
 * The interface extends the {@link Formatting} interface and provides
 * a default implementation for setting the width of a column in a {@link Sheet} based on the
 * {@link FieldAttributes#getColumnWidth()} value. This strategy is specifically designed to adjust
 * column widths dynamically.
 */
public interface ColumnFormatting extends Formatting {

    /**
     * Applies the column width specified in the {@link FieldAttributes} to the column containing the
     * specified {@link Cell}. If the column width is not specified or is less than or equal to 0,
     * no action is taken.
     *
     * @param sheet         the sheet containing the cell
     * @param cell          the cell whose column width will be adjusted
     * @param fieldAttribute the {@link FieldAttributes} that define the column width for the cell's column
     */
    @Override
    default void apply(Sheet sheet, Cell cell, FieldAttributes fieldAttribute) {
        Optional.of(fieldAttribute.getColumnWidth())
                .filter(width -> width > 0)
                .map(width -> (int) (width * 256))
                .ifPresent(width -> sheet.setColumnWidth(cell.getColumnIndex(), width));
    }

}