package uk.gov.laa.gpfd.exception;

import java.util.UUID;

/**
 * Exception class to indicate that the endpoint is not supported
 */
public class InvalidDownloadFormatException extends RuntimeException {
    public String fileName;
    public UUID reportId;

    public InvalidDownloadFormatException(String fileName, UUID reportId) {
        super();
        this.fileName = fileName;
        this.reportId = reportId;
    }
}
