package uk.gov.laa.gpfd.services.s3;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import uk.gov.laa.gpfd.dao.ReportDao;
import uk.gov.laa.gpfd.exception.FileDownloadException.S3BucketHasNoCopiesOfReportException;
import uk.gov.laa.gpfd.utils.SecurityUtils;

import java.util.UUID;

/**
 * Implements how to download files when we have S3 access.
 */
@Slf4j
public class FileDownloadFromS3Service implements FileDownloadService {

    private final S3ClientWrapper s3ClientWrapper;
    private final ReportFileNameResolver fileNameResolver;
    private final ReportDao reportDao;

    public FileDownloadFromS3Service(S3ClientWrapper s3ClientWrapper, ReportFileNameResolver fileNameResolver, ReportDao reportDao) {
        this.s3ClientWrapper = s3ClientWrapper;
        this.fileNameResolver = fileNameResolver;
        this.reportDao = reportDao;
    }

    /**
     * Fetches a file stream from S3 and passes it to the user's browser
     *
     * @param id - UUID of the report
     * @return an {@link ResponseEntity} with status OK and an {@link InputStreamResource} containing the CSV file inside.
     */
    @SneakyThrows
    @Override
    public ResponseEntity<InputStreamResource> getFileStreamResponse(UUID id) {

        reportDao.authorizeReportAccess(id);

        var s3Prefix = fileNameResolver.getS3PrefixFromId(id);

        var fileStreamOptional = s3ClientWrapper.getResultCsv(s3Prefix);
        var fileStream = fileStreamOptional
                .orElseThrow(() -> new S3BucketHasNoCopiesOfReportException(id, s3Prefix));

            var contentDisposition = ContentDisposition.attachment().filename(fileStream.getFileName()).build();

            log.info("About to stream report with ID {} to user", id);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(fileStream.stream().response().contentLength())
                    .body(new InputStreamResource(fileStream.stream()));

    }

}
