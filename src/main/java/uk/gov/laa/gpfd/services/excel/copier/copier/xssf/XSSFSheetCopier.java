package uk.gov.laa.gpfd.services.excel.copier.copier.xssf;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import uk.gov.laa.gpfd.services.excel.copier.SheetCopier;

import static uk.gov.laa.gpfd.exception.ReportGenerationException.PivotTableCopyException;


/**
 * A specialized {@link SheetCopier} implementation for XSSF (Excel 2007+) sheets that handles
 * copying of pivot tables in addition to standard sheet content.
 *
 * <p>This copier extends the basic sheet copying functionality to support XSSF-specific features:
 * <ul>
 *   <li>Pivot table definitions and cache</li>
 *   <li>Pivot table layout and formatting</li>
 *   <li>Pivot table data source references</li>
 * </ul>
 * </p>
 */
public class XSSFSheetCopier extends SheetCopier {
    /** The source XSSF workbook containing pivot table definitions */
    private final XSSFWorkbook sourceWorkbook;

    /** The target XSSF workbook where pivot tables will be recreated */
    private final XSSFWorkbook targetWorkbook;

    /** The source XSSF sheet containing pivot tables */
    private final XSSFSheet xssfSourceSheet;

    /** The target XSSF sheet where pivot tables will be copied */
    private final XSSFSheet xssfTargetSheet;

    /**
     * Creates a new XSSFSheetCopier instance for copying between XSSF sheets.
     *
     * @param sourceWorkbook the source XSSF workbook (must not be null)
     * @param targetWorkbook the target XSSF workbook (must not be null)
     * @param sourceSheet the source XSSF sheet to copy from (must not be null)
     * @param targetSheet the target XSSF sheet to copy to (must not be null)
     * @throws IllegalArgumentException if any parameter is null
     */
    public XSSFSheetCopier(XSSFWorkbook sourceWorkbook, XSSFWorkbook targetWorkbook,
                           XSSFSheet sourceSheet, XSSFSheet targetSheet) {
        super(sourceSheet, targetSheet);
        this.xssfSourceSheet = sourceSheet;
        this.xssfTargetSheet = targetSheet;
        this.sourceWorkbook = sourceWorkbook;
        this.targetWorkbook = targetWorkbook;
    }

    /**
     * Copies XSSF-specific features, primarily pivot tables, from source to target sheet.
     */
    @Override
    protected void copyAdditionalFeatures() {
        var pivotTables = xssfSourceSheet.getPivotTables();
        if (null == pivotTables || pivotTables.isEmpty()) {
            return;
        }

        for (int i = 0; i < pivotTables.size();) {
            var pivotTable = pivotTables.get(i);
            try {
                PivotTableDirector.standard(PivotTableBuilder.create(
                        sourceWorkbook,
                        targetWorkbook,
                        xssfSourceSheet,
                        xssfTargetSheet,
                        pivotTable))
                        .construct();
            } catch (Exception e) {
                throw new PivotTableCopyException(xssfSourceSheet.getSheetName(), "Failed to copy pivot table", e);
            }
            i++;
        }
    }
}