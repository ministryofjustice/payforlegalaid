package uk.gov.laa.gpfd.services.excel.workbook;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.OoxmlSheetExtensions;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import sun.misc.Unsafe;
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
     * The Unsafe instance for low-level memory operations.
     */
    private static final Unsafe UNSAFE = getUnsafe();

    /**
     * Memory offset for the rows TreeMap field.
     */
    private static final long ROWS_OFFSET;

    /**
     * Memory offset for the allFlushed boolean field.
     */
    private static final long ALL_FLUSHED_OFFSET;

    /**
     * Memory offset for the lastFlushedRowNumber field.
     */
    private static final long LAST_FLUSHED_ROW_OFFSET;

    private final ExcelSheet excelSheet;

    static {
        try {
            ROWS_OFFSET = UNSAFE.objectFieldOffset(SXSSFSheet.class.getDeclaredField("_rows"));
            ALL_FLUSHED_OFFSET = UNSAFE.objectFieldOffset(SXSSFSheet.class.getDeclaredField("allFlushed"));
            LAST_FLUSHED_ROW_OFFSET = UNSAFE.objectFieldOffset(SXSSFSheet.class.getDeclaredField("lastFlushedRowNumber"));
        } catch (Exception e) {
            throw new Error("Failed to initialize field offsets", e);
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
        this.excelSheet = excelSheet;
    }

    /**
     * Gets the Unsafe instance
     *
     * @return the Unsafe instance
     * @throws Error if Unsafe is not available
     */
    private static Unsafe getUnsafe() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe) theUnsafe.get(null);
        } catch (Exception e) {
            throw new Error("Unsafe not available", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SXSSFRow createRow(int rownum) {
        return super.createRow(rownum);
    }

    /**
     * Flushes rows from memory until only the specified number of rows remain.
     * If remaining is 0, marks all rows as flushed.
     *
     * @param remaining the number of rows to keep in memory
     * @throws IOException if an error occurs while writing rows
     */
    public void flushRows(int remaining) throws IOException {
        var rows = getRows();
        while (rows.size() > remaining) {
            flushOneRow(rows);
        }
        if (remaining == 0) {
            UNSAFE.putBoolean(this, ALL_FLUSHED_OFFSET, true);
        }
    }

    /**
     * Gets the current rows map using direct memory access.
     *
     * @return TreeMap containing the current rows
     */
    @SuppressWarnings("unchecked")
    private TreeMap<Integer, SXSSFRow> getRows() {
        return (TreeMap<Integer, SXSSFRow>) UNSAFE.getObject(this, ROWS_OFFSET);
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
        UNSAFE.putInt(this, LAST_FLUSHED_ROW_OFFSET, firstRowNum);
    }
}