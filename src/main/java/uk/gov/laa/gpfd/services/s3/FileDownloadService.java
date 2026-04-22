package uk.gov.laa.gpfd.services.s3;

import software.amazon.awssdk.core.ResponseInputStream;
import uk.gov.laa.gpfd.services.s3.S3ClientWrapper.S3CsvDownload;

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
     * @return an {@link ResponseInputStream} containing the CSV file inside, wrapped in a {@link S3CsvDownload} wrapper.
     */
    S3CsvDownload getFileStreamResponse(UUID id);
}
