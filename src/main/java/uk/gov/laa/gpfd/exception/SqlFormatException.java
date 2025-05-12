package uk.gov.laa.gpfd.exception;

/**
 * Exception class to indicate that the SQL query format does not match our expectations
 */
public class SqlFormatException extends RuntimeException {
    public SqlFormatException(String message) {
        super(message);
    }
}
