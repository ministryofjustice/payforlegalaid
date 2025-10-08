package uk.gov.laa.gpfd.services.s3;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import uk.gov.laa.gpfd.config.S3Config;
import uk.gov.laa.gpfd.exception.InvalidDownloadFormatException;
import uk.gov.laa.gpfd.exception.OperationNotSupportedException;

import java.util.List;
import java.util.UUID;

/**
 Implements how to download files when we have S3 access.
 */
@Slf4j
public class FileDownloadFromS3Service implements FileDownloadService {

    private final S3ClientWrapper s3ClientWrapper;
    private final ReportFileNameResolver fileNameResolver;
    private final S3Config s3Config;

    public FileDownloadFromS3Service(S3ClientWrapper s3ClientWrapper, ReportFileNameResolver fileNameResolver, S3Config s3Config) {
        this.s3ClientWrapper = s3ClientWrapper;
        this.fileNameResolver = fileNameResolver;
        this.s3Config = s3Config;
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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var principal = (DefaultOidcUser) authentication.getPrincipal();
        var claims = principal.getIdToken().getClaims();
        var groups = (List<String>) claims.get("groups");

        if (id == UUID.fromString("523f38f0-2179-4824-b885-3a38c5e149e8") && !groups.contains(s3Config.getRep000GroupId())){
            //TODO not placeholder
            throw new OperationNotSupportedException("rep000");
        }
        if (id == UUID.fromString("cc55e276-97b0-4dd8-a919-26d4aa373266") && !groups.contains(s3Config.getSubmissionReconciliationGroupId())){
            throw new OperationNotSupportedException("rep012");
        }
        if (id == UUID.fromString("aca2120c-8f82-45a8-a682-8dedfb7997a7") && !groups.contains(s3Config.getSubmissionReconciliationGroupId())){
            throw new OperationNotSupportedException("rep013");
        }

        var fileName = fileNameResolver.getFileNameFromId(id);

        if (!fileName.endsWith(".csv")) {
            throw new InvalidDownloadFormatException(fileName, id);
        }

        var fileStream = s3ClientWrapper.getResultCsv(fileName);
        var contentDisposition = ContentDisposition.attachment().filename(fileName).build();

        log.info("About to stream report with ID {} to user", id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(fileStream.response().contentLength())
                .body(new InputStreamResource(fileStream));
    }
}
