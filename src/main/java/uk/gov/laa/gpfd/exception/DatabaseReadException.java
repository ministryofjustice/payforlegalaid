package uk.gov.laa.gpfd.exception;

/**
 * A sealed abstract class representing a hierarchy of exceptions related to database read operations.
 * This class serves as the base for specific exceptions that can occur during database reads.
 * It extends {@link RuntimeException}, making it an unchecked exception.
 */
public class DatabaseReadException extends RuntimeException {
    public DatabaseReadException(String message) {
        super(message);
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
