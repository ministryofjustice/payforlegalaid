package uk.gov.laa.pfla.util.workbook.pivot;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ooxml.POIXMLDocumentPart;

/**
 * Exception thrown when a pivot table fails validation checks.
 * <p>
 * This exception indicates that a pivot table did not meet one or more validation requirements
 * as defined by the {@link PivotTableValidationStrategy} implementations. The exception message
 * contains specific details about which validation check failed.
 * </p>
 */
@EqualsAndHashCode(callSuper = true)
public abstract class PivotTableValidationException extends RuntimeException {

    ErrorCase errorCase;
    POIXMLDocumentPart pivotTable;

    /**
     * Constructs a new validation exception for a specific error case.
     *
     * @param errorCase  The specific validation failure case
     * @param pivotTable The pivot table that failed validation
     */
    private PivotTableValidationException(ErrorCase errorCase, POIXMLDocumentPart pivotTable) {
        super(errorCase.formatMessage(pivotTable));
        this.errorCase = errorCase;
        this.pivotTable = pivotTable;
    }

    /**
     * Creates a validation exception for a pivot table with an invalid cache definition.
     */
    public static PivotTableValidationException invalidCache(POIXMLDocumentPart pivotTable) {
        return new PivotTableValidationException(ErrorCase.INVALID_CACHE, pivotTable) {};
    }

    /**
     * Creates a validation exception for a pivot table missing a data source reference.
     */
    public static PivotTableValidationException noDataSource(POIXMLDocumentPart pivotTable) {
        return new PivotTableValidationException(ErrorCase.NO_DATA_SOURCE, pivotTable) {};
    }

    /**
     * Creates a validation exception for a pivot table with no defined location.
     */
    public static PivotTableValidationException noLocation(POIXMLDocumentPart pivotTable) {
        return new PivotTableValidationException(ErrorCase.NO_LOCATION, pivotTable) {};
    }

    /**
     * Creates a validation exception for a pivot table with no configured fields.
     */
    public static PivotTableValidationException noFields(POIXMLDocumentPart pivotTable) {
        return new PivotTableValidationException(ErrorCase.NO_FIELDS, pivotTable) {};
    }

    /**
     * Creates a validation exception for a pivot table where the pivot cache and pivot cache records are not properly
     * linked together.
     */
    public static PivotTableValidationException invalidCacheRecordsRelationship(POIXMLDocumentPart pivotTable) {
        return new PivotTableValidationException(ErrorCase.INVALID_CACHE_RECORD_RELATIONSHIP, pivotTable) {
        };
    }

    /**
     * Creates a validation exception for a pivot table where the configured fields are referencing invalid styles.
     */
    public static PivotTableValidationException invalidFieldStyle(POIXMLDocumentPart pivotTable) {
        return new PivotTableValidationException(ErrorCase.INVALID_FIELD_STYLES, pivotTable) {
        };
    }

    /**
     * Creates a validation exception for a pivot table where the configured cache fields are referencing invalid styles.
     */
    public static PivotTableValidationException invalidCacheFieldStyle(POIXMLDocumentPart pivotTable) {
        return new PivotTableValidationException(ErrorCase.INVALID_CACHE_FIELD_STYLES, pivotTable) {};
    }

    /**
     * Creates a validation exception for a pivot table where the source sheet is invalid.
     */
    public static PivotTableValidationException invalidSourceSheet(POIXMLDocumentPart pivotTable) {
        return new PivotTableValidationException(ErrorCase.INVALID_SOURCE_SHEET, pivotTable) {};
    }

    /**
     * Creates a validation exception for a pivot table definition uses columns that no longer exist.
     */
    public static PivotTableValidationException invalidColumnReference(POIXMLDocumentPart pivotTable) {
        return new PivotTableValidationException(ErrorCase.INVALID_COLUMN_REFERENCE, pivotTable) {};
    }

    /**
     * Creates a validation exception for a pivot table where the cache has no records and so is invalid.
     */
    public static PivotTableValidationException noCachedFields(POIXMLDocumentPart pivotTable) {
        return new PivotTableValidationException(ErrorCase.NO_CACHE_FIELDS, pivotTable) {};
    }

    /**
     * Defines the specific validation failure cases for pivot tables.
     */
    @RequiredArgsConstructor
    enum ErrorCase {
        INVALID_CACHE("Pivot table at %s has invalid cache definition"),
        NO_DATA_SOURCE("Pivot table at %s has no data source reference"),
        NO_LOCATION("Pivot table at %s has no defined location"),
        NO_FIELDS("Pivot table at %s has no configured fields"),
        INVALID_CACHE_RECORD_RELATIONSHIP("Pivot table at %s has a pivot cache which does not link to the pivot cache records file"),
        INVALID_FIELD_STYLES("Pivot table at %s has pivot fields with invalid style references"),
        INVALID_CACHE_FIELD_STYLES("Pivot table at %s has pivot cache fields with invalid style references"),
        INVALID_SOURCE_SHEET("Pivot table at %s is set to use a sheet that does not exist"),
        INVALID_COLUMN_REFERENCE("Pivot table at %s is referencing a data column that doesn't exist"),
        NO_CACHE_FIELDS("Pivot table at %s has no cached fields");

        private final String messageFormat;

        String formatMessage(POIXMLDocumentPart pivotTable) {
            return messageFormat.formatted(pivotTable);
        }
    }
}