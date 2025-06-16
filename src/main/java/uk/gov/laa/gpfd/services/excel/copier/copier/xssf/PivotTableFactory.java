package uk.gov.laa.gpfd.services.excel.copier.copier.xssf;

import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFPivotTable;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.Objects;

import static uk.gov.laa.gpfd.exception.ReportGenerationException.PivotTableCreationException;

/**
 * Factory interface for creating pivot tables with customizable creation strategies.
 */
public interface PivotTableFactory {

    /**
     * Creates a new pivot table in the specified sheet.
     *
     * @param sourceArea the source data area reference (must not be null)
     * @param topLeft the top-left cell reference where the pivot table should be placed (must not be null)
     * @param sheet the target sheet where the pivot table will be created (must not be null)
     * @return the newly created pivot table
     * @throws IllegalArgumentException if any parameter is null
     * @throws RuntimeException if pivot table creation fails
     */
    XSSFPivotTable createPivotTable(AreaReference sourceArea, CellReference topLeft, XSSFSheet sheet);

    /**
     * Returns the default pivot table factory implementation.
     *
     * @return a factory that uses the standard POI pivot table creation mechanism
     */
    static PivotTableFactory defaultFactory() {
        return new DefaultPivotTableFactory();
    }

    /**
     * Creates a custom factory using the provided creation function.
     *
     * @param creator the function that implements pivot table creation
     * @return a factory that delegates to the provided function
     */
    static PivotTableFactory customFactory(PivotTableCreator creator) {
        return creator::create;
    }

    /**
     * Functional interface for custom pivot table creation.
     */
    @FunctionalInterface
    interface PivotTableCreator {
        XSSFPivotTable create(AreaReference sourceArea, CellReference topLeft, XSSFSheet sheet);
    }

    /**
     * Default implementation of {@link PivotTableFactory} that uses the standard
     * POI sheet.createPivotTable() method.
     */
    class DefaultPivotTableFactory implements PivotTableFactory {

        /**
         * Creates a pivot table using the standard POI mechanism.
         *
         * @param sourceArea the source data area
         * @param topLeft the target location
         * @param sheet the target sheet
         * @return the created pivot table
         * @throws IllegalArgumentException if parameters are invalid
         * @throws IllegalStateException if pivot table cannot be created
         */
        @Override
        public XSSFPivotTable createPivotTable(AreaReference sourceArea, CellReference topLeft, XSSFSheet sheet) {
            Objects.requireNonNull(sourceArea, "AreaReference must not be null");
            Objects.requireNonNull(topLeft, "CellReference stream must not be null");
            Objects.requireNonNull(sheet, "Sheet must not be null");

            try {
                return sheet.createPivotTable(sourceArea, topLeft);
            } catch (Exception e) {
                throw new PivotTableCreationException("Failed to create pivot table", e);
            }
        }
    }
}