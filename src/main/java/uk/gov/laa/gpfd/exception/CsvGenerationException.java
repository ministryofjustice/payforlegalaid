package uk.gov.laa.gpfd.exception;

import tools.jackson.core.JacksonException;

/**
 * Base sealed exception class for CSV generation related errors.
 * <p>
 * This abstract class serves as the root of a hierarchy of exceptions that may occur
 * during CSV report generation operations. Being sealed, it explicitly controls
 * which classes can extend it, ensuring a closed set of possible exception types.
 * </p>
 */
public abstract sealed class CsvGenerationException extends RuntimeException {

    /**
     * Constructs a new CSV generation exception with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method)
     */
    protected CsvGenerationException(String message) {
        super(message);
    }

    /**
     * Constructs a new report generation exception with the specified detail message
     * and cause.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method)
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method)
     */
    protected CsvGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Exception indicating the metadata from the result set is invalid.
     */
    public static final class MetadataInvalidException extends CsvGenerationException {

        /**
         * Constructs a new metadata invalid exception with the specified detail message.
         *
         * @param message the detail message explaining the workbook type mismatch
         */
        public MetadataInvalidException(String message) {
            super(message);
        }
    }

    /**
     * Exception indicating an error writing to the csv output stream.
     */
    public static final class WritingToCsvException extends CsvGenerationException {

        /**
         * Constructs a new writing to csv exception with the specified detail message.
         *
         * @param message the detail message
         * @param e the IOException that caused the error
         */
        public WritingToCsvException(String message, JacksonException e) {
            super(message, e);
        }
    }

}