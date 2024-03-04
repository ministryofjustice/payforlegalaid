package uk.gov.laa.pfla.auth.service.services;

import org.springframework.stereotype.Service;
import uk.gov.laa.pfla.auth.service.dao.ReportTrackingTableDao;
import uk.gov.laa.pfla.auth.service.models.ReportTrackingTableModel;

import java.time.LocalDateTime;

@Service
public class ReportTrackingTableService {

    private final ReportTrackingTableDao reportTrackingTableDao;

    public ReportTrackingTableService(ReportTrackingTableDao reportTrackingTableDao) {
        this.reportTrackingTableDao = reportTrackingTableDao;
    }

    public void updateReportTracking(int requestedId, LocalDateTime creationTime) {


        ReportTrackingTableModel reportTrackingTableModel = ReportTrackingTableModel.builder()
                .id(requestedId)
                .reportName("Report1")
                .reportUrl("www.sharepoint.com/place-where-we-will-create-report")
                .creationTime(creationTime)
                .mappingId(1)
                .reportGeneratedBy("Barry White")
                .build();



        //Create a trackingtable row in the table
        reportTrackingTableDao.updateTrackingTable(reportTrackingTableModel);
    }



}