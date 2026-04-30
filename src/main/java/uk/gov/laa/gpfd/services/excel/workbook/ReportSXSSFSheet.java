package uk.gov.laa.gpfd.services.excel.workbook;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.OoxmlSheetExtensions;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import uk.gov.laa.gpfd.model.excel.ExcelSheet;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.TreeMap;

/**
 * A specialized streaming Excel sheet implementation that extends {@link SXSSFSheet} with
 * enhanced row flushing capabilities using low-level memory operations.
 *
 * <p>This implementation provides more direct control over row flushing behavior compared to
 * the standard {@link SXSSFSheet}, particularly for memory-constrained environments where
 * precise control over row retention is required.</p>
 */
public class ReportSXSSFSheet extends SXSSFSheet implements Sheet, OoxmlSheetExtensions {

    /**
     * Cached reflection field for the rows TreeMap field.
     */
    private static final Field ROWS_FIELD;

    /**
     * Cached reflection field for the allFlushed boolean field.
     */
    private static final Field ALL_FLUSHED_FIELD;

    /**
     * Cached reflection field for the lastFlushedRowNumber field.
     */
    private static final Field LAST_FLUSHED_ROW_FIELD;

    static {
        try {
            ROWS_FIELD = SXSSFSheet.class.getDeclaredField("_rows");
            ROWS_FIELD.setAccessible(true);
            ALL_FLUSHED_FIELD = SXSSFSheet.class.getDeclaredField("allFlushed");
            ALL_FLUSHED_FIELD.setAccessible(true);
            LAST_FLUSHED_ROW_FIELD = SXSSFSheet.class.getDeclaredField("lastFlushedRowNumber");
            LAST_FLUSHED_ROW_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Creates a new ReportSXSSFSheet backed by the specified XSSFSheet.
     *
     * @param workbook the parent workbook
     * @param xSheet   the backing XSSFSheet
     * @throws IOException if the sheet cannot be created
     */
    public ReportSXSSFSheet(SXSSFWorkbook workbook, XSSFSheet xSheet, ExcelSheet excelSheet) throws IOException {
        super(workbook, xSheet);
    }

    /**
     * Flushes rows from memory until only the specified number of rows remain.
     * If remaining is 0, marks all rows as flushed.
     *
     * @param remaining the number of rows to keep in memory
     * @throws IOException if an error occurs while writing rows
     */
    @Override
    public void flushRows(int remaining) throws IOException {
        var rows = getRows();
        while (rows.size() > remaining) {
            flushOneRow(rows);
        }
        if (remaining == 0) {
            try {
                ALL_FLUSHED_FIELD.set(this, true);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Failed to set allFlushed field", e);
            }
        }
    }

    /**
     * Gets the current rows map using reflection.
     *
     * @return TreeMap containing the current rows
     */
    @SuppressWarnings("unchecked")
    private TreeMap<Integer, SXSSFRow> getRows() {
        try {
            return (TreeMap<Integer, SXSSFRow>) ROWS_FIELD.get(this);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to read _rows field", e);
        }
    }

    /**
     * Flushes a single row from the sheet.
     *
     * @param rows the rows map to operate on
     * @throws IOException if an error occurs while writing the row
     */
    private void flushOneRow(TreeMap<Integer, SXSSFRow> rows) throws IOException {
        if (rows.isEmpty()) return;

        var firstRowNum = rows.firstKey();
        var row = rows.get(firstRowNum);

        _writer.writeRow(firstRowNum, row);
        rows.remove(firstRowNum);
        try {
            LAST_FLUSHED_ROW_FIELD.set(this, firstRowNum);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to set lastFlushedRowNumber field", e);
        }
    }
}