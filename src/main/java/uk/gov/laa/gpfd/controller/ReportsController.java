package uk.gov.laa.gpfd.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.laa.gpfd.api.ExcelApi;
import uk.gov.laa.gpfd.api.ReportsApi;
import uk.gov.laa.gpfd.model.GetReportById200Response;
import uk.gov.laa.gpfd.model.ReportsGet200Response;
import uk.gov.laa.gpfd.services.ReportService;
import uk.gov.laa.gpfd.services.ReportsTrackingService;
import uk.gov.laa.gpfd.services.StreamingService;
import uk.gov.laa.gpfd.services.ReportManagementService;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReportsController implements ReportsApi, ExcelApi {

    private final ReportsTrackingService reportsTrackingService;
    private final ReportService reportService;
    private final ReportManagementService reportManagementService;
    private final StreamingService streamingService;

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

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

        //TODO TAKE OUT
        System.out.println("PRINCIPLE: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        System.out.println("CREDENTIALS: " + SecurityContextHolder.getContext().getAuthentication().getCredentials());
        System.out.println("AUTHORITIES: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());


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
    public ResponseEntity<StreamingResponseBody> getCSV(@PathVariable(value = "id") UUID requestedId) {
        log.debug("Returning a CSV response to user");
        reportsTrackingService.saveReportsTracking(requestedId);
        return reportService.createCSVResponse(requestedId);
    }

    /**
     * Handles HTTP GET requests to stream an Excel file to the client. This endpoint generates an Excel file
     * based on the provided unique identifier (UUID) and streams it directly to the client as a downloadable attachment.
     *
     * <p>The response is streamed with the content type set to
     * {@code application/vnd.openxmlformats-officedocument.spreadsheetml.sheet}, which is the standard MIME type
     * for Excel files (.xlsx). This ensures compatibility with Excel and other spreadsheet software.
     *
     * <p>Example usage:
     * <pre>
     * GET /excel/b36f9bbb-1178-432c-8f99-8090e285f2d3
     * </pre>
     *
     * <p>If the Excel file is successfully generated and streamed, the response will include:
     * <ul>
     *   <li>A {@code Content-Disposition} header with the filename in the format {@code <report_name>.xlsx}.</li>
     *   <li>A {@code Content-Type} header set to {@code application/vnd.openxmlformats-officedocument.spreadsheetml.sheet}.</li>
     * </ul>
     *
     * <p>If an error occurs during the process, the method will log the error and throw an appropriate exception.
     *
     * @param id The unique identifier (UUID) of the report to be generated and streamed as an Excel file.
     * @return A {@link ResponseEntity} containing a {@link StreamingResponseBody} for the Excel file.
     */
    @Override
    public ResponseEntity<StreamingResponseBody> getExcelById(UUID id) {
        reportsTrackingService.saveReportsTracking(id);
        return streamingService.streamExcel(id);
    }
}
