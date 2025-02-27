package uk.gov.laa.gpfd.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.laa.gpfd.dao.ReportsDao;
import uk.gov.laa.gpfd.exception.DatabaseReadException;
import uk.gov.laa.gpfd.mapper.ReportsGet200ResponseReportListInnerMapper;
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
@RequiredArgsConstructor
public class ReportManagementService {
    private final ReportsDao reportsDao;

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
        return reportsDao.fetchReportList().stream()
                .map(ReportsGet200ResponseReportListInnerMapper::map)
                .toList();
    }


}
