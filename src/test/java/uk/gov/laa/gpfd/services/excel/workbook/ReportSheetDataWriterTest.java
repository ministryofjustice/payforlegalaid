package uk.gov.laa.gpfd.services.excel.workbook;

import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.streaming.SheetDataWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.gpfd.model.ReportQuery;
import uk.gov.laa.gpfd.model.excel.ExcelSheet;

import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportSheetDataWriterTest {

    @Mock
    SharedStringsTable sharedStringSource;
    @Mock
    ReportQuery report;
    @Mock
    StyleManager styleManager;

    ReportSheetDataWriter reportSheetDataWriter;

    StringWriter buffer;

    @SneakyThrows
    @BeforeEach
    void beforeEach() {
        reportSheetDataWriter = new ReportSheetDataWriter(sharedStringSource, report, styleManager);
        buffer = new StringWriter();
        var field = SheetDataWriter.class.getDeclaredField("_out");
        field.setAccessible(true);
        field.set(reportSheetDataWriter, buffer);
    }

    @Test
    @SneakyThrows
    void writeCell_shouldDoNothingIfCellNull() {
        reportSheetDataWriter.writeCell(1, null);

        assertTrue(buffer.toString().isEmpty());
    }

    @Test
    @SneakyThrows
    void writeCell_shouldWriteBlankCell() {
        var cell = mock(Cell.class);
        var sheet = mock(ExcelSheet.class);
        when(report.getExcelSheet()).thenReturn(sheet);
        when(sheet.getName()).thenReturn("testSheet");
        when(styleManager.getColumnStyle(anyInt(), any())).thenReturn(-1);
        when(cell.getCellType()).thenReturn(CellType.BLANK);

        reportSheetDataWriter.writeCell(0, cell);


        assertTrue(buffer.toString().contains("<c r=\"A1\"></c>"));
    }

    @Test
    @SneakyThrows
    void writeCell_shouldWriteNumericCell() {
        var cell = mock(Cell.class);
        var sheet = mock(ExcelSheet.class);
        when(report.getExcelSheet()).thenReturn(sheet);
        when(sheet.getName()).thenReturn("testSheet");
        when(styleManager.getColumnStyle(anyInt(), any())).thenReturn(-1);
        when(cell.getCellType()).thenReturn(CellType.NUMERIC);
        when(cell.getNumericCellValue()).thenReturn(123.31);

        reportSheetDataWriter.writeCell(0, cell);

        assertTrue(buffer.toString().contains("<c r=\"A1\" t=\"n\"><v>123.31</v></c>"));
    }

    @Test
    @SneakyThrows
    void writeCell_shouldWriteBooleanCell_withTrueAsOne() {
        var cell = mock(Cell.class);
        var sheet = mock(ExcelSheet.class);
        when(report.getExcelSheet()).thenReturn(sheet);
        when(sheet.getName()).thenReturn("testSheet");
        when(styleManager.getColumnStyle(anyInt(), any())).thenReturn(-1);
        when(cell.getCellType()).thenReturn(CellType.BOOLEAN);
        when(cell.getBooleanCellValue()).thenReturn(true);

        reportSheetDataWriter.writeCell(0, cell);

        assertTrue(buffer.toString().contains("<c r=\"A1\" t=\"b\"><v>1</v></c>"));
    }

    @Test
    @SneakyThrows
    void writeCell_shouldWriteBooleanCell_withFalseAsZero() {
        var cell = mock(Cell.class);
        var sheet = mock(ExcelSheet.class);
        when(report.getExcelSheet()).thenReturn(sheet);
        when(sheet.getName()).thenReturn("testSheet");
        when(styleManager.getColumnStyle(anyInt(), any())).thenReturn(-1);
        when(cell.getCellType()).thenReturn(CellType.BOOLEAN);
        when(cell.getBooleanCellValue()).thenReturn(false);

        reportSheetDataWriter.writeCell(0, cell);

        assertTrue(buffer.toString().contains("<c r=\"A1\" t=\"b\"><v>0</v></c>"));
    }

}