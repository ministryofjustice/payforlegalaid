package uk.gov.laa.gpfd.services.excel.copier.copier.xssf;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.poi.xssf.usermodel.XSSFPivotTable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PivotTableConfiguratorTest {

    @Mock
    private XSSFPivotTable sourcePivotTable;
    @Mock
    private XSSFPivotTable targetPivotTable;
    @Mock
    private CTPivotTableDefinition sourceDef;
    @Mock
    private CTPivotTableDefinition targetDef;
    @Mock
    private CTPivotFields pivotFields;
    @Mock
    private CTRowFields rowFields;
    @Mock
    private CTColFields colFields;
    @Mock
    private CTDataFields dataFields;
    @Mock
    private CTPivotTableStyle styleInfo;
    @Mock
    private CTPageFields pageFields;

    @Test
    void defaultConfigurator_shouldCreateCompositeWithLayoutAndDefinition() {
        PivotTableConfigurator configurator = PivotTableConfigurator.defaultConfigurator();
        assertInstanceOf(PivotTableConfigurator.CompositeConfigurator.class, configurator);
    }

    @Test
    void minimalConfigurator_shouldCreateDefinitionConfigurator() {
        PivotTableConfigurator configurator = PivotTableConfigurator.minimalConfigurator();
        assertInstanceOf(PivotTableConfigurator.DefinitionConfigurator.class, configurator);
    }

    @Test
    void styledConfigurator_shouldCreateCompositeWithStyleConfigurator() {
        PivotTableConfigurator configurator = PivotTableConfigurator.styledConfigurator("TestStyle");
        assertInstanceOf(PivotTableConfigurator.CompositeConfigurator.class, configurator);
    }

    @Test
    void layoutConfigurator_shouldCopyAllLayoutElements() {
        when(sourcePivotTable.getCTPivotTableDefinition()).thenReturn(sourceDef);
        when(targetPivotTable.getCTPivotTableDefinition()).thenReturn(targetDef);

        // Set up source definition with all elements
        when(sourceDef.isSetPivotFields()).thenReturn(true);
        when(sourceDef.getPivotFields()).thenReturn(pivotFields);
        when(sourceDef.isSetRowFields()).thenReturn(true);
        when(sourceDef.getRowFields()).thenReturn(rowFields);
        when(sourceDef.isSetColFields()).thenReturn(true);
        when(sourceDef.getColFields()).thenReturn(colFields);
        when(sourceDef.isSetDataFields()).thenReturn(true);
        when(sourceDef.getDataFields()).thenReturn(dataFields);
        when(sourceDef.isSetPivotTableStyleInfo()).thenReturn(true);
        when(sourceDef.getPivotTableStyleInfo()).thenReturn(styleInfo);
        when(sourceDef.isSetPageFields()).thenReturn(true);
        when(sourceDef.getPageFields()).thenReturn(pageFields);

        PivotTableConfigurator.LayoutConfigurator configurator = new PivotTableConfigurator.LayoutConfigurator();
        configurator.configure(sourcePivotTable, targetPivotTable);

        verify(targetDef).setColFields(colFields);
        verify(targetDef).setDataFields(dataFields);
        verify(targetDef).setLocation(any());
        verify(targetDef).setPageFields(pageFields);
        verify(targetDef).setDataOnRows(anyBoolean());
        verify(targetDef).setApplyNumberFormats(anyBoolean());
    }

    @Test
    void layoutConfigurator_shouldHandleMissingElements() {
        when(sourcePivotTable.getCTPivotTableDefinition()).thenReturn(sourceDef);
        when(targetPivotTable.getCTPivotTableDefinition()).thenReturn(targetDef);

        // Source definition with no elements set
        when(sourceDef.isSetPivotFields()).thenReturn(false);
        when(sourceDef.isSetRowFields()).thenReturn(false);
        when(sourceDef.isSetColFields()).thenReturn(false);
        when(sourceDef.isSetDataFields()).thenReturn(false);
        when(sourceDef.isSetPivotTableStyleInfo()).thenReturn(false);
        when(sourceDef.isSetPageFields()).thenReturn(false);

        PivotTableConfigurator.LayoutConfigurator configurator = new PivotTableConfigurator.LayoutConfigurator();
        configurator.configure(sourcePivotTable, targetPivotTable);

        verify(targetDef, never()).setPivotFields(any());
        verify(targetDef, never()).setRowFields(any());
        verify(targetDef, never()).setColFields(any());
        verify(targetDef, never()).setDataFields(any());
        verify(targetDef, never()).setPivotTableStyleInfo(any());
        verify(targetDef, never()).setPageFields(any());
    }

    @Test
    void definitionConfigurator_shouldCopyEntireDefinition() {
        when(sourcePivotTable.getCTPivotTableDefinition()).thenReturn(sourceDef);
        when(targetPivotTable.getCTPivotTableDefinition()).thenReturn(targetDef);

        PivotTableConfigurator.DefinitionConfigurator configurator = new PivotTableConfigurator.DefinitionConfigurator();
        configurator.configure(sourcePivotTable, targetPivotTable);

        verify(targetDef).set(sourceDef);
    }

    @Test
    void customStyleConfigurator_shouldApplyStyleToExistingStyleInfo() {
        when(targetPivotTable.getCTPivotTableDefinition()).thenReturn(targetDef);
        when(targetDef.isSetPivotTableStyleInfo()).thenReturn(true);
        when(targetDef.getPivotTableStyleInfo()).thenReturn(styleInfo);

        PivotTableConfigurator.CustomStyleConfigurator configurator = new PivotTableConfigurator.CustomStyleConfigurator("TestStyle");
        configurator.configure(sourcePivotTable, targetPivotTable);

        verify(styleInfo).setName("TestStyle");
        verify(styleInfo).setShowRowHeaders(true);
        verify(styleInfo).setShowColHeaders(true);
        verify(targetDef, never()).addNewPivotTableStyleInfo();
    }

    @Test
    void customStyleConfigurator_shouldCreateNewStyleInfoIfNotExists() {
        when(targetPivotTable.getCTPivotTableDefinition()).thenReturn(targetDef);
        when(targetDef.isSetPivotTableStyleInfo()).thenReturn(false);
        CTPivotTableStyle newStyle = mock(CTPivotTableStyle.class);
        when(targetDef.addNewPivotTableStyleInfo()).thenReturn(newStyle);

        PivotTableConfigurator.CustomStyleConfigurator configurator = new PivotTableConfigurator.CustomStyleConfigurator("TestStyle");
        configurator.configure(sourcePivotTable, targetPivotTable);

        verify(targetDef).addNewPivotTableStyleInfo();
        verify(newStyle).setName("TestStyle");
        verify(newStyle).setShowRowHeaders(true);
        verify(newStyle).setShowColHeaders(true);
    }
}