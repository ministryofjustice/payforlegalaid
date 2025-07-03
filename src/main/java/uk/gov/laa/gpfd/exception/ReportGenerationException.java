package uk.gov.laa.gpfd.exception;

/**
 * Base sealed exception class for report generation related errors.
 * <p>
 * This abstract class serves as the root of a hierarchy of exceptions that may occur
 * during Excel report generation operations. Being sealed, it explicitly controls
 * which classes can extend it, ensuring a closed set of possible exception types.
 * </p>
 */
public abstract sealed class ReportGenerationException extends RuntimeException {

    /**
     * Constructs a new report generation exception with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method)
     */
    protected ReportGenerationException(String message) {
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
    protected ReportGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Exception indicating the workbook type is invalid for the requested operation.
     * <p>
     * Typically thrown when an operation requires an SXSSF workbook but receives
     * a different workbook implementation.
     * </p>
     */
    public static final class InvalidWorkbookTypeException extends ReportGenerationException {

        /**
         * Constructs a new invalid workbook type exception with the specified detail message.
         *
         * @param message the detail message explaining the workbook type mismatch
         */
        public InvalidWorkbookTypeException(String message) {
            super(message);
        }
    }

    /**
     * Exception indicating a requested sheet was not found in the workbook.
     * <p>
     * Contains details about the missing sheet to help with debugging.
     * </p>
     */
    public static final class SheetNotFoundException extends ReportGenerationException {

        /**
         * Constructs a new sheet not found exception with the specified detail message.
         *
         * @param message the detail message including the sheet name that wasn't found
         */
        public SheetNotFoundException(String message) {
            super(message);
        }
    }

    /**
     * Exception indicating a failure during sheet copying operations.
     * <p>
     * This exception typically wraps the underlying cause of the failure and
     * provides context about the copying operation that failed.
     * </p>
     */
    public static final class SheetCopyException extends ReportGenerationException {

        /**
         * Constructs a new sheet copy exception with the specified detail message and cause.
         *
         * @param message the detail message about the copying failure
         * @param cause   the underlying cause of the copying failure
         */
        public SheetCopyException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Exception thrown when pivot table creation fails.
     * <p>
     * This exception wraps the underlying cause of failure and provides
     * context-specific error messages for pivot table operations.
     * </p>
     */
    public static final class PivotTableCreationException extends ReportGenerationException {

        /**
         * Constructs a new exception with a detailed message.
         *
         * @param message the detail message explaining the failure
         */
        public PivotTableCreationException(String message) {
            super(message);
        }

        /**
         * Constructs a new exception with a detailed message and cause.
         *
         * @param message the detail message explaining the failure
         * @param cause   the underlying cause of the exception
         */
        public PivotTableCreationException(String message, Throwable cause) {
            super(message, cause);
        }

        /**
         * Constructs a new exception with a cause.
         *
         * @param cause the underlying cause of the exception
         */
        public PivotTableCreationException(Throwable cause) {
            super("Failed to create pivot table", cause);
        }
    }

    /**
     * Exception thrown when pivot table copying operations fail.
     * <p>
     * This exception provides detailed context about the pivot table that failed to copy, including the sheet name.
     * </p>
     */
    public static final class PivotTableCopyException extends ReportGenerationException {
        private final String sheetName;

        /**
         * Constructs a new pivot table copy exception with context details.
         *
         * @param sheetName the name of the sheet containing the pivot table
         * @param message   the detail message explaining the failure
         */
        public PivotTableCopyException(String sheetName, String message) {
            super(String.format("Failed to copy pivot table in sheet '%s': %s", sheetName, message));
            this.sheetName = sheetName;
        }

        /**
         * Constructs a new pivot table copy exception with context details and cause.
         *
         * @param sheetName the name of the sheet containing the pivot table
         * @param message   the detail message explaining the failure
         * @param cause     the underlying cause of the failure
         */
        public PivotTableCopyException(String sheetName, String message, Throwable cause) {
            super(String.format("Failed to copy pivot table in sheet '%s': %s",
                    sheetName, message), cause);
            this.sheetName = sheetName;
        }

        public String getSheetName() {
            return sheetName;
        }
    }
}