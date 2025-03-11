package uk.gov.laa.gpfd.services;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;
import uk.gov.laa.gpfd.model.ReportsTracking;
import uk.gov.laa.gpfd.repository.ReportsTrackingRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportsTrackingService {

    private final ReportsTrackingRepository reportsTrackingRepository;
    private final MappingTableService mappingTableService;
    private final UserService userService;

    public ReportsTracking saveReportsTracking(UUID requestedId, OAuth2AuthorizedClient graphClient) {
        var reportListResponse = mappingTableService.getDetailsForSpecificReport(requestedId);
        var user = userService.getUserDetails(graphClient);

        var reportsTracking = ReportsTracking.builder()
            .id(UUID.randomUUID())
            .reportName(reportListResponse.getReportName())
            .reportUrl("www.sharepoint.com/place-where-we-will-create-report")
            .creationTime(Timestamp.valueOf(LocalDateTime.now()))
            .mappingId(reportListResponse.getId())
            .reportGeneratedBy(user.givenName() + " " + user.surname())
            .build();

        log.info("Saving report tracking information");
        return reportsTrackingRepository.save(reportsTracking);
    }
}