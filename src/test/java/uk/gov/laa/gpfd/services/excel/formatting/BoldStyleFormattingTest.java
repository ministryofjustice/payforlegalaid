package uk.gov.laa.gpfd.services.excel.formatting;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.gpfd.model.excel.ExcelMappingProjection;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BoldStyleFormattingTest implements BoldStyleFormatting {

    @Mock
    private Sheet mockSheet;

    @Mock
    private Cell mockCell;

    @Mock
    private Workbook mockWorkbook;

    @Mock
    private CellStyle mockCellStyle;

    @Mock
    private Font mockFont;

    @Mock
    private ExcelMappingProjection mockFieldAttribute;

    @Test
    void apply_shouldSetBoldCellStyle() {
        when(mockSheet.getWorkbook()).thenReturn(mockWorkbook);
        when(mockWorkbook.createCellStyle()).thenReturn(mockCellStyle);
        when(mockWorkbook.createFont()).thenReturn(mockFont);

        apply(mockSheet, mockCell, mockFieldAttribute);

        verify(mockCell).setCellStyle(mockCellStyle);
        verify(mockFont).setBold(true);
        verify(mockCellStyle).setFont(mockFont);
    }

    @Test
    void apply_withNullSheet_shouldThrowException() {
        assertThrows(NullPointerException.class, () -> apply(null, mockCell, mockFieldAttribute));
    }

    @Test
    void apply_withNullCell_shouldThrowException() {
        assertThrows(NullPointerException.class, () -> apply(mockSheet, null, mockFieldAttribute));
    }

    @Test
    void apply_withNullFieldAttribute_shouldThrowException() {
        assertThrows(NullPointerException.class, () -> apply(mockSheet, mockCell, null));
    }

}