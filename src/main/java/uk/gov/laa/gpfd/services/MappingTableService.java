package uk.gov.laa.gpfd.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.laa.gpfd.dao.MappingTableDao;
import uk.gov.laa.gpfd.exception.DatabaseReadException;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;
import uk.gov.laa.gpfd.mapper.ReportsGet200ResponseReportListInnerMapper;
import uk.gov.laa.gpfd.model.ReportsGet200ResponseReportListInner;

import java.util.List;

/**
 * Service class responsible for interacting with the MappingTable and transforming its data
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
public class MappingTableService {

    private final MappingTableDao mappingTableDao;

    /**
     * Fetches a list of report entries from the database, maps them into {@link ReportsGet200ResponseReportListInner}
     * objects, and returns the result.
     * <p>
     * This method retrieves all the report entries from the database, transforms them using the
     * {@link ReportsGet200ResponseReportListInnerMapper#map(MappingTable)} method, and returns a list
     * of mapped report entries.
     * </p>
     *
     * @return a list of {@link ReportsGet200ResponseReportListInner} objects containing the report data
     * @throws DatabaseReadException if there is an error fetching data from the database
     */
    public List<ReportsGet200ResponseReportListInner> fetchReportListEntries() {
        return mappingTableDao.fetchReportList().stream()
                .map(ReportsGet200ResponseReportListInnerMapper::map)
                .toList();
    }

    /**
     * Retrieves the details of a specific report based on the provided report ID.
     * <p>
     * This method checks that the report ID is within a valid range (1-999) and attempts to find the report
     * by its ID. If the report is found, it returns the mapped {@link ReportsGet200ResponseReportListInner}
     * object. If the report ID is out of range or the report is not found, appropriate exceptions are thrown.
     * </p>
     *
     * @param requestedId the ID of the requested report
     * @return a {@link ReportsGet200ResponseReportListInner} object containing the details of the requested report
     * @throws IndexOutOfBoundsException if the requested ID is outside the valid range (1-999)
     * @throws ReportIdNotFoundException if the report with the requested ID is not found
     * @throws DatabaseReadException if there is an error fetching data from the database
     */
    public ReportsGet200ResponseReportListInner getDetailsForSpecificReport(int requestedId) {
        if (requestedId <= 0 || requestedId >= 1000) throw new IndexOutOfBoundsException("Report ID needs to be a number between 0 and 1000");

        return mappingTableDao.fetchReportList().stream()
                .map(ReportsGet200ResponseReportListInnerMapper::map)
                .filter(o -> o.getId() == requestedId - 1) // Adjust for 0-based index
                .findFirst()
                .orElseThrow(() -> new ReportIdNotFoundException("Report ID not found with ID: " + requestedId));
    }
}