package uk.gov.laa.gpfd.exception;

/**
 * Exception class to indicate that the endpoint is not supported
 */
public class OperationNotSupportedException extends RuntimeException {
    public OperationNotSupportedException(String endpoint) {
        super("Operation " + endpoint + " is not supported on this instance");
    }
}
