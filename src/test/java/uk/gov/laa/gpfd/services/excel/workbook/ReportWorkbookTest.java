package uk.gov.laa.gpfd.services.excel.workbook;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.laa.gpfd.data.ReportsTestDataFactory.createTestReportWithMultipleFieldAttributes;

class ReportWorkbookTest {

    @Test
    void createSheet_withoutName_shouldReturnSXSSFSheetWithDefaultName()  throws IOException {
        try (var workbook = new ReportWorkbook(createTestReportWithMultipleFieldAttributes(), null)) {
            var sheet = workbook.createSheet();

            assertNotNull(sheet);
            assertNotNull(sheet.getSheetName());
            assertInstanceOf(ReportSXSSFSheet.class, sheet);
        }
    }

    @Test
    void createSheetDataWriter_shouldReturnReportSheetDataWriter() throws IOException {
        try (var workbook = new ReportWorkbook(null, null)) {
            var writer = workbook.createSheetDataWriter();
            assertInstanceOf(ReportSheetDataWriter.class, writer);
        }
    }

    @Test
    void workbook_shouldBeUsableInWorkbookContext() throws IOException {
        try (var workbook = new ReportWorkbook(createTestReportWithMultipleFieldAttributes(), null)) {
            var sheet = workbook.createSheet("Sheet1");
            assertNotNull(sheet);
            assertEquals("Sheet1", sheet.getSheetName());
        }
    }

    // Tests added below since removing Unsafe
    /**
     * When you create a sheet in Excel, POI needs to maintain an internal "directory" so it can look sheets up by name
     * later. This test creates a sheet called "TestSheet" and then immediately asks the workbook to find it by name
     * with getSheet("TestSheet"). If our reflection code that registers the sheet in that directory is broken, getSheet
     * will return null and the test fails.
     */
    @Test
    void createSheet_shouldRegisterBidirectionalMappings() throws IOException {
        try (var workbook = new ReportWorkbook(createTestReportWithMultipleFieldAttributes(), null)) {
            var sxSheet = workbook.createSheet("TestSheet");

            // If the bidirectional mappings are broken, getSheet() will return null
            assertNotNull(workbook.getSheet("TestSheet"));
            assertEquals(sxSheet, workbook.getSheet("TestSheet"));
        }
    }

    /**
     * The worry here is that creating a second sheet might accidentally overwrite the first one in the internal
     * directory. This test creates Sheet1, Sheet2, and Sheet3 then checks all three are still findable by
     * name afterwards.
     */
    @Test
    void createSheet_multipleSheets_shouldAllBeRetrievable() throws IOException {
        try (var workbook = new ReportWorkbook(createTestReportWithMultipleFieldAttributes(), null)) {
            var sheet1 = workbook.createSheet("Sheet1");
            var sheet2 = workbook.createSheet("Sheet2");
            var sheet3 = workbook.createSheet("Sheet3");

            assertNotNull(workbook.getSheet("Sheet1"));
            assertNotNull(workbook.getSheet("Sheet2"));
            assertNotNull(workbook.getSheet("Sheet3"));
            assertEquals(sheet1, workbook.getSheet("Sheet1"));
            assertEquals(sheet2, workbook.getSheet("Sheet2"));
            assertEquals(sheet3, workbook.getSheet("Sheet3"));
        }
    }

    /**
     * Excel sheets have both a name ("Sheet1") and a position (0, 1, 2...). This test checks you can find sheets by
     * their position as well as by name, and that the total count of sheets is correct.
     */
    @Test
    void createSheet_shouldBeRetrievableByIndex() throws IOException {
        try (var workbook = new ReportWorkbook(createTestReportWithMultipleFieldAttributes(), null)) {
            workbook.createSheet("Sheet1");
            workbook.createSheet("Sheet2");

            assertEquals("Sheet1", workbook.getSheetAt(0).getSheetName());
            assertEquals("Sheet2", workbook.getSheetAt(1).getSheetName());
            assertEquals(2, workbook.getNumberOfSheets());
        }
    }

    /**
     * This just confirms that both ways of creating a sheet, with a name (createSheet("Sheet1")) and
     * without (createSheet()), always return our custom ReportSXSSFSheet type rather than a plain POI
     * sheet. If POI ever changes how sheet creation works internally and our custom type stops being used,
     * this catches it.
     */
    @Test
    void createSheet_shouldReturnReportSXSSFSheetInstance() throws IOException {
        try (var workbook = new ReportWorkbook(createTestReportWithMultipleFieldAttributes(), null)) {
            var sheet1 = workbook.createSheet("Sheet1");
            var sheet2 = workbook.createSheet();

            assertInstanceOf(ReportSXSSFSheet.class, sheet1);
            assertInstanceOf(ReportSXSSFSheet.class, sheet2);
        }
    }

}