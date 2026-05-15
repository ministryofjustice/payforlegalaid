package uk.gov.laa.gpfd.services.excel.workbook;

import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.streaming.SheetDataWriter;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.gpfd.model.ReportQuery;
import uk.gov.laa.gpfd.model.excel.ExcelSheet;

import java.io.IOException;
import java.io.StringWriter;

import static org.apache.poi.ss.usermodel.FormulaError.DIV0;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportSheetDataWriterTest {

    @Mock
    SharedStringsTable sharedStringSource;
    @Mock
    ReportQuery report;
    @Mock
    StyleManager styleManager;
    @Mock
    Cell cell;
    @Mock
    ExcelSheet sheet;

    ReportSheetDataWriter reportSheetDataWriter;

    StringWriter buffer;

    @BeforeEach
    void beforeEach() {
        buffer = new StringWriter();
    }

    private void createDataWriter() {
        createDataWriter(sharedStringSource);
    }

    @SneakyThrows
    private void createDataWriter(SharedStringsTable sharedStringSource) {
        reportSheetDataWriter = new ReportSheetDataWriter(sharedStringSource, report, styleManager);
        var field = SheetDataWriter.class.getDeclaredField("_out");
        field.setAccessible(true);
        field.set(reportSheetDataWriter, buffer);
    }

    private void setupCommonMocks() {
        when(report.getExcelSheet()).thenReturn(sheet);
        when(sheet.getName()).thenReturn("testSheet");
        when(styleManager.getColumnStyle(anyInt(), any())).thenReturn(-1);
    }

    @Test
    void writeCell_shouldDoNothingIfCellNull() throws IOException {
        createDataWriter();
        reportSheetDataWriter.writeCell(1, null);

        assertTrue(buffer.toString().isEmpty());
    }

    @Test
    void writeCell_shouldWriteBlankCell() throws IOException {
        createDataWriter();
        setupCommonMocks();

        when(cell.getCellType()).thenReturn(CellType.BLANK);

        reportSheetDataWriter.writeCell(0, cell);

        assertThat(buffer.toString())
                .contains("<c r=\"A1\"></c>");
    }

    @Test
    void writeCell_shouldWriteStyleIfOneSet() throws IOException {
        createDataWriter();
        setupCommonMocks();

        when(cell.getCellType()).thenReturn(CellType.BLANK);
        when(styleManager.getColumnStyle(anyInt(), any())).thenReturn(32);
        reportSheetDataWriter.writeCell(0, cell);

        assertThat(buffer.toString())
                .contains("<c r=\"A1\" s=\"32\"></c>");
        verify(styleManager.getColumnStyle(0, "testSheet"));
    }

    @Test
    void writeCell_shouldWriteNumericCell() throws IOException {
        createDataWriter();
        setupCommonMocks();

        when(cell.getCellType()).thenReturn(CellType.NUMERIC);
        when(cell.getNumericCellValue()).thenReturn(123.31);

        reportSheetDataWriter.writeCell(0, cell);

        assertThat(buffer.toString())
                .contains("<c r=\"A1\" t=\"n\"><v>123.31</v></c>");
    }

    @Test
    void writeCell_shouldWriteBooleanCell_withTrueAsOne() throws IOException {
        createDataWriter();
        setupCommonMocks();

        when(cell.getCellType()).thenReturn(CellType.BOOLEAN);
        when(cell.getBooleanCellValue()).thenReturn(true);

        reportSheetDataWriter.writeCell(0, cell);

        assertThat(buffer.toString())
                .contains("<c r=\"A1\" t=\"b\"><v>1</v></c>");
    }

    @Test
    void writeCell_shouldWriteBooleanCell_withFalseAsZero() throws IOException {
        createDataWriter();
        setupCommonMocks();

        when(cell.getCellType()).thenReturn(CellType.BOOLEAN);
        when(cell.getBooleanCellValue()).thenReturn(false);

        reportSheetDataWriter.writeCell(0, cell);

        assertThat(buffer.toString())
                .contains("<c r=\"A1\" t=\"b\"><v>0</v></c>");
    }

    @Test
    void writeCell_shouldWriteStringCell_fromSharedStringSource() throws IOException {
        createDataWriter();
        setupCommonMocks();

        when(cell.getCellType()).thenReturn(CellType.STRING);
        var stringValue = new XSSFRichTextString("string123");
        when(cell.getRichStringCellValue()).thenReturn(stringValue);
        when(sharedStringSource.addSharedStringItem(stringValue)).thenReturn(99);

        reportSheetDataWriter.writeCell(0, cell);
        assertThat(buffer.toString())
                .contains("<c r=\"A1\" t=\"s\"><v>99</v></c>");
    }

    @Test
    void writeCell_shouldWriteStringCell_whenNoSharedStringSource() throws IOException {
        createDataWriter(null);
        setupCommonMocks();

        when(cell.getCellType()).thenReturn(CellType.STRING);
        when(cell.getStringCellValue()).thenReturn("string123");

        reportSheetDataWriter.writeCell(0, cell);
        assertThat(buffer.toString())
                .contains("<c r=\"A1\" t=\"inlineStr\"><is><t>string123</t></is></c>");
    }

    @Test
    void writeCell_shouldWriteStringCell_whenNoSharedStringSource_shouldPreserveLeadingSpaces() throws IOException {
        createDataWriter(null);
        setupCommonMocks();

        when(cell.getCellType()).thenReturn(CellType.STRING);
        when(cell.getStringCellValue()).thenReturn("  string123");

        reportSheetDataWriter.writeCell(0, cell);
        assertThat(buffer.toString())
                .contains("<c r=\"A1\" t=\"inlineStr\"><is><t xml:space=\"preserve\">  string123</t></is></c>");
    }

    @Test
    void writeCell_shouldWriteStringCell_whenNoSharedStringSource_shouldPreserveTrailingSpaces() throws IOException {
        createDataWriter(null);
        setupCommonMocks();

        when(cell.getCellType()).thenReturn(CellType.STRING);
        when(cell.getStringCellValue()).thenReturn("string123     ");

        reportSheetDataWriter.writeCell(0, cell);
        assertThat(buffer.toString())
                .contains("<c r=\"A1\" t=\"inlineStr\"><is><t xml:space=\"preserve\">string123     </t></is></c>");
    }

    @Test
    void writeCell_shouldWriteStringCell_whenNoSharedStringSource_shouldHandleEmptyString() throws IOException {
        createDataWriter(null);
        setupCommonMocks();

        when(cell.getCellType()).thenReturn(CellType.STRING);
        when(cell.getStringCellValue()).thenReturn("");

        reportSheetDataWriter.writeCell(0, cell);
        assertThat(buffer.toString())
                .contains("<c r=\"A1\" t=\"inlineStr\"><is><t></t></is></c>");
    }

    @Test
    void writeCell_shouldWriteStringCell_whenNoSharedStringSource_shouldHandleNullString() throws IOException {
        createDataWriter(null);
        setupCommonMocks();

        when(cell.getCellType()).thenReturn(CellType.STRING);
        when(cell.getStringCellValue()).thenReturn(null);

        reportSheetDataWriter.writeCell(0, cell);
        assertThat(buffer.toString())
                .contains("<c r=\"A1\" t=\"inlineStr\"><is><t></t></is></c>");
    }

    @Test
    void writeCell_shouldWriteErrorCell() throws IOException {
        createDataWriter();
        setupCommonMocks();

        when(cell.getCellType()).thenReturn(CellType.ERROR);
        when(cell.getErrorCellValue()).thenReturn(DIV0.getCode());

        reportSheetDataWriter.writeCell(0, cell);
        assertThat(buffer.toString())
                .contains("<c r=\"A1\" t=\"e\"><v>#DIV/0!</v></c>");
    }

    @Test
    void writeCell_shouldHandleNumericFormulae() throws IOException {
        createDataWriter();
        setupCommonMocks();

        when(cell.getCellType()).thenReturn(CellType.FORMULA);
        when(cell.getCachedFormulaResultType()).thenReturn(CellType.NUMERIC);
        when(cell.getCellFormula()).thenReturn("=A3+B4");
        when(cell.getNumericCellValue()).thenReturn(1234.32);

        reportSheetDataWriter.writeCell(0, cell);
        assertThat(buffer.toString())
                .contains("<c r=\"A1\" t=\"n\"><f>=A3+B4</f><v>1234.32</v></c>");
    }

    @Test
    void writeCell_shouldHandleStringFormulae() throws IOException {
        createDataWriter();
        setupCommonMocks();

        when(cell.getCellType()).thenReturn(CellType.FORMULA);
        when(cell.getCachedFormulaResultType()).thenReturn(CellType.STRING);
        when(cell.getCellFormula()).thenReturn("=A3+B4");
        when(cell.getStringCellValue()).thenReturn("formula result");

        reportSheetDataWriter.writeCell(0, cell);
        assertThat(buffer.toString())
                .contains("<c r=\"A1\" t=\"str\"><f>=A3+B4</f><v>formula result</v></c>");
    }

    @Test
    void writeCell_shouldHandleBooleanFormulae() throws IOException {
        createDataWriter();
        setupCommonMocks();

        when(cell.getCellType()).thenReturn(CellType.FORMULA);
        when(cell.getCachedFormulaResultType()).thenReturn(CellType.BOOLEAN);
        when(cell.getCellFormula()).thenReturn("=A3+B4");
        when(cell.getBooleanCellValue()).thenReturn(true);

        reportSheetDataWriter.writeCell(0, cell);
        assertThat(buffer.toString())
                .contains("<c r=\"A1\" t=\"b\"><f>=A3+B4</f><v>1</v></c>");
    }

    @Test
    void writeCell_shouldHandleErrorFormulae() throws IOException {
        createDataWriter();
        setupCommonMocks();

        when(cell.getCellType()).thenReturn(CellType.FORMULA);
        when(cell.getCachedFormulaResultType()).thenReturn(CellType.ERROR);
        when(cell.getCellFormula()).thenReturn("=A3+B4");
        when(cell.getErrorCellValue()).thenReturn(DIV0.getCode());

        reportSheetDataWriter.writeCell(0, cell);
        assertThat(buffer.toString())
                .contains("<c r=\"A1\" t=\"e\"><f>=A3+B4</f><v>#DIV/0!</v></c>");
    }

}