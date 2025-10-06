package uk.gov.laa.gpfd.exception;

import java.util.UUID;

/**
 * Exception class to indicate that the endpoint is not supported
 */
public class ReportNotSupportedForDownloadException extends RuntimeException {
    public UUID reportId;

    public ReportNotSupportedForDownloadException(UUID reportId) {
        super();
        this.reportId = reportId;
    }
}
