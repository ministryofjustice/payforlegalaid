package uk.gov.laa.gpfd.utils;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkbookFactoryTest {

    @Mock
    private InputStream mockInputStream;

    @Test
    void newWorkbook_WithNullInput_CreatesNewEmptyWorkbook() {
        var workbook = WorkbookFactory.newWorkbook(null);
        assertNotNull(workbook);
        assertInstanceOf(XSSFWorkbook.class, workbook);
        assertEquals(0, workbook.getNumberOfSheets());
    }

    @Test
    void newWorkbook_WithEmptyInputStream_CreatesNewEmptyWorkbook() throws IOException {
        when(mockInputStream.available()).thenReturn(0);

        var workbook = WorkbookFactory.newWorkbook(mockInputStream);
        assertNotNull(workbook);
        assertInstanceOf(XSSFWorkbook.class, workbook);
        assertEquals(0, workbook.getNumberOfSheets());
        verify(mockInputStream).available();
    }

    @Test
    void newWorkbook_WithAvailableException_CreatesNewEmptyWorkbook() throws IOException {
        when(mockInputStream.available()).thenThrow(new IOException("Stream unavailable"));

        var workbook = WorkbookFactory.newWorkbook(mockInputStream);
        assertNotNull(workbook);
        assertInstanceOf(XSSFWorkbook.class, workbook);
        assertEquals(0, workbook.getNumberOfSheets());
    }
    @Test
    void newWorkbook_WithValidExcelFile_CreatesWorkbookFromStream() throws IOException {
        byte[] excelData;
        try (var tempWorkbook = new XSSFWorkbook()) {
            tempWorkbook.createSheet("Test Sheet");
            var out = new ByteArrayOutputStream();
            tempWorkbook.write(out);
            excelData = out.toByteArray();
        }

        try (var inputStream = new ByteArrayInputStream(excelData)) {
            var workbook = WorkbookFactory.newWorkbook(inputStream);

            assertNotNull(workbook);
            assertInstanceOf(XSSFWorkbook.class, workbook);
            assertEquals(1, workbook.getNumberOfSheets());
        }
    }

    @Test
    void newWorkbook_WithValidExcelData_CreatesProperWorkbook() throws IOException {
        // Create a minimal valid XSSF workbook in memory
        byte[] excelData;
        try (var tempWorkbook = new XSSFWorkbook()) {
            tempWorkbook.createSheet("Test Sheet");
            var out = new ByteArrayOutputStream();
            tempWorkbook.write(out);
            excelData = out.toByteArray();
        }

        try (InputStream inputStream = new ByteArrayInputStream(excelData)) {
            var workbook = WorkbookFactory.newWorkbook(inputStream);
            assertNotNull(workbook);
            assertInstanceOf(XSSFWorkbook.class, workbook);
            assertEquals(1, workbook.getNumberOfSheets());
            assertEquals("Test Sheet", workbook.getSheetAt(0).getSheetName());
        }
    }


}
