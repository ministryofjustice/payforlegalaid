package uk.gov.laa.gpfd.services.excel.editor;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.laa.gpfd.model.FieldAttributes;
import uk.gov.laa.gpfd.services.excel.formatting.CellFormatter;

import java.util.*;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SheetDataWriterTest {

    private SheetDataWriter sheetDataWriter;
    private Sheet sheet;
    private Row row;
    private Cell cell;
    private CellValueSetter cellValueSetter;
    private CellFormatter cellFormatter;
    private List<Map<String, Object>> data;
    private Collection<FieldAttributes> fieldAttributes;

    @BeforeEach
    void setUp() {
        sheet = mock(Sheet.class);
        row = mock(Row.class);
        cell = mock(Cell.class);
        cellValueSetter = mock(CellValueSetter.class);
        cellFormatter = mock(CellFormatter.class);
        sheetDataWriter = new SheetDataWriter() {
            @Override
            public void writeDataToSheet(Sheet sheet, List<Map<String, Object>> data, Collection<FieldAttributes> fieldAttributes) {
                writeDataToSheet(cellValueSetter, cellFormatter, sheet, data, fieldAttributes);
            }
        };
        when(sheet.createRow(anyInt())).thenReturn(row);
        when(row.createCell(anyInt())).thenReturn(cell);
        data = List.of(
                Map.of("source1", "value1", "source2", 123),
                Map.of("source1", "value2", "source2", 456)
        );

        fieldAttributes = List.of(
                FieldAttributes.builder().sourceName("source1").build(),
                FieldAttributes.builder().sourceName("source2").build()
        );
    }

    @Test
    void shouldWriteDataToSheet() {
        // When
        sheetDataWriter.writeDataToSheet(cellValueSetter, cellFormatter, sheet, data, fieldAttributes);

        // Then
        verify(sheet, times(2)).createRow(anyInt());

        // Verify cells are created and values are set
        verify(row, times(4)).createCell(anyInt());
        verify(cellValueSetter, times(4)).setCellValue(any(), any());
        verify(cellFormatter, times(4)).applyFormatting(any(), any(), any());
    }

    @Test
    void shouldHandleEmptyData() {
        // Given
        data = Collections.emptyList();

        // When
        sheetDataWriter.writeDataToSheet(cellValueSetter, cellFormatter, sheet, data, fieldAttributes);

        // Then
        verify(sheet, never()).createRow(anyInt());
        verify(row, never()).createCell(anyInt());
        verify(cellValueSetter, never()).setCellValue(any(), any());
        verify(cellFormatter, never()).applyFormatting(any(), any(), any());
    }

    @Test
    void shouldHandleEmptyFieldAttributes() {
        // Given
        fieldAttributes = Collections.emptyList();

        // When
        sheetDataWriter.writeDataToSheet(cellValueSetter, cellFormatter, sheet, data, fieldAttributes);

        // Then
        verify(sheet, times(2)).createRow(anyInt());
        verify(row, never()).createCell(anyInt());
        verify(cellValueSetter, never()).setCellValue(any(), any());
        verify(cellFormatter, never()).applyFormatting(any(), any(), any());
    }
}