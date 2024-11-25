package uk.gov.laa.gpfd.exception;

/**
 * Exception class to be used by the UserService.
 */
public class UserServiceException extends RuntimeException {
    public UserServiceException(String message) {
        super(message);
    }

    public UserServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
