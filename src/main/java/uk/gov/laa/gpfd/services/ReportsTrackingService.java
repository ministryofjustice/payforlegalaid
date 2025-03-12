package uk.gov.laa.gpfd.services;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;
import uk.gov.laa.gpfd.model.ReportsTracking;
import uk.gov.laa.gpfd.dao.ReportsTrackingDao;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportsTrackingService {

    private final ReportsTrackingDao reportsTrackingDao;
    private final MappingTableService mappingTableService;
    private final UserService userService;

    public void saveReportsTracking(UUID requestedId, OAuth2AuthorizedClient graphClient) {
        var reportListResponse = mappingTableService.getDetailsForSpecificReport(requestedId);
        var user = userService.getUserDetails(graphClient);

        var reportsTracking = ReportsTracking.builder()
            .id(UUID.randomUUID())
            .name(reportListResponse.getReportName())
            .reportUrl("www.sharepoint.com/place-where-we-will-create-report")
            .creationDate(Timestamp.valueOf(LocalDateTime.now()))
            .reportId(reportListResponse.getId())
            .reportCreator(user.givenName() + " " + user.surname())
            .build();

        log.info("Saving report tracking information");
        reportsTrackingDao.saveReportsTracking (reportsTracking);
    }
}