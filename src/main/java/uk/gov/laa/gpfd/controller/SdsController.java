package uk.gov.laa.gpfd.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.laa.gpfd.model.sds.DocumentDownloadResponse;
import uk.gov.laa.gpfd.model.sds.DocumentUploadResponse;
import uk.gov.laa.gpfd.model.sds.SdsHealthResponse;
import uk.gov.laa.gpfd.services.sds.SdsService;

import java.util.UUID;

/**
 * REST controller for Secure Document Storage (SDS) operations.
 * Only active when {@code gpfd.sds-enabled.enabled=true}.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/sds")
@ConditionalOnProperty(name = "gpfd.sds-enabled.enabled", havingValue = "true")
public class SdsController {

    private final SdsService sdsService;

    /**
     * Upload a file to the SDS service for a given application.
     *
     * <p>Example usage:
     * <pre>
     *     POST /sds/files/{applicationId}
     *     Content-Type: multipart/form-data
     * </pre>
     *
     * @param applicationId the UUID of the application to associate the file with
     * @param file          the file to upload
     * @return the upload response containing detail, success, and checksum fields
     */
    @PostMapping(value = "/files/{applicationId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentUploadResponse> saveFile(
            @PathVariable UUID applicationId,
            @RequestParam("file") MultipartFile file) {
        log.info("Uploading file '{}' for applicationId {}", file.getOriginalFilename(), applicationId);
        DocumentUploadResponse response = sdsService.saveFile(applicationId, file);
        log.info("File upload successful for applicationId {}, success={}", applicationId, response.getSuccess());
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve a file URL from the SDS service.
     *
     * <p>Example usage:
     * <pre>
     *     GET /sds/files/{applicationId}/{documentId}
     * </pre>
     *
     * @param applicationId the UUID of the application
     * @param documentId    the document identifier within the application folder
     * @return the download response containing the file URL and checksum
     */
    @GetMapping("/files/{applicationId}/{documentId}")
    public ResponseEntity<DocumentDownloadResponse> getFile(
            @PathVariable UUID applicationId,
            @PathVariable String documentId) {
        log.info("Retrieving file '{}' for applicationId {}", documentId, applicationId);
        DocumentDownloadResponse response = sdsService.getFile(applicationId, documentId);
        return ResponseEntity.ok(response);
    }

    /**
     * Check the health status of the SDS service.
     *
     * <p>Example usage:
     * <pre>
     *     GET /sds/health
     * </pre>
     *
     * @return the SDS health response containing status and detail fields
     */
    @GetMapping("/health")
    public ResponseEntity<SdsHealthResponse> health() {
        log.debug("Checking SDS service health");
        SdsHealthResponse response = sdsService.getHealth();
        return ResponseEntity.ok(response);
    }
}

