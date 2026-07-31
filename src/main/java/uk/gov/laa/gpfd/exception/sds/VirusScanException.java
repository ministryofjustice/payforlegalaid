package uk.gov.laa.gpfd.exception.sds;

/**
 * Exception thrown when the virus scanner returns a non-standard result.
 */
public class VirusScanException extends RuntimeException {
  public VirusScanException(String message) {
    super(message);
  }

  public VirusScanException(String message, Throwable cause) {
    super(message, cause);
  }
}

