package uk.gov.laa.gpfd.services.excel.editor;

import org.apache.poi.ss.usermodel.Cell;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static java.util.Collections.unmodifiableMap;

/**
 * The {@code CellValueSetter} interface provides a mechanism to set values in a {@link Cell} object
 * based on the type of the value. It uses a map of handlers, where each handler is a {@link BiConsumer}
 * that defines how to set the value in the cell for a specific type.
 */
public interface CellValueSetter {

    /**
     * A map of handlers for setting cell values based on the type of the value. The map is unmodifiable
     * and initialized with default handlers for common types.
     *
     * <p>The keys of the map are {@link Class} objects representing the type of the value, and the values
     * are {@link BiConsumer} instances that define how to set the value in the cell.
     *
     * <p>If the value is {@code null}, the cell is set to an empty string. For any other type, the value
     * is converted to a string using its {@link Object#toString()} method.
     */
    Map<Class<?>, BiConsumer<Cell, Object>> handlers = unmodifiableMap(new LinkedHashMap<>() {
        private static final String EMPTY = "";

        {
            put(null, (Cell cell, Object val) -> cell.setCellValue(EMPTY));
            put(String.class, (Cell cell, Object val) -> cell.setCellValue((String) val));
            put(Number.class, (Cell cell, Object val) -> cell.setCellValue(((Number) val).doubleValue()));
            put(Integer.class, (Cell cell, Object val) -> cell.setCellValue((Integer) val));
            put(Double.class, (Cell cell, Object val) -> cell.setCellValue((Double) val));
            put(BigDecimal.class, (Cell cell, Object val) -> cell.setCellValue(((BigDecimal) val).doubleValue()));
            put(Boolean.class, (Cell cell, Object val) -> cell.setCellValue((Boolean) val));
            put(Timestamp.class, (Cell cell, Object val) -> cell.setCellValue((Timestamp) val));
            put(Object.class, (Cell cell, Object val) -> cell.setCellValue(val == null ? EMPTY : val.toString()));
        }
    });

    /**
     * Sets the value in the specified {@link Cell} based on the runtime type of the provided value.
     *
     * <p>This method retrieves the appropriate handler from the {@link #handlers} map based on the
     * runtime type of the value and applies it to the cell. If no specific handler is found for the
     * value's type, the default handler for {@link Object} is used, which converts the value to a string.
     *
     * @param cell  the cell in which the value is to be set
     * @param value the value to be set in the cell
     */
    default void setCellValue(Cell cell, Object value) {
        handlers.get(value.getClass()).accept(cell, value);
    }

}
