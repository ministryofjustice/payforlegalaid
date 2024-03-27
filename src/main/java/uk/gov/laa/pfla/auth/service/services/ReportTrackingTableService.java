package uk.gov.laa.pfla.auth.service.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;
import uk.gov.laa.pfla.auth.service.beans.UserDetails;
import uk.gov.laa.pfla.auth.service.dao.ReportTrackingTableDao;
import uk.gov.laa.pfla.auth.service.exceptions.UserServiceException;
import uk.gov.laa.pfla.auth.service.models.ReportTrackingTableModel;
import uk.gov.laa.pfla.auth.service.responses.ReportListResponse;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class ReportTrackingTableService {
    public static final Logger log = LoggerFactory.getLogger(ReportTrackingTableService.class);

    private final ReportTrackingTableDao reportTrackingTableDao;
    private final MappingTableService mappingTableService;

    private final UserService userService;


    @Autowired
    public ReportTrackingTableService(ReportTrackingTableDao reportTrackingTableDao, MappingTableService mappingTableService, UserService userService) {
        this.reportTrackingTableDao = reportTrackingTableDao;
        this.mappingTableService = mappingTableService;
        this.userService = userService;
    }

    public synchronized void updateReportTrackingTable(int requestedId, OAuth2AuthorizedClient graphClient) throws UserServiceException {
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        //Querying the mapping table, to obtain metadata about the report
        ReportListResponse reportListResponse = mappingTableService.getDetailsForSpecificReport(requestedId);

        UserDetails user = userService.getUserDetails(graphClient);


        ReportTrackingTableModel reportTrackingTableModel = ReportTrackingTableModel.builder()
                .reportName(reportListResponse.getReportName())
                .reportUrl("www.sharepoint.com/place-where-we-will-create-report")
                .creationTime(timestamp)
                .mappingId(reportListResponse.getId())
                .reportGeneratedBy(user.getGivenName() + " " + user.getSurname())
                .build();

        //Create a new trackingtable row
        log.info("Updating tracking information");
        reportTrackingTableDao.updateTrackingTable(reportTrackingTableModel);
    }


}