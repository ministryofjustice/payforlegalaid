package uk.gov.laa.gpfd.exception;

/**
 * Exception class to indicate that the service can not be accessed
 */
public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message) {
        super(message);
    }
}