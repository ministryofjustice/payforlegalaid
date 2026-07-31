package uk.gov.laa.gpfd.exception.sds;

/**
 * Exception thrown when a file is missing the required content-length header.
 */
public class FileLengthRequiredException extends RuntimeException {
  public FileLengthRequiredException(String message) {
    super(message);
  }

  public FileLengthRequiredException(String message, Throwable cause) {
    super(message, cause);
  }
}

