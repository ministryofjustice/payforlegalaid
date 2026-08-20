package uk.gov.laa.gpfd.services.sds;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import uk.gov.laa.gpfd.exception.ServiceUnavailableException;
import uk.gov.laa.gpfd.exception.sds.SdsFileNotFoundException;
import uk.gov.laa.gpfd.services.sds.client.ApiException;
import uk.gov.laa.gpfd.services.sds.client.api.FilesApi;
import uk.gov.laa.gpfd.services.sds.client.api.HealthApi;
import uk.gov.laa.gpfd.services.sds.client.model.SdsFileDownloadResponse;
import uk.gov.laa.gpfd.services.sds.client.model.SdsHealthResponse;
import uk.gov.laa.gpfd.services.sds.model.SdsFileDetails;
import uk.gov.laa.gpfd.services.sds.model.SdsFileVersionDetail;

import java.util.List;

import static uk.gov.laa.gpfd.utils.LogSanitiser.sanitise;

/**
 * Service class for interacting with the Secure Document Storage (SDS) API.
 * Uses generated OpenAPI client for type-safe API calls.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "gpfd.sds-enabled.enabled", havingValue = "true")
public class SdsService {

    private final FilesApi filesApi;
    private final HealthApi healthApi;

    /**
     * Retrieve a file from the SDS service by its file key.
     *
     * @param fileKey the unique identifier for the file in SDS
     * @return the file download response containing the file URL
     * @throws SdsFileNotFoundException if the file is not found in SDS
     * @throws ServiceUnavailableException if SDS is unavailable or returns a server error
     */
    public SdsFileDownloadResponse getFile(String fileKey) {
        log.debug("Retrieving file from SDS: {}", sanitise(fileKey));

        try {
            return filesApi.getFile(fileKey);
        } catch (ApiException e) {
            throw mapFileApiException(e, fileKey, "file", "retrieve file from SDS");
        }
    }

    /**
     * Retrieve file metadata from SDS by file key.
     *
     * @param fileKey the unique identifier for the file in SDS
     * @return the file details response containing all version metadata
     * @throws SdsFileNotFoundException if the file is not found in SDS
     * @throws ServiceUnavailableException if SDS is unavailable or returns a server error
     */
    public SdsFileDetails getFileDetails(String fileKey) {
        log.debug("Retrieving file details from SDS: {}", sanitise(fileKey));

        try {
            var response = filesApi.getFileDetails(fileKey);
            if (response == null) {
                return new SdsFileDetails(List.of());
            }

            var files = response.getVersionHistory().stream()
                    .map(file -> new SdsFileVersionDetail(file.getLastModified()))
                    .toList();
            return new SdsFileDetails(files);
        } catch (ApiException e) {
            throw mapFileApiException(e, fileKey, "file details", "retrieve file details from SDS");
        }
    }

    private RuntimeException mapFileApiException(
            ApiException exception,
            String fileKey,
            String targetDescription,
            String failureAction) {
        return switch (exception.getCode()) {
            case 404 -> {
                log.warn("{} not found in SDS: {}", targetDescription, sanitise(fileKey));
                yield new SdsFileNotFoundException("File not found: " + fileKey);
            }
            case 500, 502, 503, 504 -> {
                log.atError()
                        .setCause(exception)
                        .log("SDS service error retrieving {} '{}': HTTP {}",
                                targetDescription, sanitise(fileKey), exception.getCode());
                yield new ServiceUnavailableException("SDS service unavailable: " + exception.getMessage());
            }
            default -> {
                log.atError()
                        .setCause(exception)
                        .log("Unexpected error retrieving {} '{}': HTTP {}",
                                targetDescription, sanitise(fileKey), exception.getCode());
                yield new ServiceUnavailableException("Failed to " + failureAction + ": " + exception.getMessage());
            }
        };
    }

    /**
     * Get the health status of the SDS service.
     *
     * @return the health response from SDS
     * @throws ServiceUnavailableException if SDS is unavailable or returns an error
     */
    public SdsHealthResponse getHealth() {
        log.debug("Checking SDS service health");
        try {
            return healthApi.getHealth();
        } catch (ApiException e) {
            log.error("Error retrieving health status from SDS: HTTP {}", e.getCode(), e);
            throw new ServiceUnavailableException("Failed to retrieve SDS health status: " + e.getMessage());
        }
    }
}
