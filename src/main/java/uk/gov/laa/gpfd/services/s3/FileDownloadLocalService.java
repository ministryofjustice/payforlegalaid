package uk.gov.laa.gpfd.services.s3;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import uk.gov.laa.gpfd.exception.OperationNotSupportedException;

import java.util.UUID;

public class FileDownloadLocalService implements FileDownloadService {
    @Override
    public ResponseEntity<InputStreamResource> getFileStreamResponse(UUID id) {
        throw new OperationNotSupportedException("/reports/" + id + "/file");
    }
}
