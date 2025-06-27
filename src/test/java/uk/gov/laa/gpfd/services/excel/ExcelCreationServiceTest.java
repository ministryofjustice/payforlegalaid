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
import uk.gov.laa.gpfd.services.excel.formatting.CellFormatter;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.laa.gpfd.data.ReportsTestDataFactory.createTestReportWithMultipleQueries;

@ExtendWith(MockitoExtension.class)
class ExcelCreationServiceTest {

    @Mock
    private JdbcWorkbookDataStreamer jdbcWorkbookDataStreamer;

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
    void stream_ShouldCreateSheetForEachMapping() {
       var testReport = ReportsTestDataFactory.createTestReportWithQuery();
        when(mockWorkbook.createSheet("Sheet1")).thenReturn(mockSheet);
        when(mockSheet.createRow(0)).thenReturn(mockRow);
        when(mockRow.createCell(0)).thenReturn(mockCell);

        excelCreationService.stream(testReport, mockWorkbook);

        verify(mockWorkbook).createSheet("Sheet1");
        verify(jdbcWorkbookDataStreamer).queryToSheet(mockSheet, testReport.getQueries().stream().findFirst().get());
    }

    @Test
    void stream_ShouldSetupSheetHeaderCorrectly() {
        when(mockWorkbook.createSheet("Sheet1")).thenReturn(mockSheet);
        when(mockSheet.createRow(0)).thenReturn(mockRow);
        when(mockRow.createCell(0)).thenReturn(mockCell);
        when(mockRow.createCell(1)).thenReturn(mockCell);

        excelCreationService.stream(createTestReportWithMultipleQueries(), mockWorkbook);

        verify(mockRow).createCell(0);
        verify(mockRow).createCell(1);
        verify(mockCell).setCellValue("Field 1");
        verify(mockCell).setCellValue("Field 2");
    }

}