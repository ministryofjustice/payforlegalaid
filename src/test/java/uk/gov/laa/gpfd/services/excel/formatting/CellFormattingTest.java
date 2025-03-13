package uk.gov.laa.gpfd.services.excel.formatting;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.laa.gpfd.model.FieldAttributes;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyShort;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CellFormattingTest {

    private CellFormatting strategy;
    private Sheet sheet;
    private Cell cell;
    private Workbook workbook;
    private CellStyle cellStyle;
    private DataFormat dataFormat;
    private FieldAttributes fieldAttributes;

    @BeforeEach
    void setUp() {
        strategy = new CellFormatting() { };
        sheet = mock(Sheet.class);
        cell = mock(Cell.class);
        workbook = mock(Workbook.class);
        cellStyle = mock(CellStyle.class);
        dataFormat = mock(DataFormat.class);
        fieldAttributes = mock(FieldAttributes.class);

        when(sheet.getWorkbook()).thenReturn(workbook);
        when(workbook.createCellStyle()).thenReturn(cellStyle);
        when(workbook.createDataFormat()).thenReturn(dataFormat);
    }

    @Test
    void shouldApplyFormatWhenFormatIsPresent() {
        // Given
        var format = "dd/MM/yyyy";
        when(fieldAttributes.getFormat()).thenReturn(format);
        when(dataFormat.getFormat(format)).thenReturn((short) 1);

        // When
        strategy.apply(sheet, cell, fieldAttributes);

        // Then
        verify(workbook).createCellStyle();
        verify(dataFormat).getFormat(format);
        verify(cellStyle).setDataFormat((short) 1);
        verify(cell).setCellStyle(cellStyle);
    }

    @Test
    void shouldNotApplyFormatWhenFormatIsNull() {
        // Given
        when(fieldAttributes.getFormat()).thenReturn(null);

        // When
        strategy.apply(sheet, cell, fieldAttributes);

        // Then
        verify(workbook, never()).createCellStyle();
        verify(dataFormat, never()).getFormat(anyString());
        verify(cellStyle, never()).setDataFormat(anyShort());
        verify(cell, never()).setCellStyle(any(CellStyle.class));
    }

    @Test
    void shouldApplyFormatWhenFormatIsEmpty() {
        // Given
        when(fieldAttributes.getFormat()).thenReturn("");

        // When
        strategy.apply(sheet, cell, fieldAttributes);

        // Then
        verify(workbook).createCellStyle();
        verify(dataFormat).getFormat("");
        verify(cellStyle).setDataFormat(anyShort());
        verify(cell).setCellStyle(cellStyle);
    }

    @Test
    void shouldApplyFormatForMultipleCallsWithDifferentFormats() {
        // Given
        var format1 = "dd/MM/yyyy";
        var format2 = "0.00%";
        when(fieldAttributes.getFormat())
                .thenReturn(format1) // First call
                .thenReturn(format2); // Second call
        when(dataFormat.getFormat(format1)).thenReturn((short) 1);
        when(dataFormat.getFormat(format2)).thenReturn((short) 2);

        // When
        strategy.apply(sheet, cell, fieldAttributes); // First call
        strategy.apply(sheet, cell, fieldAttributes); // Second call

        // Then
        verify(workbook, times(2)).createCellStyle();
        verify(dataFormat).getFormat(format1);
        verify(dataFormat).getFormat(format2);
        verify(cellStyle, times(2)).setDataFormat(anyShort());
        verify(cell, times(2)).setCellStyle(cellStyle);
    }

    @Test
    void shouldThrowExceptionWhenCreateCellStyleFails() {
        // Given
        var format = "dd/MM/yyyy";
        when(fieldAttributes.getFormat()).thenReturn(format);
        when(workbook.createCellStyle()).thenThrow(new RuntimeException("Failed to create cell style"));

        // When & Then
        Assertions.assertThrows(RuntimeException.class, () -> {
            strategy.apply(sheet, cell, fieldAttributes);
        });
        verify(dataFormat, never()).getFormat(anyString());
        verify(cellStyle, never()).setDataFormat(anyShort());
        verify(cell, never()).setCellStyle(any(CellStyle.class));
    }

    @Test
    void shouldThrowExceptionWhenGetFormatFails() {
        // Given
        var format = "dd/MM/yyyy";
        when(fieldAttributes.getFormat()).thenReturn(format);
        when(dataFormat.getFormat(format)).thenThrow(new RuntimeException("Failed to get format"));

        // When & Then
        Assertions.assertThrows(RuntimeException.class, () -> {
            strategy.apply(sheet, cell, fieldAttributes);
        });
        verify(cellStyle, never()).setDataFormat(anyShort());
        verify(cell, never()).setCellStyle(any(CellStyle.class));
    }

    @Test
    void shouldApplyCustomFormatWhenFormatIsCustom() {
        // Given
        var format = "$#,##0.00";
        when(fieldAttributes.getFormat()).thenReturn(format);
        when(dataFormat.getFormat(format)).thenReturn((short) 3);

        // When
        strategy.apply(sheet, cell, fieldAttributes);

        // Then
        verify(workbook).createCellStyle();
        verify(dataFormat).getFormat(format);
        verify(cellStyle).setDataFormat((short) 3);
        verify(cell).setCellStyle(cellStyle);
    }

    @Test
    void shouldApplyDateFormatWhenFormatIsDate() {
        // Given
        var dateFormat = "dd/MM/yyyy"; // Date format to be applied
        when(fieldAttributes.getFormat()).thenReturn(dateFormat); // Return the date format
        when(dataFormat.getFormat(dateFormat)).thenReturn((short) 14); // Simulate format index for date

        // When
        strategy.apply(sheet, cell, fieldAttributes);

        // Then
        verify(workbook).createCellStyle();
        verify(dataFormat).getFormat(dateFormat);
        verify(cellStyle).setDataFormat((short) 14);
        verify(cell).setCellStyle(cellStyle);
    }
}