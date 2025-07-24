package uk.gov.laa.gpfd.exception;

import java.util.UUID;

/**
 * Exception class to indicate that no report was found in the database with the requested ID
 */
public class ReportIdNotFoundException extends RuntimeException {

    /**
     * Constructs a new exception with a detailed message.
     *
     * @param message the detail message explaining the specific reason for the exception
     */
    public ReportIdNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception for a missing report identified by UUID.
     * <p>
     * Automatically generates a standard message in the format:
     * "Report not found for ID [uuid-value]"
     * </p>
     *
     * @param uuid the UUID of the report that could not be found
     * @throws NullPointerException if the provided UUID is null
     */
    public ReportIdNotFoundException(UUID uuid) {
        super("Report not found for ID %s".formatted(uuid.toString()));
    }
}