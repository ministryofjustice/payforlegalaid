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
     *     POST /sds/files
     *     Content-Type: multipart/form-data
     * </pre>
     *
     * @param file the file to upload
     * @return the upload response containing detail, success, and checksum fields
     */
    @PostMapping(value = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentUploadResponse> saveFile(
            @RequestParam("file") MultipartFile file) {
        log.info("Uploading file '{}'", file.getOriginalFilename());
        DocumentUploadResponse response = sdsService.saveFile(file);
        log.info("File upload successful for '{}', success={}", file.getOriginalFilename(), response.getSuccess());
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve a file from the SDS service by its file key.
     *
     * @param fileKey - the unique identifier for the file in SDS
     *
     * <p>Example usage:
     * <pre>
     *     GET /sds/files/{fileKey}
     * </pre>
     *
     * @return the download response containing file content and metadata
     */
    @GetMapping("/files/{fileKey}")
    public ResponseEntity<DocumentDownloadResponse> getFile(
            @PathVariable String fileKey) {
        log.info("Retrieving file '{}'", fileKey);
        DocumentDownloadResponse response = sdsService.getFile(fileKey);
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

