package uk.gov.laa.pfla.auth.service.services;

import org.springframework.stereotype.Service;
import uk.gov.laa.pfla.auth.service.builders.ReportTrackingTableModelBuilder;
import uk.gov.laa.pfla.auth.service.dao.ReportTrackingTableDao;
import uk.gov.laa.pfla.auth.service.models.ReportTrackingTableModel;

import java.time.LocalDateTime;

@Service
public class ReportTrackingTableService {

    private ReportTrackingTableDao reportTrackingTableDao;

    public ReportTrackingTableService(ReportTrackingTableDao reportTrackingTableDao) {
        this.reportTrackingTableDao = reportTrackingTableDao;
    }

    public void updateReportTracking(int requestedId, LocalDateTime creationTime) {

        ReportTrackingTableModel reportTrackingTableModel = new ReportTrackingTableModelBuilder()
                .withId(requestedId)
                .withReportName("Report1")
                .withReportUrl("www.sharepoint.com/place-where-we-will-create-report")
                .withCreationTime(creationTime)
                .withMappingId(1)
                .withReportGeneratedBy("Barry White")
                .createReportTrackingTableModel();



        //Create a trackingtable row in the table
        reportTrackingTableDao.updateTable(reportTrackingTableModel);
    }



}