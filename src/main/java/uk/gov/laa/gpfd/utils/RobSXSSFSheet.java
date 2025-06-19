package uk.gov.laa.gpfd.utils;

import lombok.SneakyThrows;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class RobSXSSFSheet extends SXSSFSheet {

    private int _rowNum;

    public RobSXSSFSheet(SXSSFWorkbook workbook, XSSFSheet xSheet) throws IOException {
        super(workbook, xSheet);
    }

    @Override
    public SXSSFRow createRow(int rownum) {
        return super.createRow(rownum);
//        SXSSFRow newRow = new SXSSFRow(this);
//        Map<SXSSFSheet,XSSFSheet> o = (Map<SXSSFSheet, XSSFSheet>) sheetsField.get(this);
//        o.put(sxSheet,xSheet);
//        sheetsField.set(this, o);
//
//        newRow.setRowNumWithoutUpdatingSheet(rownum);
//        _rows.put(rownum, newRow);
//        allFlushed = false;
//        if(_randomAccessWindowSize >= 0 && _rows.size() > _randomAccessWindowSize) {
//            try {
//                flushRows(_randomAccessWindowSize);
//            } catch (IOException ioe) {
//                throw new IllegalStateException(ioe);
//            }
//        }
//        return newRow;
    }

    @SneakyThrows
    public void flushRows(int remaining) throws IOException {
        var sheetsField = SXSSFSheet.class.getDeclaredField("_rows");
        sheetsField.setAccessible(true);
        TreeMap<Integer,SXSSFRow> o = (TreeMap<Integer,SXSSFRow>) sheetsField.get(this);
        while(o.size() > remaining) {
            flushOneRow();
        }
        if (remaining == 0) {
            var foo = SXSSFSheet.class.getDeclaredField("allFlushed");
            foo.setAccessible(true);
            foo.set(this, true);
        }
    }

    @SneakyThrows
    private void flushOneRow() throws IOException {
        var sheetsField = SXSSFSheet.class.getDeclaredField("_rows");
        sheetsField.setAccessible(true);
        TreeMap<Integer,SXSSFRow> _rows = (TreeMap<Integer,SXSSFRow>) sheetsField.get(this);
        Integer firstRowNum = _rows.firstKey();
        if (firstRowNum!=null) {
            int rowIndex = firstRowNum;
            SXSSFRow row = _rows.get(firstRowNum);

            _writer.writeRow(rowIndex, row);
            _rows.remove(firstRowNum);
            sheetsField.set(this, _rows);
            var lastFlushedRowNumber = SXSSFSheet.class.getDeclaredField("lastFlushedRowNumber");
            lastFlushedRowNumber.setAccessible(true);
            lastFlushedRowNumber.set(this, rowIndex);
        }
    }

}
