package uk.gov.laa.gpfd.exception;

import lombok.Getter;

import java.util.UUID;

/**
 * Exception class to indicate that the report cannot be accessed by this user
 */
@Getter
public class ReportAccessException extends RuntimeException {

    private final UUID reportId;
    private final String errorMessage;

    public ReportAccessException(UUID reportId) {
        super();
        this.reportId = reportId;
        this.errorMessage = "You cannot access report with ID: " + reportId;
    }
}
