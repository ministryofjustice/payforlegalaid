package uk.gov.laa.gpfd.services;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.laa.gpfd.dao.ReportDao;
import uk.gov.laa.gpfd.exception.DatabaseReadException;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;
import uk.gov.laa.gpfd.exception.ReportOutputTypeNotFoundException;
import uk.gov.laa.gpfd.mapper.ResourceResponseMapper;
import uk.gov.laa.gpfd.model.GetReportById200Response;
import uk.gov.laa.gpfd.model.Report;
import uk.gov.laa.gpfd.model.ReportsGet200ResponseReportListInner;

import java.util.List;

/**
 * Service class responsible for interacting with the Reports table and transforming its data
 * into a format suitable for API responses.
 * <p>
 * This class provides methods to fetch a list of report entries and retrieve details of a specific report
 * based on the requested report ID. The report data is retrieved from a data source and transformed into
 * {@link ReportsGet200ResponseReportListInner} objects.
 * </p>
 */

@Slf4j
@Service
public record ReportManagementService(
        ReportDao reportDetailsDao,
        ResourceResponseMapper<Report, GetReportById200Response> reportByIdMapper,
        ResourceResponseMapper<Report, ReportsGet200ResponseReportListInner> innerResponseMapper
) {

    /**
     * Fetches a list of report entries from the database, maps them into {@link ReportsGet200ResponseReportListInner}
     * objects, and returns the result.
     * <p>
     * This method retrieves all the report entries from the database, transforms them using the
     *  ReportsGet200ResponseReportListInnerMapper map method, and returns a list
     * of mapped report entries.
     * </p>
     *
     * @return a list of {@link ReportsGet200ResponseReportListInner} objects containing the report data
     * @throws DatabaseReadException if there is an error fetching data from the database
     */
    public List<ReportsGet200ResponseReportListInner> fetchReportListEntries() {
        return reportDetailsDao.fetchReports().stream()
                .map(innerResponseMapper::map)
                .toList();
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
        var reportDetails = reportDetailsDao.fetchReportById(id);
        if (reportDetails.isEmpty()) {
            throw new ReportIdNotFoundException("Report with unrecognised ID");
        }

        log.info("Returning report response object for report ID {}", id);
        return reportByIdMapper.map(reportDetails.get());
    }
}
