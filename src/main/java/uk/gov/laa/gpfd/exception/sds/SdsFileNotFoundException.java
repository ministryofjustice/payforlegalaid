package uk.gov.laa.gpfd.exception.sds;

/**
 * Exception thrown when a file is not found in the SDS service.
 */
public class SdsFileNotFoundException extends RuntimeException {
  public SdsFileNotFoundException(String message) {
    super(message);
  }

  public SdsFileNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}

