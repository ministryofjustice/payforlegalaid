package uk.gov.laa.pfla.auth.service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.laa.pfla.auth.service.dao.ReportTrackingTableDao;
import uk.gov.laa.pfla.auth.service.models.ReportTrackingTableModel;
import uk.gov.laa.pfla.auth.service.responses.ReportListResponse;

import java.time.LocalDateTime;

@Service
public class ReportTrackingTableService {

    private final ReportTrackingTableDao reportTrackingTableDao;
    private final MappingTableService mappingTableService;


    @Autowired
    public ReportTrackingTableService(ReportTrackingTableDao reportTrackingTableDao, MappingTableService mappingTableService) {
        this.reportTrackingTableDao = reportTrackingTableDao;
        this.mappingTableService = mappingTableService;
    }

    public void updateReportTrackingTable(int requestedId, LocalDateTime creationTime) {

        //Querying the mapping table, to obtain metadata about the report
        ReportListResponse reportListResponse = mappingTableService.getDetailsForSpecificReport(requestedId);

        ReportTrackingTableModel reportTrackingTableModel = ReportTrackingTableModel.builder()
                .reportName(reportListResponse.getReportName())
                .reportUrl("www.sharepoint.com/place-where-we-will-create-report")
                .creationTime(creationTime)
                .mappingId(reportListResponse.getId())
                .reportGeneratedBy("Barry White")
                .build();



        //Create a trackingtable row in the table
        reportTrackingTableDao.updateTrackingTable(reportTrackingTableModel);
    }



}