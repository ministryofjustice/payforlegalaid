package uk.gov.laa.gpfd.services.excel.editor;

import org.apache.poi.ss.usermodel.Cell;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static java.lang.Double.parseDouble;
import static java.lang.Long.parseLong;
import static java.util.Arrays.stream;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * An enumeration of cell value handlers that implement different strategies for setting values
 * in Excel cells based on the Java type of the value. Each handler knows how to properly format
 * and store a specific type of value in an Excel cell.
 *
 * <p>This enum implements {@link BiConsumer} to allow direct usage as a consumer of cell-value pairs.
 * The handlers are maintained in a {@link LinkedHashMap} to preserve insertion order when iterating.
 */
public enum CellValueHandler implements BiConsumer<Cell, Object> {

    /**
     * Handler for empty/null values. Sets the cell to an empty string.
     */
    EMPTY(null) {
        private static final String EMPTY = "";

        @Override
        public void accept(Cell cell, Object value) {
            cell.setCellValue(EMPTY);
        }
    },

    /**
     * Handler for string values. Automatically detects numeric strings and converts them
     * to appropriate numeric cell values. Other strings are stored as-is.
     */
    STRING(String.class) {
        private static final String NUMBER_PATTERN = "-?\\d+(\\.\\d+)?";

        @Override
        public void accept(Cell cell, Object value) {
            String strVal = (String) value;
            if (strVal.matches(NUMBER_PATTERN)) {
                try {
                    cell.setCellValue(strVal.contains(".") ? parseDouble(strVal) : parseLong(strVal));
                } catch (NumberFormatException e) {
                    cell.setCellValue(strVal);
                }
            } else {
                cell.setCellValue(strVal);
            }
        }
    },

    /**
     * Handler for generic number values. Converts all numbers to double values.
     */
    NUMBER(Number.class) {
        @Override
        public void accept(Cell cell, Object value) {
            cell.setCellValue(((Number) value).doubleValue());
        }
    },

    /**
     * Handler for integer values. Stores values as exact integers.
     */
    INTEGER(Integer.class) {
        @Override
        public void accept(Cell cell, Object value) {
            cell.setCellValue((Integer) value);
        }
    },

    /**
     * Handler for double values. Stores values as floating-point numbers.
     */
    DOUBLE(Double.class) {
        @Override
        public void accept(Cell cell, Object value) {
            cell.setCellValue((Double) value);
        }
    },

    /**
     * Handler for BigDecimal values. Converts to double while preserving precision.
     */
    BIG_DECIMAL(BigDecimal.class) {
        @Override
        public void accept(Cell cell, Object value) {
            cell.setCellValue(((BigDecimal) value).doubleValue());
        }
    },

    /**
     * Handler for boolean values. Stores values as Excel boolean cells.
     */
    BOOLEAN(Boolean.class) {
        @Override
        public void accept(Cell cell, Object value) {
            cell.setCellValue((Boolean) value);
        }
    },

    /**
     * Handler for timestamp values. Stores values as Excel date/time cells.
     */
    TIMESTAMP(Timestamp.class) {
        @Override
        public void accept(Cell cell, Object value) {
            cell.setCellValue((Timestamp) value);
        }
    },

    /**
     * Default handler for all other object types. Uses toString() conversion.
     */
    OBJECT(Object.class) {
        @Override
        public void accept(Cell cell, Object value) {
            cell.setCellValue(value != null ? value.toString() : "");
        }
    };

    /**
     * A map of target types to their corresponding handlers, preserving declaration order.
     * The map is initialized once when the enum is loaded and is immutable thereafter.
     */
    public static final Map<Class<?>, CellValueHandler> CellValueHandlerMap =
            Collections.unmodifiableMap(stream(values()).collect(toMap(
                    handler -> handler.targetType,
                    identity(),
                    (existing, replacement) -> existing,
                    LinkedHashMap::new
            )));

    private final Class<?> targetType;

    CellValueHandler(Class<?> targetType) {
        this.targetType = targetType;
    }
}