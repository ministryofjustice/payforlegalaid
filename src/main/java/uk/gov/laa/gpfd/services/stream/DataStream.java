package uk.gov.laa.gpfd.services.stream;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.laa.gpfd.model.FileExtension;

import java.util.UUID;

/**
 * Defines the contract for streaming strategies that generate reports in different formats.
 * <p>
 * Implementations of this interface handle the format-specific logic for streaming report data
 * to clients.
 */
public interface DataStream {
    /**
     * MIME type for Excel files (XLSX format).
     */
    String APPLICATION_EXCEL = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    /**
     * Streams a report as an HTTP response in the strategy's format.
     *
     * @param uuid the unique identifier of the report to stream
     * @return a {@link ResponseEntity} containing the streaming response
     * @throws IllegalStateException if there's an error generating the stream
     */
    ResponseEntity<StreamingResponseBody> stream(UUID uuid);

    /**
     * Gets the file format supported by this strategy.
     *
     * @return the {@link FileExtension} this strategy handles
     */
    FileExtension getFormat();

    /**
     * Gets the media type (content type) for this strategy's format.
     * <p>
     * The default implementation returns:
     * </p>
     * <ul>
     *   <li>{@code application/vnd.openxmlformats-officedocument.spreadsheetml.sheet} for Excel</li>
     *   <li>{@code application/octet-stream} for CSV</li>
     * </ul>
     *
     * @return the appropriate {@link MediaType} for this format
     */
    default MediaType getContentType() {
        return switch(getFormat()) {
            case XLSX -> MediaType.valueOf(APPLICATION_EXCEL);
            case CSV, S3STORAGE -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }
}