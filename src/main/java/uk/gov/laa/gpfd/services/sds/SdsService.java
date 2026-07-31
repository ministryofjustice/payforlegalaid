package uk.gov.laa.gpfd.services.sds;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.laa.gpfd.exception.sds.FileConflictException;
import uk.gov.laa.gpfd.exception.sds.FileLengthRequiredException;
import uk.gov.laa.gpfd.exception.sds.SdsFileNotFoundException;
import uk.gov.laa.gpfd.exception.sds.VirusDetectedException;
import uk.gov.laa.gpfd.exception.sds.VirusScanException;
import uk.gov.laa.gpfd.model.sds.DocumentDownloadResponse;
import uk.gov.laa.gpfd.model.sds.DocumentUploadResponse;
import uk.gov.laa.gpfd.model.sds.SdsHealthResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.*;

/** Service class for interacting with the Secure Document Storage (SDS) API. */
@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "gpfd.sds-enabled.enabled", havingValue = "true")
public class SdsService {

    private static final String SAVE_FILE_ENDPOINT = "/save_file";
//    private static final String SAVE_OR_UPDATE_FILE_ENDPOINT = "/save_or_update_file";
    private static final String GET_FILE_ENDPOINT = "/get_file";
//    private static final String DELETE_FILES_ENDPOINT = "/delete_files";
    private static final String HEALTH_ENDPOINT = "/health";
//
    private static final String FILE_KEY_PARAM = "file_key";
    private static final String FILE_KEYS_PARAM = "file_keys";
    private static final String BUCKET_NAME_FIELD = "bucketName";
    private static final String FOLDER_FIELD = "folder";
    private static final String PATH_SEPARATOR = "/";

    private final RestClient sdsRestClient;
    private final SdsTokenService sdsTokenService;

    @Value("${app.sds-api.bucket-name}")
    private String bucketName;

    /**
     * Save a file in the SDS service.
     *
     * @param applicationId the application ID used as folder name
     * @param file the file to be saved
     * @return the file URL response from SDS
     */
    public DocumentUploadResponse saveFile(UUID applicationId, MultipartFile file) {
        String folderName = applicationId.toString();
        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put(BUCKET_NAME_FIELD, bucketName);
        bodyMap.put(FOLDER_FIELD, folderName);

        MultipartBodyBuilder builder = buildMultipartBody(file, bodyMap);

        DocumentUploadResponse response =
                handleUploadErrors(
                        sdsRestClient
                                .post()
                                .uri(SAVE_FILE_ENDPOINT)
                                .headers(headers -> headers.setBearerAuth(sdsTokenService.getSdsAccessToken()))
                                .contentType(MULTIPART_FORM_DATA)
                                .body(builder.build())
                                .retrieve()
                                .onStatus(
                                        status -> status.isSameCodeAs(CONFLICT),
                                        (request, response1) -> {
                                            throw new FileConflictException("File already exists in SDS");
                                        }))
                        .body(DocumentUploadResponse.class);

        return response;
    }

//    /**
//     * Save or update a file in the SDS service.
//     *
//     * @param applicationId the application ID used as folder name
//     * @param file the file to be saved or updated
//     * @return the file URL response from SDS
//     */
//    public DocumentUpdateResponse saveOrUpdateFile(UUID applicationId, MultipartFile file) {
//        Map<String, String> bodyMap =
//                Map.of(BUCKET_NAME_FIELD, bucketName, FOLDER_FIELD, applicationId.toString());
//        MultipartBodyBuilder builder = buildMultipartBody(file, bodyMap);
//
//        return handleUploadErrors(
//                sdsRestClient
//                        .put()
//                        .uri(SAVE_OR_UPDATE_FILE_ENDPOINT)
//                        .headers(headers -> headers.setBearerAuth(tokenService.getSdsAccessToken()))
//                        .contentType(MULTIPART_FORM_DATA)
//                        .body(builder.build())
//                        .retrieve())
//                .body(DocumentUpdateResponse.class);
//    }

