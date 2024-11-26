package uk.gov.laa.gpfd.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Service;
import uk.gov.laa.gpfd.bean.UserDetails;
import uk.gov.laa.gpfd.dao.ReportTrackingTableDao;
import uk.gov.laa.gpfd.model.ReportsGet200ResponseReportListInner;
import uk.gov.laa.gpfd.model.ReportTrackingTable;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportTrackingTableService {

    private final ReportTrackingTableDao reportTrackingTableDao;
    private final MappingTableService mappingTableService;
    private final UserService userService;

    public synchronized void updateReportTrackingTable(int requestedId, OAuth2AuthorizedClient graphClient) {
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        var reportListResponse = mappingTableService.getDetailsForSpecificReport(requestedId);

        UserDetails user = userService.getUserDetails(graphClient);

        var reportTrackingTable = ReportTrackingTable.builder()
                .reportName(reportListResponse.getReportName())
                .reportUrl("www.sharepoint.com/place-where-we-will-create-report")
                .creationTime(timestamp)
                .mappingId(reportListResponse.getId())
                .reportGeneratedBy(user.givenName() + " " + user.surname())
                .build();

        //Create a new trackingtable row
        log.info("Updating tracking information");
        reportTrackingTableDao.updateTrackingTable(reportTrackingTable);
    }

}