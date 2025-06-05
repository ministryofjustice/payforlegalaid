package uk.gov.laa.gpfd.model;

/**
 * Defines a projection between a source data field and its mapped representation in the xlsx report.
 * This interface describes how a field from a data source (e.g., database column) should be
 * represented when projected to a destination (e.g., Excel column).
 */
public interface FieldProjection {

    /**
     * Returns the original field name from the source system.
     */
    String getSourceName();

    /**
     * Returns the destination name for the projected field.
     */
    String getMappedName();
}
