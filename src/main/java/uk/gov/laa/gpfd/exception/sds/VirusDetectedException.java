package uk.gov.laa.gpfd.exception.sds;

/**
 * Exception thrown when a virus is detected in an uploaded file.
 */
public class VirusDetectedException extends RuntimeException {
  public VirusDetectedException(String message) {
    super(message);
  }

  public VirusDetectedException(String message, Throwable cause) {
    super(message, cause);
  }
}

