package uk.gov.laa.gpfd.exception;

import lombok.Getter;

import java.util.UUID;

/**
 * Base sealed exception class for /file endpoint related errors.
 */
public abstract sealed class FileDownloadException extends RuntimeException {

    public FileDownloadException(String message) {
        super(message);
    }

    /**
     * Exception class to indicate that the file cannot be downloaded from this endpoint
     */
    @Getter
    public static final class InvalidDownloadFormatException extends FileDownloadException {
        final String fileName;
        final UUID reportId;

        public InvalidDownloadFormatException(String fileName, UUID reportId) {
            super("Unable to download file for report with ID: " + reportId);
            this.fileName = fileName;
            this.reportId = reportId;
        }
    }


    /**
     * Exception class to indicate that the report cannot be downloaded via this endpoint
     */
    @Getter
    public static class ReportNotSupportedForDownloadException extends RuntimeException {
        final UUID reportId;

        public ReportNotSupportedForDownloadException(UUID reportId) {
            super("Report " + reportId +" is not valid for file retrieval.");
            this.reportId = reportId;
        }
    }


}
