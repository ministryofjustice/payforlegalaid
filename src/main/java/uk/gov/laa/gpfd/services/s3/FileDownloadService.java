package uk.gov.laa.gpfd.services.s3;

import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.UUID;

public interface FileDownloadService {
    ResponseEntity<StreamingResponseBody> getFileStreamResponse(UUID id);
}
