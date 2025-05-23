package uk.gov.laa.gpfd.services.excel.editor;

import org.apache.poi.ss.usermodel.Cell;

import java.util.function.BiConsumer;

import static uk.gov.laa.gpfd.services.excel.editor.CellValueHandler.CellValueHandlerMap;

/**
 * The {@code CellValueSetter} interface provides a mechanism to set values in a {@link Cell} object
 * based on the type of the value. It uses a map of handlers, where each handler is a {@link BiConsumer}
 * that defines how to set the value in the cell for a specific type.
 */
public interface CellValueSetter {

    /**
     * Sets the value in the specified {@link Cell} based on the runtime type of the provided value.
     *
     * <p>This method retrieves the appropriate handler from the {@link #CellValueHandler} map based on the
     * runtime type of the value and applies it to the cell. If no specific handler is found for the
     * value's type, the default handler for {@link Object} is used, which converts the value to a string.
     *
     * @param cell  the cell in which the value is to be set
     * @param value the value to be set in the cell
     */
    default void setCellValue(Cell cell, Object value) {
        CellValueHandlerMap.get(value.getClass()).accept(cell, value);
    }

}
