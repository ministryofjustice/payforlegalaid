package uk.gov.laa.gpfd.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.laa.gpfd.config.AppConfig;
import uk.gov.laa.gpfd.exception.CsvStreamException;
import uk.gov.laa.gpfd.exception.DatabaseReadException;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;
import uk.gov.laa.gpfd.model.GetReportById200Response;

import java.net.URI;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final MappingTableService mappingTableService;
    private final AppConfig appConfig;
    private final DataStreamer dataStreamer;

    /**
     * Create a Response entity with a CSV data stream inside the body, for use by the controller's '/csv' endpoint
     *
     * @param requestedId - the ID of the requested report
     * @return a ResponseEntity of type 'StreamingResponseBody', containing a stream of CSV data
     */
    public ResponseEntity<StreamingResponseBody> createCSVResponse(UUID requestedId) throws ReportIdNotFoundException, DatabaseReadException, IndexOutOfBoundsException, CsvStreamException {
        var reportListResponse = mappingTableService.getDetailsForSpecificMapping(requestedId);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=%s.csv".formatted(reportListResponse.getReportName()))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(stream -> dataStreamer.stream(reportListResponse.getSqlQuery(), stream));
    }

    /**
     * Create a json response to be used by the /report API endpoint. Once a caching system is in place, this response will serve as confirmation that a csv file has been created, and when.
     *
     * @param id - id of the requested report
     * @return reportResponse containing json data about the requested report
     * @throws ReportIdNotFoundException - From the getDetailsForSpecificReport() method call, if the requested index is not found
     * @throws DatabaseReadException     - From the createReportListResponseList() method call inside getDetailsForSpecificReport()
     */
    public GetReportById200Response createReportResponse(UUID id) {
        var reportListResponse = mappingTableService.getDetailsForSpecificReport(id);

        var reportResponse = new GetReportById200Response() {{
            setId(reportListResponse.getId());
            setReportName(reportListResponse.getReportName());
            setReportDownloadUrl(URI.create(appConfig.getServiceUrl() + "/csv/" + id));
        }};

        log.info("Returning report response object");

        return reportResponse;
    }

}