    /**
     * Get the file URL from the SDS service.
     *
     * @param applicationId the application ID
     * @param documentId the document ID
     * @return the file URL response from SDS
     */
    public DocumentDownloadResponse getFile(UUID applicationId, String documentId) {
        String fileKey = buildFileKey(applicationId, documentId);

        DocumentDownloadResponse response =
                sdsRestClient
                        .get()
                        .uri(
                                uriBuilder ->
                                        uriBuilder.path(GET_FILE_ENDPOINT).queryParam(FILE_KEY_PARAM, fileKey).build())
                        .headers(headers -> headers.setBearerAuth(sdsTokenService.getSdsAccessToken()))
                        .accept(APPLICATION_JSON)
                        .retrieve()
                        .onStatus(
                                status -> status.value() == NOT_FOUND.value(),
                                (request, response1) -> {
                                    throw new SdsFileNotFoundException("File not found");
                                })
                        .body(DocumentDownloadResponse.class);

        return response;
    }

//    /**
//     * Delete files from the SDS service.
//     *
//     * @param applicationId the application ID
//     * @param fileIds the list of file IDs to be deleted
//     * @return per-file deletion results
//     */
//    public DocumentDeleteResponse deleteFiles(UUID applicationId, List<String> fileIds) {
//        List<String> fileKeys =
//                fileIds.stream().map(fileId -> buildFileKey(applicationId, fileId)).toList();
//
//        Map<String, Integer> sdsResults =
//                sdsRestClient
//                        .delete()
//                        .uri(
//                                uriBuilder -> {
//                                    UriBuilder deleteFilesUri = uriBuilder.path(DELETE_FILES_ENDPOINT);
//                                    fileKeys.forEach(key -> deleteFilesUri.queryParam(FILE_KEYS_PARAM, key));
//                                    return deleteFilesUri.build();
//                                })
//                        .headers(headers -> headers.setBearerAuth(tokenService.getSdsAccessToken()))
//                        .retrieve()
//                        .body(
//                                new org.springframework.core.ParameterizedTypeReference<Map<String, Integer>>() {});
//
//        List<DocumentDeleteResult> results =
//                sdsResults == null
//                        ? List.of()
//                        : fileIds.stream()
//                        .map(
//                                fileId -> {
//                                    var result = new DocumentDeleteResult();
//                                    result.setDocumentId(fileId);
//                                    result.setStatus(sdsResults.get(buildFileKey(applicationId, fileId)));
//                                    return result;
//                                })
//                        .toList();
//
//        var response = new DocumentDeleteResponse();
//        response.setResults(results);
//        return response;
//    }

    /**
     * Get the health status of the SDS service.
     *
     * @return the health response from SDS
     */
    public SdsHealthResponse getHealth() {
        SdsHealthResponse response =
                sdsRestClient
                        .get()
                        .uri(HEALTH_ENDPOINT)
                        .headers(headers -> headers.setBearerAuth(sdsTokenService.getSdsAccessToken()))
                        .accept(APPLICATION_JSON)
                        .retrieve()
                        .body(SdsHealthResponse.class);

        return response;
    }

    /**
     * Build file key from application ID and document ID.
     *
     * @param applicationId the application ID
     * @param documentId the document ID
     * @return the file key
     */
    private String buildFileKey(UUID applicationId, String documentId) {
        return applicationId.toString() + PATH_SEPARATOR + documentId;
    }

    private RestClient.ResponseSpec handleUploadErrors(RestClient.ResponseSpec spec) {
        return spec.onStatus(
                        status -> status.isSameCodeAs(LENGTH_REQUIRED),
                        (req, res) -> {
                            throw new FileLengthRequiredException("File content length is required");
                        })
                .onStatus(
                        status -> status.isSameCodeAs(BAD_REQUEST),
                        (req, res) -> {
                            throw new VirusDetectedException("Virus detected in uploaded file");
                        })
                .onStatus(
                        status -> status.isSameCodeAs(INTERNAL_SERVER_ERROR),
                        (req, res) -> {
                            throw new VirusScanException("Virus scan gave a non-standard result");
                        });
    }

    /**
     * Build multipart body with file and JSON body fields.
     *
     * @param file the file to upload
     * @param bodyFields the JSON body fields
     * @return the multipart body builder
     */
    private MultipartBodyBuilder buildMultipartBody(
            MultipartFile file, Map<String, String> bodyFields) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", file.getResource()).contentType(APPLICATION_OCTET_STREAM);

        // Convert map to JSON string manually (simple format)
        StringBuilder jsonBody = new StringBuilder("{");
        bodyFields.forEach(
                (key, value) -> {
                    if (jsonBody.length() > 1) {
                        jsonBody.append(",");
                    }
                    jsonBody.append(String.format("\"%s\":\"%s\"", key, value));
                });
        jsonBody.append("}");

        builder.part("body", jsonBody.toString());
        return builder;
    }
}