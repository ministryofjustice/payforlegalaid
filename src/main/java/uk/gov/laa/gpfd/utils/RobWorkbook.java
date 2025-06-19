package uk.gov.laa.gpfd.utils;

import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.GZIPSheetDataWriter;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.streaming.SheetDataWriter;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.io.IOException;
import java.util.Map;

public class RobWorkbook extends SXSSFWorkbook implements Workbook {

    @Override
    public SXSSFSheet createSheet(String sheetname) {
        return createAndRegisterSXSSFSheet(_wb.createSheet(sheetname));
    }

    @Override
    public SXSSFSheet createSheet() {
        return createAndRegisterSXSSFSheet(_wb.createSheet());
    }

    SXSSFSheet createAndRegisterSXSSFSheet(XSSFSheet xSheet) {
        final RobSXSSFSheet sxSheet;
        try {
            sxSheet = new RobSXSSFSheet(this, xSheet);
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }
        registerSheetMapping(sxSheet,xSheet);
        return sxSheet;
    }

    @Override
    protected SheetDataWriter createSheetDataWriter() throws IOException {
        return new RobSheetDataWriter(_sharedStringSource);
    }


    @SuppressWarnings("unchecked")
    @SneakyThrows
    void registerSheetMapping(SXSSFSheet sxSheet, XSSFSheet xSheet) {
        var sheetsField = SXSSFWorkbook.class.getDeclaredField("_sxFromXHash");
        sheetsField.setAccessible(true);

        Map<SXSSFSheet,XSSFSheet> o = (Map<SXSSFSheet, XSSFSheet>) sheetsField.get(this);
        o.put(sxSheet,xSheet);
        sheetsField.set(this, o);

        var sheetsField2 = SXSSFWorkbook.class.getDeclaredField("_xFromSxHash");
        sheetsField2.setAccessible(true);
        Map<XSSFSheet,SXSSFSheet> o1 = (Map<XSSFSheet, SXSSFSheet>) sheetsField2.get(this);
        o1.put(xSheet,sxSheet);
        sheetsField2.set(this, o1);
    }



}
