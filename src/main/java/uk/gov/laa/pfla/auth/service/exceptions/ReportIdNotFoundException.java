package uk.gov.laa.pfla.auth.service.exceptions;

/**
 * Exception class to indicate that no report was found in the database with the requested ID
 */
public class ReportIdNotFoundException extends RuntimeException {
  public ReportIdNotFoundException(String message) {
    super(message);
  }

}
