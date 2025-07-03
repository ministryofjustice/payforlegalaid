package uk.gov.laa.gpfd.services.excel.copier.types.xssf;

import org.apache.poi.xssf.usermodel.XSSFPivotTable;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.gpfd.exception.ReportGenerationException.PivotTableCopyException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.laa.gpfd.services.excel.copier.types.xssf.PivotTableBuilder.create;
import static uk.gov.laa.gpfd.services.excel.copier.types.xssf.PivotTableDirector.standard;

@ExtendWith(MockitoExtension.class)
class XSSFSheetCopierTest {

    @Mock
    private XSSFWorkbook sourceWorkbook;

    @Mock
    private XSSFWorkbook targetWorkbook;

    @Mock
    private XSSFSheet sourceSheet;

    @Mock
    private XSSFSheet targetSheet;

    @Mock
    private XSSFPivotTable pivotTable;

    @Mock
    private PivotTableBuilder pivotTableBuilder;

    @Mock
    private PivotTableDirector pivotTableDirector;

    @Test
    void copyAdditionalFeatures_shouldDoNothingWhenNoPivotTablesExist() {
        when(sourceSheet.getPivotTables()).thenReturn(Collections.emptyList());

        var copier = new XSSFSheetCopier(sourceWorkbook, targetWorkbook, sourceSheet, targetSheet);
        copier.copyAdditionalFeatures();

        verify(sourceSheet, atMostOnce()).getPivotTables();
        verify(sourceSheet, never()).getRow(anyInt());
    }

    @Test
    void copyAdditionalFeatures_shouldProcessAllPivotTables() {
        var pivotTables = List.of(pivotTable, pivotTable);
        when(sourceSheet.getPivotTables()).thenReturn(pivotTables);

        try (var mockedPivotTableBuilder = mockStatic(PivotTableBuilder.class); var mockedPivotTableDirector = mockStatic(PivotTableDirector.class)) {
            mockedPivotTableBuilder.when(() -> create(sourceWorkbook, targetWorkbook, sourceSheet, targetSheet, pivotTable)).thenReturn(pivotTableBuilder);
            mockedPivotTableDirector.when(() -> standard(pivotTableBuilder)).thenReturn(pivotTableDirector);

            var copier = new XSSFSheetCopier(sourceWorkbook, targetWorkbook, sourceSheet, targetSheet);
            copier.copyAdditionalFeatures();

            mockedPivotTableBuilder.verify(() -> create(sourceWorkbook, targetWorkbook, sourceSheet, targetSheet, pivotTable), times(2));
            mockedPivotTableDirector.verify(() -> standard(pivotTableBuilder), times(2));
            verify(pivotTableDirector, times(2)).construct();
        }
    }

    @Test
    void copyAdditionalFeatures_shouldThrowPivotTableCopyExceptionOnFailure() {
        when(sourceSheet.getPivotTables()).thenReturn(List.of(pivotTable));
        when(sourceSheet.getSheetName()).thenReturn("TestSheet");

        try (var mockedPivotTableBuilder = mockStatic(PivotTableBuilder.class)) {
            mockedPivotTableBuilder.when(() -> create(sourceWorkbook, targetWorkbook, sourceSheet, targetSheet, pivotTable)).thenThrow(new RuntimeException("Test error"));

            var copier = new XSSFSheetCopier(sourceWorkbook, targetWorkbook, sourceSheet, targetSheet);

            var exception = assertThrows(PivotTableCopyException.class, copier::copyAdditionalFeatures);
            assertEquals("Failed to copy pivot table in sheet 'TestSheet': Failed to copy pivot table", exception.getMessage());
            assertEquals("TestSheet", exception.getSheetName());
            assertNotNull(exception.getCause());
        }
    }

    @Test
    void copyAdditionalFeatures_shouldHandleNullPivotTablesList() {
        when(sourceSheet.getPivotTables()).thenReturn(null);

        var copier = new XSSFSheetCopier(sourceWorkbook, targetWorkbook, sourceSheet, targetSheet);

        assertDoesNotThrow(copier::copyAdditionalFeatures);
    }

    @Test
    void copyAdditionalFeatures_shouldProcessSinglePivotTable() {
        when(sourceSheet.getPivotTables()).thenReturn(List.of(pivotTable));

        try (var mockedPivotTableBuilder = mockStatic(PivotTableBuilder.class); var mockedPivotTableDirector = mockStatic(PivotTableDirector.class)) {
            mockedPivotTableBuilder.when(() -> create(sourceWorkbook, targetWorkbook, sourceSheet, targetSheet, pivotTable)).thenReturn(pivotTableBuilder);
            mockedPivotTableDirector.when(() -> standard(pivotTableBuilder)).thenReturn(pivotTableDirector);

            var copier = new XSSFSheetCopier(sourceWorkbook, targetWorkbook, sourceSheet, targetSheet);
            copier.copyAdditionalFeatures();

            mockedPivotTableBuilder.verify(() -> create(sourceWorkbook, targetWorkbook, sourceSheet, targetSheet, pivotTable));
            mockedPivotTableDirector.verify(() -> standard(pivotTableBuilder));
            verify(pivotTableDirector).construct();
        }
    }
}