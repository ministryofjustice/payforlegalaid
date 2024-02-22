package uk.gov.laa.pfla.auth.service.exceptions;

/**
 * Exception class to indicate that no user details were returned from the external directory
 */
public class ReportIdNotFoundException extends RuntimeException {
  public ReportIdNotFoundException(String message) {
    super(message);
  }

}
