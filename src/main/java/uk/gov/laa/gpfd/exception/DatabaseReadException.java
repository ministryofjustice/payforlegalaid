package uk.gov.laa.gpfd.exception;

/**
 * Exception class to indicate that an error occurred while reading from the database
 */
public class DatabaseReadException extends RuntimeException {
    public DatabaseReadException(String message) {
        super(message);
    }

    public static class SqlFormatException extends DatabaseReadException {
        public SqlFormatException(String message) {
            super(message);
        }
    }
}
