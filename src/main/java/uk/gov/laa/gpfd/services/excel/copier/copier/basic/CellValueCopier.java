package uk.gov.laa.gpfd.services.excel.copier.copier.basic;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Enumeration of cell value copiers that handle copying different cell types between Excel cells.
 * Each copier knows how to properly transfer values of a specific {@link CellType}.
 */
public enum CellValueCopier implements BiConsumer<Cell, Cell> {
    /**
     * Handles string cell values.
     */
    STRING(CellType.STRING) {
        @Override
        public void accept(Cell source, Cell target) {
            target.setCellValue(source.getStringCellValue());
        }
    },
    /**
     * Handles numeric cell values.
     */
    NUMERIC(CellType.NUMERIC) {
        @Override
        public void accept(Cell source, Cell target) {
            target.setCellValue(source.getNumericCellValue());
        }
    },
    /**
     * Handles boolean cell values.
     */
    BOOLEAN(CellType.BOOLEAN) {
        @Override
        public void accept(Cell source, Cell target) {
            target.setCellValue(source.getBooleanCellValue());
        }
    },
    /**
     * Handles formula cell values.
     */
    FORMULA(CellType.FORMULA) {
        @Override
        public void accept(Cell source, Cell target) {
            target.setCellFormula(source.getCellFormula());
        }
    },
    /**
     * Handles blank cells.
     */
    BLANK(CellType.BLANK) {
        @Override
        public void accept(Cell source, Cell target) {
            target.setBlank();
        }
    },
    /**
     * Handles error cell values.
     */
    ERROR(CellType.ERROR) {
        @Override
        public void accept(Cell source, Cell target) {
            target.setCellErrorValue(source.getErrorCellValue());
        }
    },
    /**
     * Default handler for unknown cell types.
     */
    DEFAULT(null) {
        @Override
        public void accept(Cell source, Cell target) {
            // No-op for unknown types
        }
    };

    private static final Map<CellType, CellValueCopier> HANDLER;

    static {
        var map = new EnumMap<CellType, CellValueCopier>(CellType.class);
        for (var copier : values()) {
            if (null != copier.type) {
                map.put(copier.type, copier);
            }
        }
        HANDLER = Collections.unmodifiableMap(map);
    }

    private final CellType type;

    CellValueCopier(CellType type) {
        this.type = type;
    }

    /**
     * Copies the value from source cell to target cell based on cell type.
     */
    public static void copyValue(Cell source, Cell target) {
        CellValueCopier copier = HANDLER.getOrDefault(source.getCellType(), DEFAULT);
        copier.accept(source, target);
    }

}