package uk.gov.laa.gpfd.services.excel.formatting;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import uk.gov.laa.gpfd.model.FieldAttributes;

/**
 * The interface is a functional interface designed to define a strategy for applying
 * formatting to a {@link Cell} in a {@link Sheet} based on the provided {@link FieldAttributes}. It serves as
 * a contract for customizing the appearance and behavior of cells in a spreadsheet.
 */
@FunctionalInterface
public interface Formatting {

    /**
     * Applies formatting to the specified {@link Cell} in the given {@link Sheet} based on the provided
     * {@link FieldAttributes}. This method defines the core logic for customizing the appearance or behavior
     * of the cell.
     *
     * @param sheet         the sheet containing the cell
     * @param cell          the cell to which formatting will be applied
     * @param fieldAttribute the {@link FieldAttributes} that define the formatting rules for the cell
     */
    void apply(Sheet sheet, Cell cell, FieldAttributes fieldAttribute);
}