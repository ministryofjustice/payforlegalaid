package uk.gov.laa.pfla.auth.service.exceptions;

/**
 * Exception class to be used by the UserService.
 */
public class UserServiceException extends Exception {
  public UserServiceException(String message) {
    super(message);
  }

  public UserServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
