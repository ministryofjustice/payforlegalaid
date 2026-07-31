package uk.gov.laa.gpfd.exception.sds;

/**
 * Exception thrown when a file upload encounters a conflict (e.g., file already exists).
 */
public class FileConflictException extends RuntimeException {
  public FileConflictException(String message) {
    super(message);
  }

  public FileConflictException(String message, Throwable cause) {
    super(message, cause);
  }
}

