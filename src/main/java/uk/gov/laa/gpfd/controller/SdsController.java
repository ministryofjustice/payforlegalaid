package uk.gov.laa.gpfd.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.laa.gpfd.services.sds.SdsService;
import uk.gov.laa.gpfd.services.sds.client.model.SdsFileDownloadResponse;
import uk.gov.laa.gpfd.services.sds.client.model.SdsHealthResponse;

/**
 * REST controller for Secure Document Storage (SDS) operations.
 * Provides endpoints to interact with the SDS API.
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
     * Retrieve a file from the SDS service by its file key.
     *
     * <p>Example usage:
     * <pre>
     *     GET /sds/files/CCMS%20AP%20Debtors.csv
     * </pre>
     *
     * @param fileKey the name of the file to retrieve from SDS
     * @return the download response containing the file URL
     */
    @GetMapping("/files/{fileKey}")
    public ResponseEntity<SdsFileDownloadResponse> getFile(
            @PathVariable String fileKey) {
        log.info("Retrieving file '{}' from SDS", fileKey);
        SdsFileDownloadResponse response = sdsService.getFile(fileKey);
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
     * @return the health status response from SDS
     */
    @GetMapping("/health")
    public ResponseEntity<SdsHealthResponse> health() {
        log.debug("Checking SDS service health");
        SdsHealthResponse response = sdsService.getHealth();
        return ResponseEntity.ok(response);
    }
}

