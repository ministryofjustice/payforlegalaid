package uk.gov.laa.gpfd.services.excel;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.laa.gpfd.model.Report;
import uk.gov.laa.gpfd.model.ReportQuery;

import uk.gov.laa.gpfd.dao.ReportViewsDao;
import uk.gov.laa.gpfd.services.TemplateService;
import uk.gov.laa.gpfd.services.excel.editor.SheetDataWriter;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExcelCreationServiceTest {

    @Mock
    private TemplateService templateLoader;

    @Mock
    private ReportViewsDao dataFetcher;

    @Mock
    private SheetDataWriter sheetDataWriter;

    @InjectMocks
    private ExcelCreationService excelCreationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldBuildExcelWithData() {
        // Given
        var workbook = mock(Workbook.class);
        var query = new ReportQuery() {{
            setTabName("Sheet1");
            setQuery("SELECT * FROM table");
        }};
        var report = new Report() {{
            setTemplateSecureDocumentId("TEMPLATE_123");
            setQueries(Collections.singletonList(query));
        }};

        when(templateLoader.findTemplateById("TEMPLATE_123")).thenReturn(workbook);
        when(dataFetcher.callDataBase("SELECT * FROM table")).thenReturn(Collections.emptyList());

        // When
        var result = excelCreationService.buildExcel(report);

        // Then
        assertNotNull(result);
        assertEquals(workbook, result);

        // Verify interactions
        verify(templateLoader).findTemplateById("TEMPLATE_123");
    }

    @Test
    void shouldNotWriteDataWhenSheetNotFound() {
        // Given
        var workbook = mock(Workbook.class);
        var query = new ReportQuery() {{
            setTabName("NonExistentSheet");
            setQuery("SELECT * FROM table");
        }};
        var report = new Report(){{
            setTemplateSecureDocumentId("TEMPLATE_123");
            setQueries(Collections.singletonList(query));
        }};

        when(templateLoader.findTemplateById("TEMPLATE_123")).thenReturn(workbook);

        // When
        var result = excelCreationService.buildExcel(report);

        // Then
        assertNotNull(result);
        assertEquals(workbook, result);

        // Verify interactions
        verify(templateLoader).findTemplateById("TEMPLATE_123");
        verify(sheetDataWriter, never()).writeDataToSheet(any(), any(), any());
    }

    @Test
    void shouldHandleEmptyQueries() {
        // Given
        var workbook = mock(Workbook.class);
        var report = new Report() {{
            setTemplateSecureDocumentId("TEMPLATE_123");
            setQueries(Collections.emptyList());
        }};

        when(templateLoader.findTemplateById("TEMPLATE_123")).thenReturn(workbook);

        // When
        var result = excelCreationService.buildExcel(report);

        // Then
        assertNotNull(result);
        assertEquals(workbook, result);

        // Verify interactions
        verify(templateLoader).findTemplateById("TEMPLATE_123");
        verify(sheetDataWriter, never()).writeDataToSheet(any(), any(), any());
    }

    @Test
    void shouldHandleMultipleQueries() {
        // Given
        var workbook = mock(Workbook.class);
        var query1 = new ReportQuery() {{
            setTabName("Sheet1");
            setQuery("SELECT * FROM table1");
        }};
        var query2 = new ReportQuery() {{
            setTabName("Sheet2");
            setQuery("SELECT * FROM table2");
        }};
        var report = new Report() {{
            setTemplateSecureDocumentId("TEMPLATE_123");
            setQueries(List.of(query1, query2));
        }};

        when(templateLoader.findTemplateById("TEMPLATE_123")).thenReturn(workbook);
        when(dataFetcher.callDataBase("SELECT * FROM table1")).thenReturn(Collections.emptyList());
        when(dataFetcher.callDataBase("SELECT * FROM table2")).thenReturn(Collections.emptyList());

        // When
        var result = excelCreationService.buildExcel(report);

        // Then
        assertNotNull(result);
        assertEquals(workbook, result);

        // Verify interactions
        verify(templateLoader).findTemplateById("TEMPLATE_123");
    }
}