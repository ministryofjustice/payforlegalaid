package uk.gov.laa.gpfd.exception;

import lombok.Getter;

import java.util.UUID;

/**
 * Exception class to indicate that the report cannot be downloaded via this endpoint
 */
@Getter
public class ReportNotSupportedForDownloadException extends RuntimeException {
    final UUID reportId;
    final String errorMessage;

    public ReportNotSupportedForDownloadException(UUID reportId, String errorMessage) {
        super();
        this.reportId = reportId;
        this.errorMessage = errorMessage;
    }
}
