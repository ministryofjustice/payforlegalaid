package uk.gov.laa.gpfd.services.s3;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import uk.gov.laa.gpfd.exception.OperationNotSupportedException;

import java.util.UUID;

/**
 Implements how to download files when we have no S3 access.
 */
public class FileDownloadLocalService implements FileDownloadService {
    /**
     * This operation is not supported locally, and all this function does is throw an error to that affect
     *
     * @param id - UUID of the report
     * @return will have thrown an {@link OperationNotSupportedException} before it can return
     */
    @Override
    public ResponseEntity<InputStreamResource> getFileStreamResponse(UUID id) {
        throw new OperationNotSupportedException("/reports/" + id + "/file");
    }
}
