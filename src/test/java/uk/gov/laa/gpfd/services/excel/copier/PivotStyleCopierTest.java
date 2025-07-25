package uk.gov.laa.gpfd.services.excel.copier;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDxf;
import uk.gov.laa.gpfd.exception.ReportGenerationException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PivotStyleCopierTest {

    private CTDxf pivotStyleWithBorder;
    private CTDxf pivotStyleWithFont;

    @Test
    void shouldCopyDxfStylesFromSourceToTarget() throws IOException {

        try (var sourceWorkbook = new XSSFWorkbook();
             var targetWorkbook = new SXSSFWorkbook()) {

            setupWorkbookWithStyles(sourceWorkbook);
            var pivotFormatCopier = new PivotStyleCopier(sourceWorkbook, targetWorkbook);
            pivotFormatCopier.copyPivotStyles();

            var targetStyles = targetWorkbook.getXSSFWorkbook().getStylesSource().getCTStylesheet();
            assertTrue(targetStyles.isSetDxfs());
            var targetDxfs = targetStyles.getDxfs();
            assertEquals(2, targetDxfs.sizeOfDxfArray());
            assertTrue(pivotStyleWithBorder.valueEquals(targetDxfs.getDxfArray(0)));
            assertTrue(pivotStyleWithFont.valueEquals(targetDxfs.getDxfArray(1)));

        }
    }

    private void setupWorkbookWithStyles(XSSFWorkbook workbook) {
        var sourceStyles = workbook.getStylesSource().getCTStylesheet();
        sourceStyles.addNewDxfs();
        pivotStyleWithBorder = CTDxf.Factory.newInstance();
        pivotStyleWithBorder.addNewBorder().addNewBottom().addNewColor().setAuto(true);
        pivotStyleWithFont = CTDxf.Factory.newInstance();
        pivotStyleWithFont.addNewFont().addNewName().setVal("Comic sans");

        sourceStyles.getDxfs().addNewDxf().set(pivotStyleWithBorder);
        sourceStyles.getDxfs().addNewDxf().set(pivotStyleWithFont);

    }

    @Test
    void shouldNotCopyAnythingIfSourceDoesNotSupportPivots() throws IOException {
        try (var sourceWorkbook = new SXSSFWorkbook();
             var targetWorkbook = new SXSSFWorkbook()) {

            var pivotFormatCopier = new PivotStyleCopier(sourceWorkbook, targetWorkbook);
            pivotFormatCopier.copyPivotStyles();

            // If target has no setDxfs it means we haven't copied anything - as this is the array that contains any dxf styles
            assertFalse(targetWorkbook.getXSSFWorkbook().getStylesSource().getCTStylesheet().isSetDxfs());
        }
    }

    @Test
    void shouldNotCopyAnythingIfNoDxfsSetOnSource() throws IOException {
        try (var sourceWorkbook = new XSSFWorkbook();
             var targetWorkbook = new SXSSFWorkbook()) {

            var pivotFormatCopier = new PivotStyleCopier(sourceWorkbook, targetWorkbook);
            pivotFormatCopier.copyPivotStyles();

            assertFalse(targetWorkbook.getXSSFWorkbook().getStylesSource().getCTStylesheet().isSetDxfs());
        }
    }

    @Test
    void shouldNotCopyAnythingIfNoDxfsInSourceArray() throws IOException {
        try (var sourceWorkbook = new XSSFWorkbook();
             var targetWorkbook = new SXSSFWorkbook()) {

            sourceWorkbook.getStylesSource().getCTStylesheet().addNewDxfs();

            var pivotFormatCopier = new PivotStyleCopier(sourceWorkbook, targetWorkbook);
            pivotFormatCopier.copyPivotStyles();

            assertFalse(targetWorkbook.getXSSFWorkbook().getStylesSource().getCTStylesheet().isSetDxfs());
        }
    }

    @Test
    void constructorShouldThrowErrorIfTargetWorkbookIsWrongType() throws IOException {
        try (var sourceWorkbook = new XSSFWorkbook();
             var targetWorkbook = new XSSFWorkbook()) {

            assertThrows(ReportGenerationException.InvalidWorkbookTypeException.class, () -> new PivotStyleCopier(sourceWorkbook, targetWorkbook));
        }
    }

    @Test
    void constructorShouldRequireNonNullArguments() throws IOException {
        try (var workbook = new XSSFWorkbook()) {
            assertThrows(NullPointerException.class, () -> new PivotStyleCopier(workbook, null));
            assertThrows(NullPointerException.class, () -> new PivotStyleCopier(null, workbook));
        }
    }

}
