package uk.gov.laa.gpfd.services.excel.copier.types.xssf;

import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.xssf.usermodel.XSSFPivotTable;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import uk.gov.laa.gpfd.services.excel.editor.PivotTableRefresher;

/**
 * Builder for creating and configuring pivot tables when copying between workbooks.
 * <p>
 * This builder handles the complete process of pivot table creation including:
 * <ul>
 *   <li>Header row creation</li>
 *   <li>Pivot table instantiation</li>
 *   <li>Layout and style configuration</li>
 *   <li>Pivot cache configuration</li>
 * </ul>
 * </p>
 *
 */
public class PivotTableBuilder implements PivotTableRefresher {
    private final XSSFWorkbook sourceWorkbook;
    private final XSSFWorkbook targetWorkbook;
    private final XSSFSheet sourceSheet;
    private final XSSFSheet targetSheet;
    private final XSSFPivotTable sourcePivotTable;
    private PivotTableFactory factory = PivotTableFactory.defaultFactory();
    private PivotTableConfigurator configurator = new PivotTableConfigurator.CompositeConfigurator();
    private boolean refreshOnLoad = true;

    /**
     * Constructs a new PivotTableBuilder instance.
     *
     * @param sourceWorkbook the source workbook containing the original pivot table
     * @param targetWorkbook the target workbook where the new pivot table will be created
     * @param sourceSheet the source worksheet containing the original pivot table
     * @param targetSheet the target worksheet where the new pivot table will be placed
     * @param sourcePivotTable the source pivot table to copy
     */
    private PivotTableBuilder(XSSFWorkbook sourceWorkbook, XSSFWorkbook targetWorkbook,
                              XSSFSheet sourceSheet, XSSFSheet targetSheet,
                              XSSFPivotTable sourcePivotTable) {
        this.sourceWorkbook = sourceWorkbook;
        this.targetWorkbook = targetWorkbook;
        this.sourceSheet = sourceSheet;
        this.targetSheet = targetSheet;
        this.sourcePivotTable = sourcePivotTable;
    }

    /**
     * Creates a new PivotTableBuilder instance with the specified configuration.
     *
     * @param sourceWorkbook the source workbook (must not be null)
     * @param targetWorkbook the target workbook (must not be null)
     * @param sourceSheet the source worksheet (must not be null)
     * @param targetSheet the target worksheet (must not be null)
     * @param sourcePivotTable the source pivot table to copy (must not be null)
     * @return a new PivotTableBuilder instance
     * @throws IllegalArgumentException if any parameter is null
     */
    public static PivotTableBuilder create(XSSFWorkbook sourceWorkbook, XSSFWorkbook targetWorkbook,
                                           XSSFSheet sourceSheet, XSSFSheet targetSheet,
                                           XSSFPivotTable sourcePivotTable) {
        return new PivotTableBuilder(sourceWorkbook, targetWorkbook, sourceSheet, targetSheet, sourcePivotTable);
    }

    /**
     * Sets the factory to use for pivot table creation.
     *
     * @param factory the factory implementation (must not be null)
     * @return this builder instance for method chaining
     * @throws IllegalArgumentException if factory is null
     */
    public PivotTableBuilder withFactory(PivotTableFactory factory) {
        this.factory = factory;
        return this;
    }

    /**
     * Sets the configurator to use for pivot table layout and styling.
     *
     * @param configurator the configurator implementation (must not be null)
     * @return this builder instance for method chaining
     * @throws IllegalArgumentException if configurator is null
     */
    public PivotTableBuilder withConfigurator(PivotTableConfigurator configurator) {
        this.configurator = configurator;
        return this;
    }

    /**
     * Configures whether the pivot table should refresh automatically when loaded.
     *
     * @param refreshOnLoad true to enable refresh on load, false to disable
     * @return this builder instance for method chaining
     */
    public PivotTableBuilder withRefreshOnLoad(boolean refreshOnLoad) {
        this.refreshOnLoad = refreshOnLoad;
        return this;
    }

    /**
     * Builds and configures the pivot table in the target workbook.
     * <p>
     * The build process:
     * <ol>
     *   <li>Creates header row if needed</li>
     *   <li>Creates the pivot table using the configured factory</li>
     *   <li>Applies configuration using the configured configurator</li>
     *   <li>Configures the pivot cache</li>
     * </ol>
     * </p>
     */
    public void build() {
        var sourceArea = sourcePivotTable.getPivotCacheDefinition().getPivotArea(sourceWorkbook);
        if (null == sourceArea) return;

        createHeaderRow(sourceArea);
        var targetPivot = createPivotTable(sourceArea);
        configurator.configure(sourcePivotTable, targetPivot);
        configurePivotCache(targetPivot);
    }

    /**
     * Creates a header row in the target sheet if one doesn't exist.
     *
     * @param sourceArea the source area reference used to determine column count
     */
    private void createHeaderRow(AreaReference sourceArea) {
        var headerRow = targetSheet.getRow(0);
        if (null == headerRow) {
            headerRow = targetSheet.createRow(0);
        }

        var lastCol = sourceArea.getLastCell().getCol();
        for (var i = 0; i <= lastCol; i++) {
            if (null == headerRow.getCell(i)) {
                headerRow.createCell(i);
            }
        }
    }

    /**
     * Creates a new pivot table in the target sheet.
     *
     * @param sourceArea the source data area reference
     * @return the newly created pivot table
     * @throws PivotTableCreationException if pivot table creation fails
     */
    private XSSFPivotTable createPivotTable(AreaReference sourceArea) {
        var topLeft = sourceArea.getFirstCell();
        return factory.createPivotTable(sourceArea, topLeft, targetSheet);
    }

    /**
     * Configures the pivot cache for the target pivot table.
     *
     * @param targetPivot the target pivot table to configure
     */
    private void configurePivotCache(XSSFPivotTable targetPivot) {
        var targetCacheDef = targetPivot.getPivotCacheDefinition();
        var targetCTCacheDef = targetCacheDef.getCTPivotCacheDefinition();
        targetCTCacheDef.set(sourcePivotTable.getPivotCacheDefinition().getCTPivotCacheDefinition());

        var newCacheId = targetWorkbook.getPivotTables().size() + 1;
        targetCTCacheDef.setId(String.valueOf(newCacheId));

        var pivotDef = targetPivot.getCTPivotTableDefinition();
        pivotDef.setCacheId(newCacheId);

        var cacheSource = targetCTCacheDef.getCacheSource();
        if (null != cacheSource  && null != cacheSource.getWorksheetSource()) {
            var ref = targetCacheDef.getPivotArea(targetWorkbook).formatAsString();
            cacheSource.getWorksheetSource().setRef(ref);
        }

        if (refreshOnLoad) {
            refreshPivotTables(targetWorkbook);
        }
    }
}