package uk.gov.laa.gpfd.services.excel.copier;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDxf;
import uk.gov.laa.gpfd.exception.ReportGenerationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PivotStyleCopierTest {

    private static XSSFWorkbook sourceWorkbookWithPivots;
    private static CTDxf pivotStyleWithBorder;
    private static CTDxf pivotStyleWithFont;

    @BeforeAll
    public static void setupSourceWorkbookToHavePivotStyles() {
        sourceWorkbookWithPivots = new XSSFWorkbook();
        var sourceStyles = sourceWorkbookWithPivots.getStylesSource().getCTStylesheet();
        sourceStyles.addNewDxfs();
        pivotStyleWithBorder = CTDxf.Factory.newInstance();
        pivotStyleWithBorder.addNewBorder().addNewBottom().addNewColor().setAuto(true);
        pivotStyleWithFont = CTDxf.Factory.newInstance();
        pivotStyleWithFont.addNewFont().addNewName().setVal("Comic sans");

        sourceStyles.getDxfs().addNewDxf().set(pivotStyleWithBorder);
        sourceStyles.getDxfs().addNewDxf().set(pivotStyleWithFont);
    }

    @Test
    void shouldCopyDxfStylesFromSourceToTarget() {

        var targetWorkbook = new SXSSFWorkbook();

        var pivotFormatCopier = new PivotStyleCopier(sourceWorkbookWithPivots, targetWorkbook);
        pivotFormatCopier.copyPivotStyles();

        var targetStyles = targetWorkbook.getXSSFWorkbook().getStylesSource().getCTStylesheet();
        assertTrue(targetStyles.isSetDxfs());
        var targetDxfs = targetStyles.getDxfs();
        assertEquals(2, targetDxfs.sizeOfDxfArray());
        assertTrue(pivotStyleWithBorder.valueEquals(targetDxfs.getDxfArray(0)));
        assertTrue(pivotStyleWithFont.valueEquals(targetDxfs.getDxfArray(1)));

    }

    @Test
    void shouldNotCopyAnythingIfWrongTypeOfWorkbook() {
        var sourceWorkbook = new SXSSFWorkbook();
        var targetWorkbook = new SXSSFWorkbook();

        var pivotFormatCopier = new PivotStyleCopier(sourceWorkbook, targetWorkbook);
        pivotFormatCopier.copyPivotStyles();

        assertFalse(targetWorkbook.getXSSFWorkbook().getStylesSource().getCTStylesheet().isSetDxfs());
    }

    @Test
    void shouldNotCopyAnythingIfNoDxfsSetOnSource() {
        var sourceWorkbook = new XSSFWorkbook();
        var targetWorkbook = new SXSSFWorkbook();

        var pivotFormatCopier = new PivotStyleCopier(sourceWorkbook, targetWorkbook);
        pivotFormatCopier.copyPivotStyles();

        assertFalse(targetWorkbook.getXSSFWorkbook().getStylesSource().getCTStylesheet().isSetDxfs());
    }

    @Test
    void shouldNotCopyAnythingIfNoDxfsInSourceArray() {
        var sourceWorkbook = new XSSFWorkbook();
        sourceWorkbook.getStylesSource().getCTStylesheet().addNewDxfs();
        var targetWorkbook = new SXSSFWorkbook();

        var pivotFormatCopier = new PivotStyleCopier(sourceWorkbook, targetWorkbook);
        pivotFormatCopier.copyPivotStyles();

        assertFalse(targetWorkbook.getXSSFWorkbook().getStylesSource().getCTStylesheet().isSetDxfs());
    }


    @Test
    void shouldThrowErrorIfTargetWorkbookIsWrongType() {
        var sourceWorkbook = new XSSFWorkbook();
        var targetWorkbook = new XSSFWorkbook();

        var pivotFormatCopier = new PivotStyleCopier(sourceWorkbook, targetWorkbook);
        assertThrows(ReportGenerationException.InvalidWorkbookTypeException.class, pivotFormatCopier::copyPivotStyles);

    }

}