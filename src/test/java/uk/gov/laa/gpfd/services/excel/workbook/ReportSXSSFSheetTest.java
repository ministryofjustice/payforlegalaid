package uk.gov.laa.gpfd.services.excel.workbook;

import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportSXSSFSheetTest {

    @Test
    void flushRows_shouldRetainRemainingRows() throws Exception {
        try (var xssfWorkbook = new XSSFWorkbook();
             var streamingWorkbook = new SXSSFWorkbook(xssfWorkbook, 100)) {
            var xSheet = xssfWorkbook.createSheet("Test");
            var reportSheet = new ReportSXSSFSheet(streamingWorkbook, xSheet);
            reportSheet.createRow(0);
            reportSheet.createRow(1);
            reportSheet.createRow(2);

            var rows = getRows(reportSheet);
            assertEquals(3, rows.size());

            reportSheet.flushRows(1);

            assertEquals(1, rows.size());
            assertEquals(Integer.valueOf(2), rows.firstKey());
        }
    }

    @Test
    void flushRows_zeroRemaining_shouldMarkAllFlushed() throws Exception {
        try (var xssfWorkbook = new XSSFWorkbook();
             var streamingWorkbook = new SXSSFWorkbook(xssfWorkbook, 100)) {
            var xSheet = xssfWorkbook.createSheet("Test");
            var reportSheet = new ReportSXSSFSheet(streamingWorkbook, xSheet);
            reportSheet.createRow(0);
            reportSheet.createRow(1);
            reportSheet.createRow(2);

            reportSheet.flushRows(0);

            assertTrue(isAllFlushed(reportSheet));
        }
    }

    @SuppressWarnings("unchecked")
    private static TreeMap<Integer, SXSSFRow> getRows(ReportSXSSFSheet sheet) throws Exception {
        Field rowsField = SXSSFSheet.class.getDeclaredField("_rows");
        rowsField.setAccessible(true);
        return (TreeMap<Integer, SXSSFRow>) rowsField.get(sheet);
    }

    private static boolean isAllFlushed(ReportSXSSFSheet sheet) throws Exception {
        Field allFlushedField = SXSSFSheet.class.getDeclaredField("allFlushed");
        allFlushedField.setAccessible(true);
        return allFlushedField.getBoolean(sheet);
    }
}