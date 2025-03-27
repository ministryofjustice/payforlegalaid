package uk.gov.laa.gpfd.services;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
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

    @Async
    public void saveReportsTracking(UUID requestedId) {
        log.debug("Tracking report {} being accessed by {}", requestedId, userService.getCurrentUserName());
        var reportDetails = reportManagementService.getDetailsForSpecificReport(requestedId);

        var reportsTracking = ReportsTracking.builder()
            .id(UUID.randomUUID())
            .name(reportDetails.getName())
            .reportUrl("www.sharepoint.com/place-where-we-will-create-report")
            .creationDate(Timestamp.valueOf(LocalDateTime.now()))
            .reportId(reportDetails.getId())
            .reportCreator(userService.getCurrentUserName())
            .reportOwner(reportDetails.getReportOwnerName())
            .reportOutputType(reportDetails.getReportOutputType().toString())
            .templateUrl(reportDetails.getTemplateSecureDocumentId())
            .build();

        reportsTrackingDao.saveReportsTracking (reportsTracking);
        log.debug("After tracking report {} being accessed by {}", requestedId, userService.getCurrentUserName());
    }
}