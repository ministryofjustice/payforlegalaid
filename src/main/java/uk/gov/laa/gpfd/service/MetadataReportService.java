package uk.gov.laa.gpfd.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.laa.gpfd.exception.DatabaseReadException;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;
import uk.gov.laa.gpfd.model.GetReportById200Response;

import java.net.URI;

/**
 * Service class responsible for handling operations related to metadata reports.
 * <p>
 * This service integrates with the {@link MappingTableService} to fetch report metadata and format it into a
 * response structure suitable for API clients.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MetadataReportService {

    private final MappingTableService mappingTableService;

    /**
     * Creates a JSON response for the /report API endpoint based on the requested report ID.
     * <p>
     * This method fetches metadata for the specified report using the {@link MappingTableService#getDetailsForSpecificReport(int)} method,
     * formats the data into a {@link GetReportById200Response}, and includes a pre-configured download URL for the CSV file
     * associated with the report.
     * </p>
     * <p>
     * The method logs the generated response for auditing and debugging purposes.
     * </p>
     *
     * @param id the unique identifier of the requested report
     * @return a {@link GetReportById200Response} object containing metadata for the requested report
     * @throws IndexOutOfBoundsException if the report ID is less than 1 or greater than the maximum allowed ID (e.g., 1000)
     * @throws ReportIdNotFoundException if no report is found for the given ID
     * @throws DatabaseReadException if there is an issue reading data from the database
     */
    public GetReportById200Response createReportResponse(int id) {
        var reportListResponse = mappingTableService.getDetailsForSpecificReport(id);

        var reportResponse = new GetReportById200Response() {{
            setId(reportListResponse.getId());
            setReportName(reportListResponse.getReportName());
            setReportDownloadUrl(URI.create("https://laa-pay-for-la-dev.apps.live.cloud-platform.service.justice.gov.uk/" + "csv/" + id));
        }};

        log.info("Returning report response object {}", reportResponse);

        return reportResponse;
    }
}
