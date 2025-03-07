package uk.gov.laa.gpfd.services.excel.formatting;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.laa.gpfd.model.FieldAttributes;

import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ColumnFormattingStrategyTest {

    private ColumnFormatting strategy;
    private Sheet sheet;
    private Cell cell;
    private FieldAttributes fieldAttributes;

    @BeforeEach
    void setUp() {
        strategy = new ColumnFormatting() { };
        sheet = mock(Sheet.class);
        cell = mock(Cell.class);
        fieldAttributes = mock(FieldAttributes.class);
        when(cell.getColumnIndex()).thenReturn(0);
    }

    @Test
    void shouldSetColumnWidthWhenWidthIsPositive() {
        // Given
        var columnWidth = 10.5;
        when(fieldAttributes.getColumnWidth()).thenReturn(columnWidth);

        // When
        strategy.apply(sheet, cell, fieldAttributes);

        // Then
        verify(sheet).setColumnWidth(0, (int) (columnWidth * 256)); // Verify width is set correctly
    }

    @Test
    void shouldNotSetColumnWidthWhenWidthIsZero() {
        // Given
        double columnWidth = 0;
        when(fieldAttributes.getColumnWidth()).thenReturn(columnWidth);

        // When
        strategy.apply(sheet, cell, fieldAttributes);

        // Then
        verify(sheet, never()).setColumnWidth(anyInt(), anyInt()); // Verify no width is set
    }

    @Test
    void shouldNotSetColumnWidthWhenWidthIsNegative() {
        // Given
        var columnWidth = -5.0;
        when(fieldAttributes.getColumnWidth()).thenReturn(columnWidth);

        // When
        strategy.apply(sheet, cell, fieldAttributes);

        // Then
        verify(sheet, never()).setColumnWidth(anyInt(), anyInt()); // Verify no width is set
    }

    @Test
    void shouldThrowExceptionWhenSheetSetColumnWidthFails() {
        // Given
        var columnWidth = 10.5;
        when(fieldAttributes.getColumnWidth()).thenReturn(columnWidth);
        doThrow(new RuntimeException("Failed to set column width")).when(sheet).setColumnWidth(anyInt(), anyInt());

        // When & Then
        Assertions.assertThrows(RuntimeException.class, () -> {
            strategy.apply(sheet, cell, fieldAttributes);
        });

        verify(sheet).setColumnWidth(0, (int) (columnWidth * 256));
    }
}
