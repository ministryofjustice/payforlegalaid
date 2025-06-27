package uk.gov.laa.gpfd.services.excel.copier.copier.basic;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.laa.gpfd.services.excel.copier.copier.basic.CellValueCopier.copyValue;

@ExtendWith(MockitoExtension.class)
class CellValueCopierTest {

    @Mock
    private Cell sourceCell;

    @Mock
    private Cell targetCell;

    @Test
    void copyValue_shouldHandleStringType() {
        when(sourceCell.getCellType()).thenReturn(CellType.STRING);
        when(sourceCell.getStringCellValue()).thenReturn("Test String");

        copyValue(sourceCell, targetCell);

        verify(targetCell).setCellValue("Test String");
    }

    @Test
    void copyValue_shouldHandleNumericType() {
        when(sourceCell.getCellType()).thenReturn(CellType.NUMERIC);
        when(sourceCell.getNumericCellValue()).thenReturn(123.45);

        copyValue(sourceCell, targetCell);

        verify(targetCell).setCellValue(123.45);
    }

    @Test
    void copyValue_shouldHandleBooleanType() {
        when(sourceCell.getCellType()).thenReturn(CellType.BOOLEAN);
        when(sourceCell.getBooleanCellValue()).thenReturn(true);

        copyValue(sourceCell, targetCell);

        verify(targetCell).setCellValue(true);
    }

    @Test
    void copyValue_shouldHandleFormulaType() {
        when(sourceCell.getCellType()).thenReturn(CellType.FORMULA);
        when(sourceCell.getCellFormula()).thenReturn("SUM(A1:A10)");

        copyValue(sourceCell, targetCell);

        verify(targetCell).setCellFormula("SUM(A1:A10)");
    }

    @Test
    void copyValue_shouldHandleBlankType() {
        when(sourceCell.getCellType()).thenReturn(CellType.BLANK);

        copyValue(sourceCell, targetCell);

        verify(targetCell).setBlank();
    }

    @Test
    void copyValue_shouldHandleErrorType() {
        when(sourceCell.getCellType()).thenReturn(CellType.ERROR);
        when(sourceCell.getErrorCellValue()).thenReturn((byte) 42);

        copyValue(sourceCell, targetCell);

        verify(targetCell).setCellErrorValue((byte) 42);
    }

    @Test
    void copyValue_shouldUseDefaultForUnknownType() {
        when(sourceCell.getCellType()).thenReturn(CellType._NONE);

        copyValue(sourceCell, targetCell);

        verifyNoInteractions(targetCell);
    }

    @Test
    void copyValue_shouldHandleNullSourceCell() {
        assertThrows(NullPointerException.class, () -> copyValue(null, targetCell));
    }

    @Test
    void copyValue_shouldHandleNullTargetCell() {
        when(sourceCell.getCellType()).thenReturn(CellType.STRING);

        assertThrows(NullPointerException.class, () -> copyValue(sourceCell, null));
    }

}