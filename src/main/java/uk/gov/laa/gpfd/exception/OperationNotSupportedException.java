package uk.gov.laa.gpfd.exception;

public class OperationNotSupportedException extends RuntimeException {
    public OperationNotSupportedException(String endpoint) {
        super("Operation " + endpoint + " is not supported on this instance");
    }
}
