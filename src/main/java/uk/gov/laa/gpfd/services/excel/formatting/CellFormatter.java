package uk.gov.laa.gpfd.services.excel.formatting;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import uk.gov.laa.gpfd.model.FieldAttributes;

import java.util.Collection;

/**
 * The {@code CellFormatter} interface is a functional interface designed to apply formatting to a {@link Cell}
 * in a {@link Sheet} based on the provided {@link FieldAttributes}. It provides a mechanism to customize
 * the appearance and behavior of cells in a sheet.
 */
@FunctionalInterface
public interface CellFormatter {

    /**
     * Applies formatting to the specified {@link Cell} based on the provided {@link FieldAttributes}.
     * This method is the primary entry point for customizing cell formatting.
     *
     * @param sheet         the sheet containing the cell
     * @param cell          the cell to which formatting will be applied
     * @param fieldAttribute the {@link FieldAttributes} that define the formatting rules for the cell
     */
    void applyFormatting(Sheet sheet, Cell cell, FieldAttributes fieldAttribute);

    /**
     * Applies a collection of {@link Formatting} instances to the specified {@link Cell}.
     * This method provides a default implementation that iterates over the provided formatting strategies
     * and applies each one to the cell.
     *
     * @param formatting     the collection of {@link Formatting} instances to apply
     * @param sheet          the sheet containing the cell
     * @param cell           the cell to which formatting will be applied
     * @param fieldAttribute the {@link FieldAttributes} that define the formatting rules for the cell
     */
    default void applyFormatting(Collection<Formatting> formatting, Sheet sheet, Cell cell, FieldAttributes fieldAttribute) {
        for (var format : formatting) format.apply(sheet, cell, fieldAttribute);
    }
}