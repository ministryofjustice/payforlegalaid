package uk.gov.laa.gpfd.services.s3;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public class FileDownloadFromS3Service implements FileDownloadService {

    private final S3ClientWrapper s3ClientWrapper;

    public FileDownloadFromS3Service(S3ClientWrapper s3ClientWrapper) {
        this.s3ClientWrapper = s3ClientWrapper;
    }

    @Override
    public ResponseEntity<InputStreamResource> getFileStreamResponse(UUID id) {
        // TODO may need to inject as bean to make this testable
        var fileResolver = new ReportFileNameResolver();

        var fileName = fileResolver.getFileNameFromId(id);

        var fileStream = s3ClientWrapper.getResultCsv(fileName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(fileStream.response().contentLength())
                .body(new InputStreamResource(fileStream));
    }
}
