package uk.gov.laa.gpfd.services.excel.editor;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFPivotCacheDefinition;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/***
 * The {@code PivotTableRefresher} interface defines a contract for refreshing pivot tables
 * in an Excel workbook. It provides a default implementation for setting the {@code refreshOnLoad}
 * flag on pivot cache definitions in an XSSF workbook (used for .xlsx files).
 *
 * <p>This interface is designed to encapsulate the logic for ensuring that pivot tables
 * are refreshed when the workbook is opened in Excel.
 *
 * <p>The default implementation works with {@link XSSFWorkbook} and sets the {@code refreshOnLoad}
 * flag for all pivot cache definitions in the workbook. If the workbook is not an instance
 * of {@link XSSFWorkbook}, the method does nothing.
 */
public interface PivotTableRefresher {

    String PACKAGE_RELATIONSHIP_TYPE = "pivotCacheDefinition";

    /**
     * Sets the {@code refreshOnLoad} flag for all pivot cache definitions in the provided workbook.
     * This ensures that pivot tables are refreshed automatically when the workbook is opened in Excel.
     *
     * @param workbook the workbook containing the pivot tables to refresh. Must be an instance
     *                 of {@link XSSFWorkbook} for the refresh logic to be applied.
     *
     * @throws IllegalArgumentException if the {@code workbook} parameter is {@code null}.
     */
    default void refreshPivotTables(Workbook workbook) {
        if (workbook == null) {
            throw new IllegalArgumentException("Workbook cannot be null");
        }

        if (workbook instanceof XSSFWorkbook xssfWorkbook) {
            xssfWorkbook.getRelationParts().stream()
                    .filter(part -> part.getRelationship().getRelationshipType().contains(PACKAGE_RELATIONSHIP_TYPE))
                    .map(part -> (XSSFPivotCacheDefinition) xssfWorkbook.getRelationById(part.getRelationship().getId()))
                    .forEach(cache -> cache.getCTPivotCacheDefinition().setRefreshOnLoad(true));
        }
    }
}
