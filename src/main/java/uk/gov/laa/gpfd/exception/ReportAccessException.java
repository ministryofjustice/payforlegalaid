package uk.gov.laa.gpfd.exception;

import lombok.Getter;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.util.UUID;

/**
 * Exception class to indicate that the report cannot be accessed by this user
 */
@Getter
public class ReportAccessException extends RuntimeException {

    private final UUID reportId;
    private final String errorMessage;

    public ReportAccessException(UUID reportId, String errorMessage) {
        super();
        this.reportId = reportId;
        this.errorMessage = errorMessage;
    }
}
