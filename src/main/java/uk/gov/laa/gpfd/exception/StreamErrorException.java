package uk.gov.laa.gpfd.exception;

import lombok.Getter;

import java.util.UUID;

@Getter
public class StreamErrorException extends RuntimeException {

    private final UUID reportId;

    public StreamErrorException(String message, UUID reportId) {
        super(message);
        this.reportId = reportId;
    }
}
