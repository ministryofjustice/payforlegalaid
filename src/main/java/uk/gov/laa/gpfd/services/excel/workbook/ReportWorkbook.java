package uk.gov.laa.gpfd.services.excel.workbook;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.streaming.SheetDataWriter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import sun.misc.Unsafe;
import uk.gov.laa.gpfd.model.Report;
import uk.gov.laa.gpfd.model.ReportQuery;

import java.io.IOException;
import java.util.Map;

/**
 * A specialized streaming workbook implementation that extends {@link SXSSFWorkbook} with
 * additional functionality for report generation. This class provides optimized sheet creation
 * and maintains bidirectional mappings between streaming sheets (SXSSF) and their backing
 * XSSF sheets using low-level memory operations via {@link sun.misc.Unsafe}.
 */
public class ReportWorkbook extends SXSSFWorkbook implements Workbook {
    private static final String SX_FROM_X_FIELD = "_sxFromXHash";
    private static final String X_FROM_SX_FIELD = "_xFromSxHash";
    private static final Unsafe UNSAFE = getUnsafe();
    private static final long SX_FROM_X_HASH_OFFSET;
    private static final long X_FROM_SX_HASH_OFFSET;
    private final Report report;
    private final StyleManager styleManager;

    static {
        try {
            SX_FROM_X_HASH_OFFSET = UNSAFE.objectFieldOffset(SXSSFWorkbook.class.getDeclaredField(SX_FROM_X_FIELD));
            X_FROM_SX_HASH_OFFSET = UNSAFE.objectFieldOffset(SXSSFWorkbook.class.getDeclaredField(X_FROM_SX_FIELD));
        } catch (NoSuchFieldException e) {
            throw new Error(e);
        }
    }

    public ReportWorkbook(Report report, StyleManager styleManager) {
        this.report = report;
        this.styleManager = styleManager;
    }

    private static Unsafe getUnsafe() {
        try {
            var theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe) theUnsafe.get(null);
        } catch (Exception e) {
            throw new Error("Unsafe not available", e);
        }
    }

    /**
     * Creates a new sheet with the specified name and registers it in the internal mappings.
     *
     * @param sheetName the name of the sheet
     * @return the newly created SXSSFSheet
     * @throws IllegalStateException if sheet creation fails
     */
    @Override
    public SXSSFSheet createSheet(String sheetName) {
        return createAndRegisterSheet(_wb.createSheet(sheetName));
    }

    /**
     * Creates a new sheet with a default name and registers it in the internal mappings.
     *
     * @return the newly created SXSSFSheet
     * @throws IllegalStateException if sheet creation fails
     */
    @Override
    public SXSSFSheet createSheet() {
        return createAndRegisterSheet(_wb.createSheet());
    }

    ReportQuery reportQuery;
    /**
     * Creates and registers a new SXSSFSheet from the specified XSSFSheet.
     *
     * @param xSheet the backing XSSFSheet
     * @return the newly created SXSSFSheet
     * @throws IllegalStateException if sheet creation fails
     */
    private SXSSFSheet createAndRegisterSheet(XSSFSheet xSheet) {
        try {
            reportQuery = report.extractAllMappings().stream().filter(e -> e.getExcelSheet().getName().equals(xSheet.getSheetName())).findFirst().orElse(null);
            var sxSheet = new ReportSXSSFSheet(this, xSheet, null);
            registerMapping(sxSheet, xSheet);
            return sxSheet;
        } catch (IOException ioe) {
            throw new IllegalStateException("Failed to create sheet", ioe);
        }
    }

    /**
     * Creates a specialized SheetDataWriter for this workbook.
     *
     * @return a new ReportSheetDataWriter instance
     * @throws IOException if the writer cannot be created
     */
    @Override
    protected SheetDataWriter createSheetDataWriter() throws IOException {
        return new ReportSheetDataWriter(_sharedStringSource, reportQuery, styleManager);
    }

    /**
     * Registers bidirectional mappings between an SXSSFSheet and its backing XSSFSheet.
     *
     * @param sxSheet the streaming sheet
     * @param xSheet  the backing XSSF sheet
     */
    private void registerMapping(SXSSFSheet sxSheet, XSSFSheet xSheet) {
        updateSheetMapping(SX_FROM_X_FIELD, sxSheet, xSheet);
        updateSheetMapping(X_FROM_SX_FIELD, xSheet, sxSheet);
    }

    /**
     * Updates the internal sheet mapping with the specified key-value pair.
     *
     * @param <K>       the key type
     * @param <V>       the value type
     * @param fieldName the name of the field containing the mapping
     * @param key       the mapping key
     * @param value     the mapping value
     */
    @SuppressWarnings("unchecked")
    private <K, V> void updateSheetMapping(String fieldName, K key, V value) {
        var offset = fieldName.equals(SX_FROM_X_FIELD) ? SX_FROM_X_HASH_OFFSET : X_FROM_SX_HASH_OFFSET;

        var map = (Map<K, V>) UNSAFE.getObject(this, offset);
        map.put(key, value);
        UNSAFE.putObject(this, offset, map);
    }

}