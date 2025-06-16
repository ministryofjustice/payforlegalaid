package uk.gov.laa.gpfd.services.excel;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import uk.gov.laa.gpfd.dao.JdbcWorkbookDataStreamer;
import uk.gov.laa.gpfd.data.ReportsTestDataFactory;
import uk.gov.laa.gpfd.model.ImmutableReportQuery;
import uk.gov.laa.gpfd.model.ReportQuerySql;
import uk.gov.laa.gpfd.model.excel.ExcelTemplate;
import uk.gov.laa.gpfd.model.excel.ImmutableExcelSheet;
import uk.gov.laa.gpfd.services.TemplateService;
import uk.gov.laa.gpfd.services.excel.editor.FormulaCalculator;
import uk.gov.laa.gpfd.services.excel.editor.PivotTableRefresher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExcelCreationServiceTest {
    private static final String UUID = "123da9ec-b0b3-4371-af10-f375330d85d3";
    private static final ExcelTemplate TEMPLATE_DOCUMENT = ExcelTemplate.fromString(UUID);

    @Spy
    private TemplateService templateLoader;

    @Mock
    private JdbcWorkbookDataStreamer dataFetcher;

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
                .excelSheet(ImmutableExcelSheet.builder()
                        .name("Sheet1")
                        .fieldAttributes(Collections.emptyList())
                        .build())
                .query(ReportQuerySql.of("SELECT * FROM ANY_REPORT.TABLE"))
                .build();

        var report = ReportsTestDataFactory.createTestReport(UUID, Collections.singletonList(query));

        when(templateLoader.findTemplateById(TEMPLATE_DOCUMENT)).thenReturn(workbook);
        when(workbook.createSheet(any())).thenReturn(mock(Sheet.class));

        // When
        excelCreationService.stream(report, outputStream);

        // Then
        verify(templateLoader).findTemplateById(TEMPLATE_DOCUMENT);
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
                .excelSheet(ImmutableExcelSheet.builder()
                        .name("NonExistentSheet")
                        .fieldAttributes(Collections.emptyList())
                        .build())
                .query(ReportQuerySql.of("SELECT * FROM ANY_REPORT.TABLE"))
                .build();
        var report = ReportsTestDataFactory.createTestReport(UUID, Collections.singletonList(query));

        when(templateLoader.findTemplateById(TEMPLATE_DOCUMENT)).thenReturn(workbook);
        when(workbook.createSheet(any())).thenReturn(mock(Sheet.class));

        // When
        excelCreationService.stream(report, outputStream);

        // Then
        verify(templateLoader).findTemplateById(TEMPLATE_DOCUMENT);
        verify(workbook).write(outputStream);
    }

    @Test
    void shouldHandleEmptyQueries() throws IOException {
        // Given
        var workbook = mock(Workbook.class);
        var outputStream = new ByteArrayOutputStream();
        var report = ReportsTestDataFactory.createTestReport(UUID, Collections.emptyList());

        when(templateLoader.findTemplateById(TEMPLATE_DOCUMENT)).thenReturn(workbook);

        // When
        excelCreationService.stream(report, outputStream);

        // Then
        verify(templateLoader).findTemplateById(TEMPLATE_DOCUMENT);
        verify(pivotTableRefresher).refreshPivotTables(workbook);
        verify(formulaCalculator).evaluateAllFormulaCells(workbook);
        verify(workbook).write(outputStream);
    }

    @Test
    void shouldHandleMultipleQueries() throws IOException {
        // Given
        var workbook = mock(Workbook.class);
        var outputStream = new ByteArrayOutputStream();
        var query1 = ImmutableReportQuery.builder()
                .excelSheet(ImmutableExcelSheet.builder()
                        .name("Sheet1")
                        .fieldAttributes(Collections.emptyList())
                        .build())
                .query(ReportQuerySql.of("SELECT * FROM ANY_REPORT.TABLE"))
                .build();
        var query2 = ImmutableReportQuery.builder()
                .excelSheet(ImmutableExcelSheet.builder()
                        .name("Sheet2")
                        .fieldAttributes(Collections.emptyList())
                        .build())
                .query(ReportQuerySql.of("SELECT * FROM ANY_REPORT.TABLE2"))
                .build();

        var report = ReportsTestDataFactory.createTestReport(UUID, List.of(query1, query2));

        when(templateLoader.findTemplateById(TEMPLATE_DOCUMENT)).thenReturn(workbook);
        when(workbook.createSheet(any())).thenReturn(mock(Sheet.class));

        // When
        excelCreationService.stream(report, outputStream);

        // Then
        verify(templateLoader).findTemplateById(ExcelTemplate.fromString(UUID));
        verify(pivotTableRefresher).refreshPivotTables(workbook);
        verify(formulaCalculator).evaluateAllFormulaCells(workbook);
        verify(workbook).write(outputStream);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenIOExceptionOccurs() throws IOException {
        // Given
        var workbook = mock(Workbook.class);
        var outputStream = new ByteArrayOutputStream();
        var report = ReportsTestDataFactory.createTestReport(UUID, Collections.emptyList());

        when(templateLoader.findTemplateById(TEMPLATE_DOCUMENT)).thenReturn(workbook);
        doThrow(new IOException("Test exception")).when(workbook).write(outputStream);

        // When/Then
        assertThrows(RuntimeException.class, () -> excelCreationService.stream(report, outputStream));
    }
}
