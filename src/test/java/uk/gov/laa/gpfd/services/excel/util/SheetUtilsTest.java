package uk.gov.laa.gpfd.services.excel.util;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SheetUtilsTest {

    @Test
    void shouldFindSheetByNameWhenSheetExists() {
        // Given
        var workbook = mock(Workbook.class);
        var sheet1 = mock(Sheet.class);
        var sheet2 = mock(Sheet.class);

        when(workbook.getNumberOfSheets()).thenReturn(2);
        when(workbook.getSheetAt(0)).thenReturn(sheet1);
        when(workbook.getSheetAt(1)).thenReturn(sheet2);
        when(sheet1.getSheetName()).thenReturn("Sheet1");
        when(sheet2.getSheetName()).thenReturn("Sheet2");

        // When
        var result = SheetUtils.findSheetByName(workbook, "Sheet2");

        // Then
        assertTrue(result.isPresent());
        assertEquals(sheet2, result.get());
        verify(workbook).getNumberOfSheets();
        verify(workbook).getSheetAt(1);
        verify(sheet2).getSheetName();
    }

    @Test
    void shouldNotFindSheetByNameWhenSheetDoesNotExist() {
        // Given
        var workbook = mock(Workbook.class);
        var sheet1 = mock(Sheet.class);
        var sheet2 = mock(Sheet.class);

        when(workbook.getNumberOfSheets()).thenReturn(2);
        when(workbook.getSheetAt(0)).thenReturn(sheet1);
        when(workbook.getSheetAt(1)).thenReturn(sheet2);
        when(sheet1.getSheetName()).thenReturn("Sheet1");
        when(sheet2.getSheetName()).thenReturn("Sheet2");

        // When
        var result = SheetUtils.findSheetByName(workbook, "Sheet3");

        // Then
        assertFalse(result.isPresent());
        verify(workbook).getNumberOfSheets();
        verify(workbook).getSheetAt(0);
        verify(workbook).getSheetAt(1);
        verify(sheet1).getSheetName();
        verify(sheet2).getSheetName();
    }

    @Test
    void shouldNotFindSheetByNameWhenWorkbookIsEmpty() {
        // Given
        var workbook = mock(Workbook.class);
        when(workbook.getNumberOfSheets()).thenReturn(0);

        // When
        var result = SheetUtils.findSheetByName(workbook, "Sheet1");

        // Then
        assertFalse(result.isPresent());
        verify(workbook).getNumberOfSheets();
        verify(workbook, never()).getSheetAt(anyInt());
    }

    @Test
    void shouldHandleNullSheetName() {
        // Given
        var workbook = mock(Workbook.class);
        var sheet1 = mock(Sheet.class);
        var sheet2 = mock(Sheet.class);

        when(workbook.getNumberOfSheets()).thenReturn(2);
        when(workbook.getSheetAt(0)).thenReturn(sheet1);
        when(workbook.getSheetAt(1)).thenReturn(sheet2);
        when(sheet1.getSheetName()).thenReturn("Sheet1");
        when(sheet2.getSheetName()).thenReturn(null); // Null sheet name

        // When
        var result = SheetUtils.findSheetByName(workbook, "Sheet2");

        // Then
        assertFalse(result.isPresent());
        verify(workbook).getNumberOfSheets();
        verify(workbook).getSheetAt(1);
        verify(sheet2).getSheetName();
    }
}