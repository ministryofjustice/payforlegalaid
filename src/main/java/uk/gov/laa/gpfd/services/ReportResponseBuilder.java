package uk.gov.laa.gpfd.services;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.laa.gpfd.model.FileExtension;

import java.util.Optional;

import static org.apache.poi.xdgf.util.Util.sanitizeFilename;
import static uk.gov.laa.gpfd.services.stream.DataStream.APPLICATION_EXCEL;

@Component
public class ReportResponseBuilder {

    public ResponseEntity<StreamingResponseBody> buildResponse(StreamingResponseBody trackedStream,
                                                               String filename, FileExtension fileExtension) {
        return buildResponseInternal(trackedStream, filename, fileExtension, Optional.empty());
    }

    public ResponseEntity<StreamingResponseBody> buildResponse(StreamingResponseBody trackedStream,
                                                               String filename, FileExtension fileExtension,
                                                               Long contentLength) {
        return buildResponseInternal(trackedStream, filename, fileExtension, Optional.of(contentLength));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private ResponseEntity<StreamingResponseBody> buildResponseInternal(StreamingResponseBody trackedStream,
                                                                        String filename, FileExtension fileExtension,
                                                                        Optional<Long> contentLength) {
        var builder = ResponseEntity.ok()
                .header("Content-Disposition", createContentDisposition(filename))
                .contentType(getContentType(fileExtension));

        contentLength.ifPresent(builder::contentLength);

        return builder.body(trackedStream);

    }

    private String createContentDisposition(String filename) {
        return "attachment; filename=\"%s\"".formatted(sanitizeFilename(filename));
    }

    private MediaType getContentType(FileExtension fileExtension) {
        return switch (fileExtension) {
            case XLSX -> MediaType.valueOf(APPLICATION_EXCEL);
            case CSV, S3STORAGE -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }

}
