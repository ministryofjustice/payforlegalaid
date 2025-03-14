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
    private final ReportManagementService reportManagementService;
    private final UserService userService;

    public void saveReportsTracking(UUID requestedId, OAuth2AuthorizedClient graphClient) {
        var reportDetails = reportManagementService.getDetailsForSpecificReport(requestedId);
        var user = userService.getUserDetails(graphClient);

        var reportsTracking = ReportsTracking.builder()
            .id(UUID.randomUUID())
            .name(reportDetails.getName())
            .reportUrl("www.sharepoint.com/place-where-we-will-create-report")
            .creationDate(Timestamp.valueOf(LocalDateTime.now()))
            .reportId(reportDetails.getId())
            .reportCreator(user.givenName() + " " + user.surname())
            .reportOwner(reportDetails.getReportOwnerName())
            .reportOutputType(reportDetails.getReportOutputType().toString())
            .templateUrl(reportDetails.getTemplateSecureDocumentId())
            .build();

        log.info("Saving report tracking information");
        reportsTrackingDao.saveReportsTracking (reportsTracking);
    }
}