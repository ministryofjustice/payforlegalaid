package uk.gov.laa.gpfd.services.s3;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

/**
 Interface for how we handle download requests.
 In effect, it is just a way to split out behaviour between systems with S3 {@link FileDownloadFromS3Service} and
 without {@link FileDownloadLocalService}.
 */
public interface FileDownloadService {
    /**
     * Return user a file stream with their requested download
     *
     * @param id - UUID of the report
     * @return an {@link ResponseEntity} with status OK and an {@link InputStreamResource} containing the CSV file inside.
     */
    ResponseEntity<InputStreamResource> getFileStreamResponse(UUID id);
}
