package uk.gov.laa.gpfd.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.laa.gpfd.exception.CsvStreamException;
import uk.gov.laa.gpfd.exception.DatabaseReadException;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;
import uk.gov.laa.gpfd.exception.ReportOutputTypeNotFoundException;
import uk.gov.laa.gpfd.exception.SqlFormatException;
import uk.gov.laa.gpfd.model.GetReportById200Response;
import uk.gov.laa.gpfd.utils.SqlFormatValidator;

import java.net.URI;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final MappingTableService mappingTableService;
    private final ReportManagementService reportManagementService;
    private final DataStreamer dataStreamer;
    private final SqlFormatValidator sqlFormatValidator;

    /**
     * Create a Response entity with a CSV data stream inside the body, for use by the controller's '/csv' endpoint
     *
     * @param requestedId - the ID of the requested report
     * @return a ResponseEntity of type 'StreamingResponseBody', containing a stream of CSV data
     */
    public ResponseEntity<StreamingResponseBody> createCSVResponse(UUID requestedId) throws ReportIdNotFoundException, DatabaseReadException, IndexOutOfBoundsException, CsvStreamException {
        var reportDetails = mappingTableService.getDetailsForSpecificMapping(requestedId);
        //TODO test
        var sqlQuery = reportDetails.getSqlQuery();
        if (!sqlFormatValidator.isSqlFormatValid(sqlQuery)){
            throw new SqlFormatException("SQL format invalid for report %s (id %s)".formatted(reportDetails.getReportName(), reportDetails.getId()));
        }

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=%s.csv".formatted(reportDetails.getReportName()))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(stream -> dataStreamer.stream(sqlQuery, stream));
    }

    /**
     * Create a json response to be used by the /reports API endpoint. Once a caching system is in place, this response will serve as confirmation that a csv file has been created, and when.
     *
     * @param id - id of the requested report
     * @return reportResponse containing json data about the requested report
     * @throws ReportIdNotFoundException - From the getDetailsForSpecificReport() method call, if the requested index is not found
     * @throws DatabaseReadException     - From the createReportListResponseList() method call inside getDetailsForSpecificReport()
     * @throws ReportOutputTypeNotFoundException  - From the FileExtension.getSubPathForExtension(reportDetails.getExtension()) call
     */
    public GetReportById200Response createReportResponse(UUID id) {
        log.info("Getting details for report ID {}", id);
        var reportDetails = reportManagementService.getDetailsForSpecificReport(id);

        var reportResponse = new GetReportById200Response() {{
            setId(reportDetails.getId());
            setReportName(reportDetails.getName());
            setReportDownloadUrl(URI.create(reportDetails.getReportDownloadUrl()));
        }};

        log.info("Returning report response object for report ID {}", id);

        return reportResponse;
    }

}