package uk.gov.laa.gpfd.services.excel.workbook;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.laa.gpfd.data.ReportsTestDataFactory.createTestReportWithMultipleQueries;

class ReportWorkbookTest {

    @Test
    void createSheet_withoutName_shouldReturnSXSSFSheetWithDefaultName()  throws IOException {
        try (var workbook = new ReportWorkbook(createTestReportWithMultipleQueries(), null)) {
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
        try (var workbook = new ReportWorkbook(createTestReportWithMultipleQueries(), null)) {
            var sheet = workbook.createSheet("Sheet1");
            assertNotNull(sheet);
            assertEquals("Sheet1", sheet.getSheetName());
        }
    }

}