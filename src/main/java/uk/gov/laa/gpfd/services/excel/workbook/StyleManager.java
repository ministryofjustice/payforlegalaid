package uk.gov.laa.gpfd.services.excel.workbook;

import org.apache.poi.ss.usermodel.CellStyle;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages and stores cell styles for Excel columns with associated keys.
 * Provides methods to set and retrieve styles based on column index and key.
 *
 * @deprecated This style manager is not safe for concurrent report generation.
 * This stores mutable style state which can be shared between generated Excel reports
 * allowing styles to leak between reports when they are generated concurrently.
 * Use with caution in multithreaded environments.
 *
 * <p>This is being retained for now while a decision is made on whether styled reports
 * will be continued to be supported as part of DLP.
 *
 * <p>See LPF-1602 for known concurrency issues with this style manager.
 */
@SuppressWarnings("java:S1133") // Suppressing deprecation, retained pending DLP decision.
@Deprecated(forRemoval = false)
public interface StyleManager {

    /**
     * Sets a style for a specific column and key combination.
     *
     * @param columnIndex the zero-based column index
     * @param key the key associated with the style
     * @param style the CellStyle to be stored
     * @throws IllegalArgumentException if columnIndex is out of bounds
     */
    void setColumnStyle(int columnIndex, String key, CellStyle style);

    /**
     * Retrieves the style index for a specific column and key combination.
     *
     * @param columnIndex the zero-based column index
     * @param key the key associated with the style
     * @return the style index, or -1 if not found or columnIndex is invalid
     */
    int getColumnStyle(int columnIndex, String key);

    /**
     * Creates and returns a new instance of StyleManager.
     *
     * @return a new StyleManager instance
     * @deprecated This style manager is not safe for concurrent report generation.
     */
    @SuppressWarnings("java:S1133") // Suppressing deprecation, retained pending DLP decision.
    @Deprecated(forRemoval = false)
    static StyleManager create() {
        return new DefaultStyleManager();
    }

    /**
     * Implementation of StyleManager interface that stores styles in a grid structure.
     * Uses column indices and string keys to manage styles efficiently.
     * See {@link StyleManager#create()} for details on concurrency issues.
     */
    class DefaultStyleManager implements StyleManager {
        private final short[][] matrix = new short[MAX_COLUMNS][MAX_KEYS];
        private final Map<String, Integer> keyIndex = new HashMap<>();

        private static final int MAX_COLUMNS = 10_000;
        private static final int MAX_KEYS = 256;


        /**
         * {@inheritDoc}
         */
        @Override
        public void setColumnStyle(int columnIndex, String key, CellStyle style) {
            if (columnIndex < 0 || columnIndex >= MAX_COLUMNS) {
                throw new IllegalArgumentException("Column index out of bounds");
            }

            int keyIdx = getKeyIndex(key);
            matrix[columnIndex][keyIdx] = style.getIndex();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getColumnStyle(int columnIndex, String key) {
            if (columnIndex < 0 || columnIndex >= MAX_COLUMNS) {
                return -1;
            }

            var keyIdx = keyIndex.get(key);
            if (keyIdx == null) {
                return -1;
            }

            // & 0xffff converts short to int
            return matrix[columnIndex][keyIdx] & 0xffff;
        }

        /**
         * Gets or creates an index for the given key.
         *
         * @param key the style key
         * @return the index associated with the key
         * @throws IllegalStateException if the maximum number of keys is exceeded
         */
        private int getKeyIndex(String key) {
            return keyIndex.computeIfAbsent(key, k -> {
                if (keyIndex.size() >= MAX_KEYS) {
                    throw new IllegalStateException("Maximum number of unique keys exceeded");
                }
                return keyIndex.size();
            });
        }
    }
}