package uk.gov.laa.gpfd.services.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.gpfd.dao.JdbcWorkbookDataStreamer;
import uk.gov.laa.gpfd.data.ReportsTestDataFactory;
import uk.gov.laa.gpfd.services.TemplateService;
import uk.gov.laa.gpfd.services.excel.formatting.CellFormatter;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.laa.gpfd.data.ReportsTestDataFactory.createTestReportWithMultipleFieldAttributes;

@ExtendWith(MockitoExtension.class)
class ExcelCreationServiceTest {

    @Mock
    private JdbcWorkbookDataStreamer jdbcWorkbookDataStreamer;

    @Mock
    private TemplateService.ExcelTemplateService templateLoader;

    @Mock
    private CellFormatter formatter;

    @Mock
    private Workbook mockWorkbook;

    @Mock
    private Sheet mockSheet;

    @Mock
    private Row mockRow;

    @Mock
    private Cell mockCell;

    @InjectMocks
    private ExcelCreationService excelCreationService;

    @Test
    void stream_ShouldCreateSheetWithMapping() {
       var testReport = ReportsTestDataFactory.createTestReportWithQuery();
        when(mockWorkbook.createSheet("Sheet1")).thenReturn(mockSheet);
        when(mockSheet.createRow(0)).thenReturn(mockRow);
        when(mockRow.createCell(0)).thenReturn(mockCell);

        excelCreationService.stream(testReport, mockWorkbook);

        verify(mockWorkbook).createSheet("Sheet1");
        verify(jdbcWorkbookDataStreamer).queryToSheet(mockSheet, testReport.getQueries().stream().findFirst().get());
    }

    @Test
    void resolveTemplate_ShouldDelegateToTemplateService() {
        var report = ReportsTestDataFactory.createTestReport();
        when(templateLoader.findTemplateById(report.getTemplateDocument())).thenReturn(mockWorkbook);

        assertSame(mockWorkbook, excelCreationService.resolveTemplate(report));
        verify(templateLoader).findTemplateById(report.getTemplateDocument());
    }

    @Test
    void createEmpty_ShouldDelegateToTemplateService() {
        var report = ReportsTestDataFactory.createTestReport();
        when(templateLoader.createEmpty(report)).thenReturn(mockWorkbook);

        assertSame(mockWorkbook, excelCreationService.createEmpty(report));
        verify(templateLoader).createEmpty(report);
    }

    @Test
    void stream_ShouldApplyFormattingToHeaderCells() {
        var report = ReportsTestDataFactory.createTestReportWithQuery();
        var query = report.getQueries().iterator().next();
        var mapping = query.getExcelSheet().getFieldAttributes().iterator().next();

        when(mockWorkbook.createSheet("Sheet1")).thenReturn(mockSheet);
        when(mockSheet.createRow(0)).thenReturn(mockRow);
        when(mockRow.createCell(0)).thenReturn(mockCell);

        excelCreationService.stream(report, mockWorkbook);

        verify(mockRow).createCell(0);
        verify(mockCell).setCellValue("Field 1");
        verify(formatter, times(1)).applyFormatting(mockSheet, mockCell, mapping);
        verify(jdbcWorkbookDataStreamer).queryToSheet(mockSheet, query);
    }

    @Test
    void stream_ShouldSetupSheetHeaderCorrectly() {
        when(mockWorkbook.createSheet("Sheet1")).thenReturn(mockSheet);
        when(mockSheet.createRow(0)).thenReturn(mockRow);
        when(mockRow.createCell(0)).thenReturn(mockCell);
        when(mockRow.createCell(1)).thenReturn(mockCell);

        excelCreationService.stream(createTestReportWithMultipleFieldAttributes(), mockWorkbook);

        verify(mockRow).createCell(0);
        verify(mockRow).createCell(1);
        verify(mockCell).setCellValue("Field 1");
        verify(mockCell).setCellValue("Field 2");
    }

}