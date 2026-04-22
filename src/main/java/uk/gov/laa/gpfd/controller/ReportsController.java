package uk.gov.laa.gpfd.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.laa.gpfd.api.ReportsApi;
import uk.gov.laa.gpfd.dao.ReportDao;
import uk.gov.laa.gpfd.dao.ReportTrackingDao;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;
import uk.gov.laa.gpfd.model.FileExtension;
import uk.gov.laa.gpfd.model.GetReportById200Response;
import uk.gov.laa.gpfd.model.ReportsGet200Response;
import uk.gov.laa.gpfd.services.ReportManagementService;
import uk.gov.laa.gpfd.services.ResponseBuilder;
import uk.gov.laa.gpfd.services.StreamingService;
import uk.gov.laa.gpfd.services.s3.FileDownloadService;
import uk.gov.laa.gpfd.services.s3.S3ClientWrapper;
import uk.gov.laa.gpfd.utils.SecurityUtils;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static uk.gov.laa.gpfd.model.FileExtension.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReportsController implements ReportsApi {

    private final ReportManagementService reportManagementService;
    private final StreamingService streamingService;
    private final FileDownloadService fileDownloadService;
    private final ReportDao reportDao;
    private final ReportTrackingDao reportTrackingDao;
    private final SecurityUtils securityUtils;
    private final ResponseBuilder responseBuilder;

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    @Override
    public ResponseEntity<GetReportById200Response> getReportById(UUID id) {
        log.debug("Returning a report response to user");
        return ResponseEntity.ok(reportManagementService.createReportResponse(id));
    }

    @Override
    public ResponseEntity<ReportsGet200Response> reportsGet() {
        log.info("Requesting report list from service");
        var reportListEntries = reportManagementService.fetchReportListEntries();

        var response = new ReportsGet200Response();
        reportListEntries.forEach(response::addReportListItem);

        log.info("Successfully pulled report list - total reports : {}", reportListEntries.size());
        return ResponseEntity.ok(response);
    }

    /**
     * Sends a report to the user in the form of a CSV data stream. If the user requests via a web browser this response then triggers the browser to download the file.
     *
     * @param requestedId - id of the requested report
     * @return CSV data stream or reports data
     *
     * <p>Example usage:
     *      <pre>
     *      GET /reports/f46b4d3d-c100-429a-bf9a-6c3305dbdbf1/csv
     *      </pre>
     */
    @Override
    public ResponseEntity<StreamingResponseBody> getCsvById(UUID requestedId) {
        log.info("Returning a CSV report for id {} to user", requestedId);

        // Enforce role-based access control for this report
        reportDao.verifyUserCanAccessReport(requestedId);

        // Validate that this report is actually a CSV report
        reportManagementService.validateReportFormat(requestedId, CSV);
        var rawStream = streamingService.stream(requestedId, CSV);

        return buildCsvAndExcelResponse(requestedId, rawStream);
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
     * GET /reports/b36f9bbb-1178-432c-8f99-8090e285f2d3/excel
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
        log.info("Returning an Excel report for id {} to user", id);

        // Enforce role-based access control for this report
        reportDao.verifyUserCanAccessReport(id);

        // Validate format before attempting to stream
        reportManagementService.validateReportFormat(id, XLSX);

        var response = streamingService.stream(id, XLSX);
        return buildCsvAndExcelResponse(id, response);
    }

    //    @Override
    @RequestMapping(
            method = {RequestMethod.GET},
            value = {"/reports/{id}/file2"},
            produces = {"application/octet-stream", "application/json"}
    )
    public ResponseEntity<StreamingResponseBody> getReportDownloadById2(@Parameter(name = "id",description = "The unique ID of the requested report.",required = true,in = ParameterIn.PATH) @PathVariable("id") UUID id) {
        log.info("Downloading report for id {}", id);

        // Validate that this report is S3STORAGE format
        reportManagementService.validateReportFormat(id, S3STORAGE);

        var s3Response = fileDownloadService.getFileStreamResponse(id);
        return buildS3Response(id, s3Response);
    }

    @Override
    public ResponseEntity<InputStreamResource> getReportDownloadById(UUID id) {
        log.info("Downloading report for id {}", id);

        // Validate that this report is S3STORAGE format
        reportManagementService.validateReportFormat(id, S3STORAGE);

        var response = fileDownloadService.getFileStreamResponse(id);
        reportTrackingDao.insertTrackingRow(id, securityUtils.extractUserId());

        var contentDisposition = ContentDisposition.attachment().filename(response.getFileName()).build();

        log.info("About to stream report with ID {} to user", id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(response.stream().response().contentLength())
                .body(new InputStreamResource(response.stream()));
    }

    private ResponseEntity<StreamingResponseBody> buildCsvAndExcelResponse(UUID reportId, StreamingResponseBody rawStream) {
        var report = reportDao.fetchReportById(reportId).orElseThrow(() -> new ReportIdNotFoundException(reportId));
        StreamingResponseBody trackedStream = createTrackedStream(reportId, rawStream);
        var filename = String.format("%s.%s", report.getName(), report.getOutputType().getExtension());
        return responseBuilder.buildResponse(trackedStream, filename, FileExtension.fromString(report.getOutputType().getExtension()));
    }

    private ResponseEntity<StreamingResponseBody> buildS3Response(UUID reportId, S3ClientWrapper.S3CsvDownload s3CsvDownload) {
        var report = reportDao.fetchReportById(reportId).orElseThrow(() -> new ReportIdNotFoundException(reportId));
        var s3Stream = s3CsvDownload.stream();
        StreamingResponseBody rawStream = outputStream -> {
            try (s3Stream) {
                s3Stream.transferTo(outputStream);
            }
        };

        StreamingResponseBody trackedStream = createTrackedStream(reportId, rawStream);
        return responseBuilder.buildResponse(trackedStream, s3CsvDownload.getFileName(), FileExtension.fromString(report.getOutputType().getExtension()), s3CsvDownload.stream().response().contentLength());
    }

    private @NonNull StreamingResponseBody createTrackedStream(UUID reportId, StreamingResponseBody rawStream) {
        return output -> {
            try {
                rawStream.writeTo(output);
                output.flush();
                reportTrackingDao.insertTrackingRow(reportId, securityUtils.extractUserId());
            } catch (IOException e) {
                //todo log
                throw new RuntimeException(e);
            } finally {
                log.info("Completed server-side response stream for report {}", reportId);
            }
        };
    }


}
