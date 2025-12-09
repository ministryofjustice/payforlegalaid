package uk.gov.laa.gpfd.exception;

import lombok.Getter;

import java.util.UUID;

/**
 * Exception class to indicate that the report cannot be downloaded via this endpoint
 */
@Getter
public class ReportNotSupportedForDownloadException extends RuntimeException {
    final UUID reportId;

    public ReportNotSupportedForDownloadException(UUID reportId) {
        super("Report " + reportId +" is not valid for file retrieval.");
        this.reportId = reportId;
    }
}
