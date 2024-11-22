package uk.gov.laa.gpfd.exceptions;

/**
 * Exception class to indicate that an error occurred while attempting to create a CSV data stream
 */
public class CsvStreamException extends RuntimeException {
  public CsvStreamException(String message) {
    super(message);
  }

}
