package uk.gov.laa.gpfd.exception;

import lombok.Getter;

import java.util.UUID;

/**
 * Exception class to indicate that the file cannot be downloaded from this endpoint
 */
@Getter
public class InvalidDownloadFormatException extends RuntimeException {
    final String fileName;
    final UUID reportId;

    public InvalidDownloadFormatException(String fileName, UUID reportId) {
        super();
        this.fileName = fileName;
        this.reportId = reportId;
    }
}
