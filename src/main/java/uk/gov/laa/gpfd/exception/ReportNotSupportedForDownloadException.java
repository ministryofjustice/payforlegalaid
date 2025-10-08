package uk.gov.laa.gpfd.exception;

import lombok.Getter;

import java.util.UUID;

/**
 * Exception class to indicate that the endpoint is not supported
 */
@Getter
public class ReportNotSupportedForDownloadException extends RuntimeException {
    final UUID reportId;

    public ReportNotSupportedForDownloadException(UUID reportId) {
        super();
        this.reportId = reportId;
    }
}
