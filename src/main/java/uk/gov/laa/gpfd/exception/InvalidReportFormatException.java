package uk.gov.laa.gpfd.exception;

import lombok.Getter;

import java.util.UUID;

/**
 * Exception class to indicate that the report format is not valid for selected path
 */
@Getter
public class InvalidReportFormatException extends RuntimeException {
    private final UUID reportId;
    private final String requestedFormat;
    private final String actualFormat;

    public InvalidReportFormatException(UUID reportId, String requestedFormat, String actualFormat) {
        super(String.format("Report %s is not valid for %s retrieval. This report is in %s format.",
                reportId, requestedFormat, actualFormat));
        this.reportId = reportId;
        this.requestedFormat = requestedFormat;
        this.actualFormat = actualFormat;
    }
}