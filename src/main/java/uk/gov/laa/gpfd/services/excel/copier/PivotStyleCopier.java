package uk.gov.laa.gpfd.services.excel.copier;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import uk.gov.laa.gpfd.exception.ReportGenerationException.InvalidWorkbookTypeException;

import java.util.Objects;

/**
 * A class to copy pivot styles from the template to the new workbook
 * Pivot styles (called dxf in the underlying xml) are stored differently to cell styles
 * hence why it is necessary to copy these separately
 */
public class PivotStyleCopier {

    /** The source (template) workbook to copy from */
    private final Workbook sourceWorkbook;

    /** The target workbook to copy to */
    private final SXSSFWorkbook targetWorkbook;

    /**
     * Creates a new PivotStyleCopier instance.
     *
     * @param sourceWorkbook the source workbook to copy from
     * @param targetWorkbook the target workbook to copy to
     */
    public PivotStyleCopier(Workbook sourceWorkbook, Workbook targetWorkbook) {

        Objects.requireNonNull(sourceWorkbook, "Source workbook must not be null");
        Objects.requireNonNull(targetWorkbook, "Target workbook must not be null");

        if (!(targetWorkbook instanceof SXSSFWorkbook)) {
            throw new InvalidWorkbookTypeException("Target workbook must be SXSSFWorkbook but was " + targetWorkbook.getClass().getSimpleName());
        }

        this.targetWorkbook = (SXSSFWorkbook) targetWorkbook;
        this.sourceWorkbook = sourceWorkbook;
    }

    /**
     * Copies pivot table-specific styles over from the template to the new workbook.
     * Pivot styles are "dxf" style sections rather than the cellStyles used elsewhere
     *
     * @throws InvalidWorkbookTypeException if target is wrong type to add styles to
     */
    public void copyPivotStyles() {
        if (!(sourceWorkbook instanceof XSSFWorkbook)) {
            // Pivots not supported in this template so nothing to copy
            return;
        }

        var srcStyleSheet = ((XSSFWorkbook) sourceWorkbook).getStylesSource().getCTStylesheet();
        var targetStyleSheet = targetWorkbook.getXSSFWorkbook().getStylesSource().getCTStylesheet();

        if (!srcStyleSheet.isSetDxfs()) {
            // No pivot styles on the source sheet
            return;
        }

        var srcDxfs = srcStyleSheet.getDxfs();

        if (null == srcDxfs || srcDxfs.sizeOfDxfArray() == 0) {
            // No pivot styles on the source sheet
            return;
        }

        var targetDxfs = targetStyleSheet.isSetDxfs() ? targetStyleSheet.getDxfs() : targetStyleSheet.addNewDxfs();

        for (var dxf : srcDxfs.getDxfList()) {
            var newDxf = targetDxfs.addNewDxf();
            newDxf.set(dxf);
        }

        targetDxfs.setCount(targetDxfs.sizeOfDxfArray());
    }
}