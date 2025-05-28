package uk.gov.laa.gpfd.exception;

/**
 * A sealed abstract class representing a hierarchy of exceptions related to database read operations.
 * This class serves as the base for specific exceptions that can occur during database reads.
 * It extends {@link RuntimeException}, making it an unchecked exception.
 */
public abstract sealed class DatabaseReadException extends RuntimeException {

    /**
     * Constructs a new {@code DatabaseReadException} with the specified error message.
     *
     * @param message the detail message describing the exception.
     */
    protected DatabaseReadException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code DatabaseReadException} with the specified error message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).  (A {@code null} value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     */
    protected DatabaseReadException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Represents an exception that occurs when attempting to fetch data from the database fails.
     * This is a specific type of {@link DatabaseReadException}.
     */
    public static final class DatabaseFetchException extends DatabaseReadException {
        public DatabaseFetchException(String s) {
            super(s);
        }

        /**
         * Constructs a new {@code DatabaseFetchException} with the specified error message.
         *
         * @param message the detail message (which is saved for later retrieval
         *                by the {@link #getMessage()} method).
         * @param cause   the root cause (usually from using an underlying
         *                data access API such as JDBC)
         */
        public DatabaseFetchException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Represents an exception that occurs when mapping data from the database fails.
     * This is a specific type of {@link DatabaseReadException}.
     */
    public static final class MappingException extends DatabaseReadException {
        public MappingException(String s) {
            super(s);
        }
    }

    /**
     * Represents an exception that occurs when the stored query in the database does not meet our business rules.
     * This is a specific type of {@link DatabaseReadException}.
     */
    public static final class SqlFormatException extends DatabaseReadException {
        public SqlFormatException(String message) {
            super(message);
        }
    }
}
