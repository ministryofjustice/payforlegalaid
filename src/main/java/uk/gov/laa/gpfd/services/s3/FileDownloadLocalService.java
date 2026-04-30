package uk.gov.laa.gpfd.services.s3;

import uk.gov.laa.gpfd.exception.OperationNotSupportedException;
import uk.gov.laa.gpfd.services.s3.S3ClientWrapper.S3CsvDownload;

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
    public S3CsvDownload getFileStreamResponse(UUID id) {
        throw new OperationNotSupportedException("/reports/" + id + "/file");
    }
}
