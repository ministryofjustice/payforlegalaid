package uk.gov.laa.gpfd.services.excel.copier.copier.xssf;

import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFPivotTable;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.laa.gpfd.exception.ReportGenerationException.PivotTableCreationException;

@ExtendWith(MockitoExtension.class)
class PivotTableFactoryTest {

    @Mock
    private AreaReference sourceArea;

    @Mock
    private CellReference topLeft;

    @Mock
    private XSSFSheet sheet;

    @Mock
    private XSSFPivotTable pivotTable;

    @Mock
    private PivotTableFactory.PivotTableCreator creator;

    @Test
    void defaultFactory_shouldReturnDefaultPivotTableFactoryInstance() {
        var factory = PivotTableFactory.defaultFactory();
        assertInstanceOf(PivotTableFactory.DefaultPivotTableFactory.class, factory);
    }

    @Test
    void customFactory_shouldDelegateToProvidedCreator() {
        when(creator.create(sourceArea, topLeft, sheet)).thenReturn(pivotTable);
        var factory = PivotTableFactory.customFactory(creator);

        var result = factory.createPivotTable(sourceArea, topLeft, sheet);

        assertEquals(pivotTable, result);
        verify(creator).create(sourceArea, topLeft, sheet);
    }

    @Test
    void defaultFactoryCreatePivotTable_shouldThrowWhenAreaReferenceIsNull() {
        var factory = PivotTableFactory.defaultFactory();

        assertThrows(NullPointerException.class,
                () -> factory.createPivotTable(null, topLeft, sheet));
    }

    @Test
    void defaultFactoryCreatePivotTable_shouldThrowWhenCellReferenceIsNull() {
        var factory = PivotTableFactory.defaultFactory();

        assertThrows(NullPointerException.class,
                () -> factory.createPivotTable(sourceArea, null, sheet));
    }

    @Test
    void defaultFactoryCreatePivotTable_shouldThrowWhenSheetIsNull() {
        var factory = PivotTableFactory.defaultFactory();

        assertThrows(NullPointerException.class,
                () -> factory.createPivotTable(sourceArea, topLeft, null));
    }

    @Test
    void defaultFactoryCreatePivotTable_shouldCallSheetCreatePivotTable() {
        when(sheet.createPivotTable(sourceArea, topLeft)).thenReturn(pivotTable);
        var factory = PivotTableFactory.defaultFactory();

        var result = factory.createPivotTable(sourceArea, topLeft, sheet);

        assertEquals(pivotTable, result);
        verify(sheet).createPivotTable(sourceArea, topLeft);
    }

    @Test
    void defaultFactoryCreatePivotTable_shouldWrapExceptionsInPivotTableCreationException() {
        var cause = new RuntimeException("Test error");
        when(sheet.createPivotTable(sourceArea, topLeft)).thenThrow(cause);
        var factory = PivotTableFactory.defaultFactory();

        var exception = assertThrows(PivotTableCreationException.class,
                () -> factory.createPivotTable(sourceArea, topLeft, sheet));

        assertEquals("Failed to create pivot table", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void customFactory_shouldPropagateExceptionsFromCreator() {
        RuntimeException expected = new RuntimeException("Custom error");
        when(creator.create(sourceArea, topLeft, sheet)).thenThrow(expected);
        var factory = PivotTableFactory.customFactory(creator);

        var actual = assertThrows(RuntimeException.class,
                () -> factory.createPivotTable(sourceArea, topLeft, sheet));

        assertEquals(expected, actual);
    }

    @Test
    void defaultFactoryCreatePivotTable_shouldRequireNonNullParameters() {
        var factory = PivotTableFactory.defaultFactory();

        assertAll(
                () -> assertThrows(NullPointerException.class,
                        () -> factory.createPivotTable(null, topLeft, sheet)),
                () -> assertThrows(NullPointerException.class,
                        () -> factory.createPivotTable(sourceArea, null, sheet)),
                () -> assertThrows(NullPointerException.class,
                        () -> factory.createPivotTable(sourceArea, topLeft, null))
        );
    }
}