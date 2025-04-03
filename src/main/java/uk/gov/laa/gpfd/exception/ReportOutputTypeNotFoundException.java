package uk.gov.laa.gpfd.exception;

/**
 * Exception class to indicate that no report was found in the database with the requested ID
 */
public class ReportOutputTypeNotFoundException extends RuntimeException {
    public ReportOutputTypeNotFoundException(String message) {
        super(message);
    }
}
