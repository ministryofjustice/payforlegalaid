package uk.gov.laa.gpfd.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.laa.gpfd.api.ReportsApi;
import uk.gov.laa.gpfd.model.GetReportById200Response;
import uk.gov.laa.gpfd.model.ReportsGet200Response;
import uk.gov.laa.gpfd.services.MappingTableService;
import uk.gov.laa.gpfd.services.ReportService;
import uk.gov.laa.gpfd.services.ReportTrackingTableService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReportsController implements ReportsApi {

    private final ReportTrackingTableService reportTrackingTableService;
    private final MappingTableService mappingTableService;
    private final ReportService reportService;

    @Override
    public ResponseEntity<GetReportById200Response> getReportById(Integer id) {
        var response = reportService.createReportResponse(id);

        log.debug("Returning a report response to user");
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ReportsGet200Response> reportsGet() {
        var reportListEntries = mappingTableService.fetchReportListEntries();

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
    public ResponseEntity<StreamingResponseBody> getCSV(@PathVariable(value = "id") int requestedId,
                                                        @RegisteredOAuth2AuthorizedClient("graph") OAuth2AuthorizedClient graphClient) {
        reportTrackingTableService.updateReportTrackingTable(requestedId, graphClient);

        log.debug("Returning a CSV response to user");
        return reportService.createCSVResponse(requestedId);
    }


    /**
     * Sends a report to the user in the form of a CSV data stream. If the user requests via a web browser this response then triggers the browser to download the file.
     *
     * @param requestedId - id of the requested report
     * @return CSV data stream or reports data
     */
    @RequestMapping(value = "/csv2222/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> getCSV2222(@PathVariable(value = "id") int requestedId,
                                                        @RegisteredOAuth2AuthorizedClient("graph") OAuth2AuthorizedClient graphClient) {
        reportTrackingTableService.updateReportTrackingTable(requestedId, graphClient);

        log.debug("Returning a CSV response to user");
        return reportService.createCSVResponse(requestedId);
    }

}
