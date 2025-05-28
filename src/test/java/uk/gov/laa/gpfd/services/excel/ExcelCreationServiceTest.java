package uk.gov.laa.gpfd.services.excel;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import uk.gov.laa.gpfd.data.ReportsTestDataFactory;
import uk.gov.laa.gpfd.model.ImmutableReportQuery;
import uk.gov.laa.gpfd.model.ReportQuerySql;
import uk.gov.laa.gpfd.services.TemplateService;
import uk.gov.laa.gpfd.services.excel.editor.FormulaCalculator;
import uk.gov.laa.gpfd.services.excel.editor.PivotTableRefresher;
import uk.gov.laa.gpfd.services.excel.editor.SheetDataWriter;
import uk.gov.laa.gpfd.dao.stream.StreamingDao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExcelCreationServiceTest {

    @Spy
    private TemplateService templateLoader;

    @Mock
    private StreamingDao<Map<String, Object>> dataFetcher;

    @Mock
    private SheetDataWriter sheetDataWriter;

    @Mock
    private PivotTableRefresher pivotTableRefresher;

    @Mock
    private FormulaCalculator formulaCalculator;

    @InjectMocks
    private ExcelCreationService excelCreationService;

    @BeforeEach
    void setUp() {
        templateLoader = new TemplateService.ExcelTemplateService(mock(), mock());
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldStreamExcelWithData() throws IOException {
        // Given
        var workbook = mock(Workbook.class);
        var outputStream = new ByteArrayOutputStream();
        var query = ImmutableReportQuery.builder()
                .sheetName("Sheet1")
                .query(ReportQuerySql.of("SELECT * FROM ANY_REPORT.TABLE"))
                .build();
        var report = ReportsTestDataFactory.createTestReport("TEMPLATE_123", Collections.singletonList(query));

        when(templateLoader.findTemplateById("TEMPLATE_123")).thenReturn(workbook);
        when(dataFetcher.queryForStream(ReportQuerySql.of("SELECT * FROM ANY_REPORT.TABLE"))).thenReturn(Stream.empty());

        // When
        excelCreationService.stream(report, outputStream);

        // Then
        verify(templateLoader).findTemplateById("TEMPLATE_123");
        verify(pivotTableRefresher).refreshPivotTables(workbook);
        verify(formulaCalculator).evaluateAllFormulaCells(workbook);
        verify(workbook).write(outputStream);
    }

    @Test
    void shouldNotWriteDataWhenSheetNotFound() throws IOException {
        // Given
        var workbook = mock(Workbook.class);
        var outputStream = new ByteArrayOutputStream();
        var query = ImmutableReportQuery.builder()
                .sheetName("NonExistentSheet")
                .query(ReportQuerySql.of("SELECT * FROM ANY_REPORT.TABLE"))
                .build();
        var report = ReportsTestDataFactory.createTestReport("TEMPLATE_123", Collections.singletonList(query));

        when(templateLoader.findTemplateById("TEMPLATE_123")).thenReturn(workbook);

        // When
        excelCreationService.stream(report, outputStream);

        // Then
        verify(templateLoader).findTemplateById("TEMPLATE_123");
        verify(sheetDataWriter, never()).writeDataToSheet(any(), any(), any());
        verify(workbook).write(outputStream);
    }

    @Test
    void shouldHandleEmptyQueries() throws IOException {
        // Given
        var workbook = mock(Workbook.class);
        var outputStream = new ByteArrayOutputStream();
        var report = ReportsTestDataFactory.createTestReport("TEMPLATE_123", Collections.emptyList());

        when(templateLoader.findTemplateById("TEMPLATE_123")).thenReturn(workbook);

        // When
        excelCreationService.stream(report, outputStream);

        // Then
        verify(templateLoader).findTemplateById("TEMPLATE_123");
        verify(pivotTableRefresher).refreshPivotTables(workbook);
        verify(formulaCalculator).evaluateAllFormulaCells(workbook);
        verify(sheetDataWriter, never()).writeDataToSheet(any(), any(), any());
        verify(workbook).write(outputStream);
    }

    @Test
    void shouldHandleMultipleQueries() throws IOException {
        // Given
        var workbook = mock(Workbook.class);
        var outputStream = new ByteArrayOutputStream();
        var query1 = ImmutableReportQuery.builder()
                .sheetName("Sheet1")
                .query(ReportQuerySql.of("SELECT * FROM ANY_REPORT.TABLE"))
                .build();
        var query2 = ImmutableReportQuery.builder()
                .sheetName("Sheet2")
                .query(ReportQuerySql.of("SELECT * FROM ANY_REPORT.TABLE2"))
                .build();

        var report = ReportsTestDataFactory.createTestReport("TEMPLATE_123", List.of(query1, query2));

        when(templateLoader.findTemplateById("TEMPLATE_123")).thenReturn(workbook);
        when(dataFetcher.queryForStream(ReportQuerySql.of("SELECT * FROM ANY_REPORT.TABLE"))).thenReturn(Stream.empty());
        when(dataFetcher.queryForStream(ReportQuerySql.of("SELECT * FROM ANY_REPORT.TABLE2"))).thenReturn(Stream.empty());

        // When
        excelCreationService.stream(report, outputStream);

        // Then
        verify(templateLoader).findTemplateById("TEMPLATE_123");
        verify(pivotTableRefresher).refreshPivotTables(workbook);
        verify(formulaCalculator).evaluateAllFormulaCells(workbook);
        verify(workbook).write(outputStream);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenIOExceptionOccurs() throws IOException {
        // Given
        var workbook = mock(Workbook.class);
        var outputStream = new ByteArrayOutputStream();
        var report = ReportsTestDataFactory.createTestReport("TEMPLATE_123", Collections.emptyList());

        when(templateLoader.findTemplateById("TEMPLATE_123")).thenReturn(workbook);
        doThrow(new IOException("Test exception")).when(workbook).write(outputStream);

        // When/Then
        assertThrows(RuntimeException.class, () -> excelCreationService.stream(report, outputStream));
    }
}