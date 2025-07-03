package uk.gov.laa.gpfd.services.excel.copier;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.gpfd.exception.ReportGenerationException.InvalidWorkbookTypeException;
import uk.gov.laa.gpfd.exception.ReportGenerationException.SheetCopyException;
import uk.gov.laa.gpfd.exception.ReportGenerationException.SheetNotFoundException;
import uk.gov.laa.gpfd.services.excel.copier.types.basic.BasicSheetCopier;
import uk.gov.laa.gpfd.services.excel.copier.types.xssf.XSSFSheetCopier;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SheetCopierFactoryTest {

    @Mock
    private XSSFWorkbook xssfSourceWorkbook;

    @Mock
    private Workbook nonXssfSourceWorkbook;

    @Mock
    private SXSSFWorkbook sxssfTargetWorkbook;

    @Mock
    private XSSFWorkbook xssfTargetWorkbook;

    @Mock
    private XSSFSheet sourceSheet;

    @Mock
    private XSSFSheet targetSheet;

    @Test
    void createCopier_shouldThrowWhenSourceWorkbookIsNull() {
        assertThrows(NullPointerException.class,
                () -> SheetCopierFactory.createCopier(null, sxssfTargetWorkbook, "Sheet1"));
    }

    @Test
    void createCopier_shouldThrowWhenTargetWorkbookIsNull() {
        assertThrows(NullPointerException.class,
                () -> SheetCopierFactory.createCopier(xssfSourceWorkbook, null, "Sheet1"));
    }

    @Test
    void createCopier_shouldThrowWhenSheetNameIsNull() {
        assertThrows(NullPointerException.class,
                () -> SheetCopierFactory.createCopier(xssfSourceWorkbook, sxssfTargetWorkbook, null));
    }

    @Test
    void createCopier_shouldThrowWhenTargetNotSXSSFWorkbook() {
        assertThrows(InvalidWorkbookTypeException.class,
                () -> SheetCopierFactory.createCopier(xssfSourceWorkbook, xssfTargetWorkbook, "Sheet1"));
    }

    @Test
    void createCopier_shouldThrowWhenSheetNotFound() {
        when(xssfSourceWorkbook.getSheet("Sheet1")).thenReturn(null);

        assertThrows(SheetNotFoundException.class,
                () -> SheetCopierFactory.createCopier(xssfSourceWorkbook, sxssfTargetWorkbook, "Sheet1"));
    }

    @Test
    void createCopier_shouldCreateXSSFSheetCopierForXSSFSource() throws Exception {
        when(xssfSourceWorkbook.getSheet("Sheet1")).thenReturn(sourceSheet);
        when(sxssfTargetWorkbook.getXSSFWorkbook()).thenReturn(xssfTargetWorkbook);
        when(xssfTargetWorkbook.createSheet("Sheet1")).thenReturn(targetSheet);

        var copier = SheetCopierFactory.createCopier(xssfSourceWorkbook, sxssfTargetWorkbook, "Sheet1");

        assertInstanceOf(XSSFSheetCopier.class, copier);
        verify(targetSheet).setAutobreaks(false);
    }

    @Test
    void createCopier_shouldCreateBasicSheetCopierForNonXSSFSource() throws Exception {
        when(nonXssfSourceWorkbook.getSheet("Sheet1")).thenReturn(sourceSheet);
        when(sxssfTargetWorkbook.getXSSFWorkbook()).thenReturn(xssfTargetWorkbook);
        when(xssfTargetWorkbook.createSheet("Sheet1")).thenReturn(targetSheet);

        var copier = SheetCopierFactory.createCopier(nonXssfSourceWorkbook, sxssfTargetWorkbook, "Sheet1");

        assertInstanceOf(BasicSheetCopier.class, copier);
        verify(targetSheet).setAutobreaks(false);
    }

    @Test
    void createCopier_shouldWrapExceptionsInSheetCopyException() {
        when(xssfSourceWorkbook.getSheet("Sheet1")).thenReturn(sourceSheet);
        when(sxssfTargetWorkbook.getXSSFWorkbook()).thenThrow(new RuntimeException("Test error"));

        var exception = assertThrows(SheetCopyException.class,
                () -> SheetCopierFactory.createCopier(xssfSourceWorkbook, sxssfTargetWorkbook, "Sheet1"));

        assertEquals("Failed to create sheet copier", exception.getMessage());
        assertNotNull(exception.getCause());
    }
}