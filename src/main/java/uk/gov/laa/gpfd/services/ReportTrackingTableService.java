package uk.gov.laa.gpfd.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;
import uk.gov.laa.gpfd.dao.ReportTrackingTableDao;
import uk.gov.laa.gpfd.model.ReportTrackingTable;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;


/**
 * Service class responsible for managing the report tracking functionality.
 * <p>
 * This service is responsible for updating the tracking table with metadata about a specific report.
 * It retrieves report details from the {@link MappingTableService} and user information from the
 * {@link UserService}. The tracking information is then inserted or updated into the database
 * via the {@link ReportTrackingTableDao}.
 * </p>
 *
 * <p>
 * The {@link #updateReportTrackingTable(int, OAuth2AuthorizedClient)} method is synchronized
 * to ensure thread-safety when multiple requests are attempting to update the report tracking data
 * concurrently.
 * </p>
 *
 * <p>
 * This class uses the {@link ReportTrackingTableDao} to interact with the database and perform
 * insertions or updates of the report tracking data.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportTrackingTableService {

    private final ReportTrackingTableDao reportTrackingTableDao;
    private final MappingTableService mappingTableService;
    private final UserService userService;

    /**
     * Updates the report tracking table for a specific report based on the requested ID.
     * <p>
     * This method is synchronized to ensure thread-safety when multiple threads attempt to update
     * the report tracking table simultaneously. It retrieves the report details and the current user
     * details, then constructs a {@link ReportTrackingTable} object to be inserted or updated in the database.
     * </p>
     *
     * @param requestedId the ID of the report for which tracking information is being updated.
     *                    The ID must be between 1 and 1000.
     * @param graphClient the authorized client used to retrieve the user details.
     * @throws ReportIdNotFoundException if the requested report ID is not found in the mapping table.
     * @throws DatabaseReadException if there is an error interacting with the database.
     */
    public synchronized void updateReportTrackingTable(UUID requestedId, OAuth2AuthorizedClient graphClient) {
        var reportListResponse = mappingTableService.getDetailsForSpecificReport(requestedId);
        var user = userService.getUserDetails(graphClient);

        var reportTrackingTable = ReportTrackingTable.builder()
                .reportName(reportListResponse.getReportName())
                .reportUrl("www.sharepoint.com/place-where-we-will-create-report")
                .creationTime(Timestamp.valueOf(LocalDateTime.now()))
                .mappingId(reportListResponse.getId())
                .reportGeneratedBy(user.givenName() + " " + user.surname())
                .build();

        // Log and update the tracking table
        log.debug("Updating tracking information for report {}", requestedId);
        reportTrackingTableDao.updateTrackingTable(reportTrackingTable);
    }

}