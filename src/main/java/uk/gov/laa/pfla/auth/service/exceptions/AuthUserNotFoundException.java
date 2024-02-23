package uk.gov.laa.pfla.auth.service.exceptions;

/**
 * Exception class to indicate that no user details were returned from the external directory
 */
public class AuthUserNotFoundException extends UserServiceException {
  public AuthUserNotFoundException(String message) {
    super(message);
  }

}
