package uk.gov.laa.gpfd.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.laa.gpfd.api.ReportsApi;
import uk.gov.laa.gpfd.model.GetReportById200Response;
import uk.gov.laa.gpfd.model.ReportsGet200Response;
import uk.gov.laa.gpfd.services.MappingTableService;
import uk.gov.laa.gpfd.services.ReportService;
import uk.gov.laa.gpfd.services.ReportTrackingTableService;
import uk.gov.laa.gpfd.services.StreamingService;
import uk.gov.laa.gpfd.services.ReportManagementService;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReportsController implements ReportsApi {

    private final ReportTrackingTableService reportTrackingTableService;
    private final ReportService reportService;
    private final ReportManagementService reportManagementService;
    private final StreamingService streamingService;

    @Override
    public ResponseEntity<GetReportById200Response> getReportById(UUID id) {
        var response = reportService.createReportResponse(id);

        log.debug("Returning a report response to user");
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ReportsGet200Response> reportsGet() {
        var reportListEntries = reportManagementService.fetchReportListEntries();

        var response = new ReportsGet200Response() {{
            reportListEntries.forEach(this::addReportListItem);
        }};

        log.debug("Returning a reportListResponse to user");
        return ResponseEntity.ok(response);
    }

    /**
     * Sends a report to the user in the form of a CSV data stream. If the user requests via a web browser this response then triggers the browser to download the file.
     *
     * @param requestedId - id of the requested report
     * @return CSV data stream or reports data
     */
    @RequestMapping(value = "/csv/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> getCSV(@PathVariable(value = "id") UUID requestedId,
                                                        @RegisteredOAuth2AuthorizedClient("graph") OAuth2AuthorizedClient graphClient) {
        log.debug("Returning a CSV response to user");
        return reportService.createCSVResponse(requestedId, graphClient);
    }

    /**
     * Handles HTTP GET requests to stream an Excel file to the client. This endpoint is designed to
     * asynchronously generate and stream an Excel file based on the provided unique identifier.
     *
     * <p>The response is streamed directly to the client with the content type set to
     * {@link MediaType#APPLICATION_OCTET_STREAM_VALUE}, ensuring the file is downloaded as an attachment.
     * The method uses a {@link DeferredResult} to handle the asynchronous nature of the operation,
     * allowing the server to process other requests while the Excel file is being generated and streamed.
     *
     * <p>Example usage:
     * <pre>
     * GET /excel/b36f9bbb-1178-432c-8f99-8090e285f2d3
     * </pre>
     *
     * @param response    the {@link HttpServletResponse} to which the Excel file will be streamed
     * @param requestedId the unique identifier (UUID) of the report to be generated and streamed
     * @return a {@link DeferredResult} representing the asynchronous result of the streaming operation
     */
    @SuppressWarnings("java:S6856") // Expose later endpoint
    public DeferredResult<StreamingResponseBody> getExcel(HttpServletResponse response, @PathVariable(value = "id") UUID requestedId) {
        log.info("Request received for streaming Excel file with ID: {}", requestedId);
        return streamingService.streamExcel(response, requestedId);
    }

}
