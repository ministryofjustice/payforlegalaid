package uk.gov.laa.gpfd.services.excel.copier.types.xssf;

import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertSame;

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

            var builder = PivotTableBuilder.create(workbook, workbook, sourceSheet, targetSheet, sourcePivotTable);
            assertSame(PivotTableBuilder.class, builder.getClass());
        }
    }

    @Test
    void withFactory_and_withConfigurator_shouldSetPrivateFields() throws Exception {
        try (var workbook = new XSSFWorkbook()) {
            var sourceSheet = workbook.createSheet("Source");
            var targetSheet = workbook.createSheet("Target");
            sourceSheet.createRow(0).createCell(0);
            targetSheet.createRow(0).createCell(0);
            var sourcePivotTable = targetSheet.createPivotTable(
                    new AreaReference("'Source'!A1:A1", workbook.getSpreadsheetVersion()),
                    new CellReference(0, 0));

            var builder = PivotTableBuilder.create(workbook, workbook, sourceSheet, targetSheet, sourcePivotTable);
            var factory = PivotTableFactory.customFactory((area, ref, sheet) -> sourcePivotTable);
            var configurator = new PivotTableConfigurator.CompositeConfigurator();

            var chainedBuilder = builder.withFactory(factory).withConfigurator(configurator);

            assertSame(builder, chainedBuilder);

            var factoryField = PivotTableBuilder.class.getDeclaredField("factory");
            factoryField.setAccessible(true);
            var configuratorField = PivotTableBuilder.class.getDeclaredField("configurator");
            configuratorField.setAccessible(true);

            assertSame(factory, factoryField.get(builder));
            assertSame(configurator, configuratorField.get(builder));
        }
    }
}