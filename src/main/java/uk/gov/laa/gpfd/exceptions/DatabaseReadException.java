package uk.gov.laa.gpfd.exceptions;

/**
 * Exception class to indicate that an error occurred while reading from the database
 */
public class DatabaseReadException extends RuntimeException {
  public DatabaseReadException(String message) {
    super(message);
  }

}
