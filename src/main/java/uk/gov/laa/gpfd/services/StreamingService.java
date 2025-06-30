package uk.gov.laa.gpfd.services;

import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.laa.gpfd.model.FileExtension;
import uk.gov.laa.gpfd.exception.ReportOutputTypeNotFoundException;
import uk.gov.laa.gpfd.services.stream.DataStream;

import java.util.Map;
import java.util.UUID;

/**
 * A service that handles streaming of reports in various file formats.
 * <p>
 * This service acts as a facade that delegates the actual streaming work to format-specific
 * strategies registered in the {link uk.gov.laa.gpfd.config.AppConfig#streamStrategyFactory}. It provides a unified interface
 * for streaming reports while supporting easy extension to additional formats.
 * </p>
 *
 *
 * @see DataStream
 */
public interface StreamingService {

    /**
     * Streams a report in the requested format.
     * <p>
     * The method locates the appropriate streaming strategy for the specified format and
     * executes it to generate the response. The response will contain the report data
     * stream along with proper content type and disposition headers.
     * </p>
     *
     * @param id The unique identifier of the report to stream
     * @param format The desired output format for the report
     * @return A {@link ResponseEntity} containing the report data as a {@link StreamingResponseBody}
     * @throws ReportOutputTypeNotFoundException if no strategy is available for the requested format
     * @throws IllegalStateException if the streaming operation fails
     *
     * @see FileExtension
     * @see StreamingResponseBody
     */
    ResponseEntity<StreamingResponseBody> stream(UUID id, FileExtension format);

    record DefaultStreamingService(Map<FileExtension, DataStream> strategies) implements StreamingService {

        @Override
        public ResponseEntity<StreamingResponseBody> stream(UUID id, FileExtension format) {
            return strategies.get(format).stream(id);
        }
    }
}