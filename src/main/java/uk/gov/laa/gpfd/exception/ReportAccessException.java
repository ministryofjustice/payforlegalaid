package uk.gov.laa.gpfd.exception;

import lombok.Getter;

import java.util.UUID;

/**
 * Exception class to indicate that the endpoint is not supported
 */
@Getter
public class ReportAccessException extends RuntimeException {

    private final UUID reportId;

    public ReportAccessException(UUID reportId) {
        super();
        this.reportId = reportId;
    }
}
