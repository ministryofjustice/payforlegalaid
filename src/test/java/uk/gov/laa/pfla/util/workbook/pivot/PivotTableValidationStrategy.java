package uk.gov.laa.pfla.util.workbook.pivot;

import org.apache.poi.xssf.usermodel.XSSFPivotCacheRecords;
import org.apache.poi.xssf.usermodel.XSSFPivotTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCacheField;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFormat;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotCacheDefinition;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotField;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static org.apache.poi.xssf.usermodel.XSSFRelation.PIVOT_CACHE_RECORDS;
import static uk.gov.laa.pfla.util.workbook.pivot.PivotTableValidationStrategy.*;

/**
 * An interface defining the contract for pivot table validation strategies.
 * <p>
 * Implementations of this interface validate specific aspects of an Excel pivot table's
 * structure and configuration. Each implementation should focus on a single validation
 * concern.
 * </p>
 */
sealed interface PivotTableValidationStrategy permits CacheDefinitionValidation, PivotFormatValidation,
        DataSourceValidation, LocationValidation, PivotCacheFieldValidation, PivotFieldStyleValidation,
        PivotFieldsValidation, PivotCacheFieldStyleValidation {

    /**
     * Validates a specific aspect of the given pivot table.
     * <p>
     * Implementations should perform focused validation on one particular aspect
     * of the pivot table's structure or configuration. The method should throw
     * {@link PivotTableValidationException} with a descriptive message if validation fails.
     * </p>
     */
    void validate(XSSFPivotTable pivotTable) throws PivotTableValidationException;

    /**
     * Helper routine to get the workbook details from the pivot table
     *
     * @param pivotTable - pivotTable we are validating
     * @return workbook we are validating
     */
    static XSSFWorkbook getWorkbookFromPivotTable(XSSFPivotTable pivotTable) {
        // First parent is the sheet, whose parent is the workbook
        return (XSSFWorkbook) pivotTable.getParent().getParent();
    }

    /**
     * Validates the pivot table's pivot fields.
     * <p>
     * The PivotTable definition has PivotFields inside it.
     * Here we ensure the pivot fields are defined and non-empty.
     * If empty, the pivot is invalid and will cause an error opening the Excel file.
     * </p>
     */
    final class PivotFieldsValidation implements PivotTableValidationStrategy {

        @Override
        public void validate(XSSFPivotTable pivotTable) throws PivotTableValidationException {
            var pivotFields = pivotTable.getCTPivotTableDefinition().getPivotFields();
            if (pivotFields == null || pivotFields.getPivotFieldList().isEmpty()) {
                throw PivotTableValidationException.noFields(pivotTable);
            }
        }
    }

    /**
     * Validates the pivot table's pivot fields have valid style references.
     * <p>
     * The PivotTable definition has PivotFields inside it.
     * A PivotField can have a style set against it (a "numFmtId").
     * This tells it say that the field should try and use the DD/MM/YYYY style, etc.
     * Here we check the referenced format exists.
     * If the "numFmtId" it refers to does not exist, this suggests we have missed the formatting in our report.
     * </p>
     */
    final class PivotFieldStyleValidation implements PivotTableValidationStrategy {

        @Override
        public void validate(XSSFPivotTable pivotTable) throws PivotTableValidationException {
            var pivotFields = pivotTable.getCTPivotTableDefinition().getPivotFields();
            var styleSheet = getWorkbookFromPivotTable(pivotTable).getStylesSource();

            pivotFields.getPivotFieldList().stream()
                    // If this field isn't set that is fine, just means use standard formatting
                    .filter(CTPivotField::isSetNumFmtId)
                    .map(CTPivotField::getNumFmtId)
                    .map(l -> Short.parseShort(Long.toString(l)))
                    .map(styleSheet::getNumberFormatAt)
                    // If null, we've got an invalid reference!
                    .filter(Objects::isNull)
                    .findFirst()
                    .ifPresent(invalidFieldStyle -> {
                        throw PivotTableValidationException.invalidFieldStyle(pivotTable);
                    });

        }

    }

    /**
     * Validates the location definition of a pivot table.
     * <p>
     * Ensures the pivot table has a defined location in the worksheet where it should be rendered.
     * Without a valid location, the pivot table cannot be properly displayed.
     * </p>
     */
    final class LocationValidation implements PivotTableValidationStrategy {

        @Override
        public void validate(XSSFPivotTable pivotTable) throws PivotTableValidationException {
            if (pivotTable.getCTPivotTableDefinition().getLocation() == null) {
                throw PivotTableValidationException.noLocation(pivotTable);
            }
        }

    }

    /**
     * Validates the data source configuration of a pivot table.
     * <p>
     * Ensure the pivot table has a properly configured data source reference,
     * and then ensure the worksheet source referenced actually exists.
     * If this is not set, the pivot table will fail to pull data and not render.
     * </p>
     */
    final class DataSourceValidation implements PivotTableValidationStrategy {

        @Override
        public void validate(XSSFPivotTable pivotTable) throws PivotTableValidationException {
            var cacheDef = pivotTable.getPivotCacheDefinition();
            var cacheSource = cacheDef.getCTPivotCacheDefinition().getCacheSource();
            if (null == cacheSource) {
                throw PivotTableValidationException.noDataSource(pivotTable);
            }

            var worksheetSource = cacheSource.getWorksheetSource();
            if (null == worksheetSource) {
                throw PivotTableValidationException.invalidSourceSheet(pivotTable);
            }

            var sheetSourceName = worksheetSource.getSheet();

            if (null == getWorkbookFromPivotTable(pivotTable).getSheet(sheetSourceName)) {
                throw PivotTableValidationException.invalidSourceSheet(pivotTable);
            }

        }

    }

    /**
     * Validates the PivotCache has fields set.
     * <p>
     * The PivotCacheDefinition has "CachedFields" inside.
     * If these fields are not defined they will cause the pivot table to be missing information
     * </p>
     */
    final class PivotCacheFieldValidation implements PivotTableValidationStrategy {

        @Override
        public void validate(XSSFPivotTable pivotTable) throws PivotTableValidationException {

            var pivotCache = pivotTable.getPivotCacheDefinition().getCTPivotCacheDefinition();
            var cacheFields = pivotCache.getCacheFields();
            if (null == cacheFields || cacheFields.getCacheFieldList().isEmpty()) {
                // If there are no cache fields it is an incomplete pivot table
                throw PivotTableValidationException.noCachedFields(pivotTable);
            }
        }
    }

    /**
     * Validates the fields referenced in the pivot cache are set up correctly.
     * <p>
     * The PivotCacheDefinition has "CachedFields" inside. These contain the source field
     * and (optionally) a "numFmtId" that says what type of formatting should be used for the data.
     * We check these fields are in the source sheet and that any formats used are defined in the style sheet.
     * If these fields are not well-defined they will cause the pivot table to be missing information
     * </p>
     */
    final class PivotCacheFieldStyleValidation implements PivotTableValidationStrategy {

        @Override
        public void validate(XSSFPivotTable pivotTable) throws PivotTableValidationException {

            var pivotCache = pivotTable.getPivotCacheDefinition().getCTPivotCacheDefinition();
            var cacheFields = pivotCache.getCacheFields();
            var workbook = getWorkbookFromPivotTable(pivotTable);
            var styleSheet = workbook.getStylesSource();
            var sourceHeaders = getSourceHeaders(workbook, pivotCache);

            cacheFields.getCacheFieldList().stream()
                    .filter(CTCacheField::isSetNumFmtId)
                    .map(fmt -> (short) fmt.getNumFmtId())
                    .filter(this::isFormattingBeingUsed)
                    .filter(numFmtId -> null == styleSheet.getNumberFormatAt(numFmtId))
                    .findFirst()
                    .ifPresent(fmtId -> {
                        throw PivotTableValidationException.invalidCacheFieldStyle(pivotTable);
                    });

            cacheFields.getCacheFieldList().stream()
                    .filter(field -> !sourceHeaders.contains(field.getName()))
                    .findFirst()
                    .ifPresent(field -> {
                        throw PivotTableValidationException.invalidColumnReference(pivotTable);
                    });

        }

        private boolean isFormattingBeingUsed(int numFmtId) {
            // numFmt = 0 means no specific format is defined
            return numFmtId != 0;
        }

        private Set<String> getSourceHeaders(XSSFWorkbook workbook, CTPivotCacheDefinition pivotCache) {
            var sourceSheet = workbook.getSheet(pivotCache.getCacheSource().getWorksheetSource().getSheet());

            var sourceHeaderRow = sourceSheet.getRow(0);
            var headers = new HashSet<String>();
            for (var cell : sourceHeaderRow) {
                headers.add(cell.getStringCellValue().trim());
            }

            return headers;
        }
    }

    /**
     * Validates the pivot table's relationship with the cache.
     * <p>
     * Checks that the PivotTable definition has a valid relationship to the PivotCache definition, and the PivotCache definition has a
     * valid relationship to the PivotCacheRecord.
     * Without these, the pivot table will cause errors when you open the spreadsheet.
     * </p>
     */
    final class CacheDefinitionValidation implements PivotTableValidationStrategy {

        @Override
        public void validate(XSSFPivotTable pivotTable) throws PivotTableValidationException {
            var cacheDef = pivotTable.getPivotCacheDefinition();
            if (cacheDef == null || cacheDef.getCTPivotCacheDefinition() == null) {
                throw PivotTableValidationException.invalidCache(pivotTable);
            }

            // Check Pivot Cache Records exists
            var allAreValidObjects = cacheDef.getRelationParts().stream()
                    .filter(part -> part.getRelationship().getRelationshipType().contains(PIVOT_CACHE_RECORDS.getRelation()))
                    .map(part -> cacheDef.getRelationById(part.getRelationship().getId()))
                    .allMatch(XSSFPivotCacheRecords.class::isInstance);

            if (!allAreValidObjects) {
                throw PivotTableValidationException.invalidCacheRecordsRelationship(pivotTable);
            }
        }

    }

    /**
     * Validates the pivot table's formats.
     * <p>
     * Each format in the pivot table (in the "formats" array) have a reference to the "dxf" entry in the stylesheet.
     * The "dxf" in the stylesheet are a zero-indexed array, rather than having a uniquely defined ID,
     * i.e. if the format uses "dxfId="3", it will be the 4th entry in the array.
     * If these "dxf"s referenced are not in the stylesheet, then you will get a "needs repair" warning when you
     * open the spreadsheet in Excel.
     * </p>
     */
    final class PivotFormatValidation implements PivotTableValidationStrategy {

        @Override
        public void validate(XSSFPivotTable pivotTable) throws PivotTableValidationException {

            var ctPivot = pivotTable.getCTPivotTableDefinition();

            if (ctPivot.isSetFormats() && ctPivot.getFormats().getCount() > 0) {

                var ctStyleSheet = getWorkbookFromPivotTable(pivotTable).getStylesSource().getCTStylesheet();
                var howManyDxfIds = ctStyleSheet.isSetDxfs() ? ctStyleSheet.getDxfs().sizeOfDxfArray() : 0;

                pivotTable.getCTPivotTableDefinition().getFormats().getFormatList().stream()
                        .filter(CTFormat::isSetDxfId)
                        // As this is a zero-indexed array, its invalid if the id is bigger than the count
                        .filter(format -> format.getDxfId() >= howManyDxfIds)
                        .findFirst()
                        .ifPresent(field -> {
                            throw PivotTableValidationException.invalidFieldStyle(pivotTable);
                        });
            }

        }

    }
}
