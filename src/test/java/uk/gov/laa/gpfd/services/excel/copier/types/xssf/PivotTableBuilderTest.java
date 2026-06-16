package uk.gov.laa.gpfd.services.excel.copier.types.xssf;

import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PivotTableBuilderTest {

    @Test
    void create_shouldReturnBuilderInstance() throws IOException {
        try (var workbook = new XSSFWorkbook()) {
            var sourceSheet = workbook.createSheet("Source");
            var targetSheet = workbook.createSheet("Target");
            sourceSheet.createRow(0).createCell(0);
            targetSheet.createRow(0).createCell(0);
            var sourcePivotTable = targetSheet.createPivotTable(
                    new AreaReference("'Source'!A1:A1", workbook.getSpreadsheetVersion()),
                    new CellReference(0, 0));

            var builder = PivotTableBuilder.create(workbook, workbook, targetSheet, sourcePivotTable);
            assertSame(PivotTableBuilder.class, builder.getClass());
        }
    }

    @Test
    void build_shouldCreateHeaderRowUseCustomFactoryAndApplyConfigurator() throws Exception {
        try (var workbook = new XSSFWorkbook()) {
            var sourceSheet = workbook.createSheet("Source");
            var targetSheet = workbook.createSheet("Target");
            sourceSheet.createRow(0).createCell(0).setCellValue("Value");
            var sourcePivotTable = sourceSheet.createPivotTable(
                    new AreaReference("'Source'!A1:A1", workbook.getSpreadsheetVersion()),
                    new CellReference(0, 0));

            var targetPivot = targetSheet.createPivotTable(
                    new AreaReference("'Source'!A1:A1", workbook.getSpreadsheetVersion()),
                    new CellReference(0, 0));

            var factory = Mockito.mock(PivotTableFactory.class);
            when(factory.createPivotTable(any(AreaReference.class), any(CellReference.class), any(XSSFSheet.class)))
                    .thenReturn(targetPivot);

            var configurator = new PivotTableConfigurator.CompositeConfigurator();
            configurator.addConfigurator(new PivotTableConfigurator.CustomStyleConfigurator("TestStyle"));

            var builder = PivotTableBuilder.create(workbook, workbook, targetSheet, sourcePivotTable)
                    .withFactory(factory)
                    .withConfigurator(configurator);

            builder.build();

            verify(factory).createPivotTable(any(AreaReference.class), any(CellReference.class), any(XSSFSheet.class));
            assertNotNull(targetSheet.getRow(0));
            assertTrue(targetPivot.getCTPivotTableDefinition().isSetPivotTableStyleInfo());
            assertEquals("TestStyle", targetPivot.getCTPivotTableDefinition().getPivotTableStyleInfo().getName());
        }
    }
}