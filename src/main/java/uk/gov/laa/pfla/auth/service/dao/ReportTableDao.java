package uk.gov.laa.pfla.auth.service.dao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import uk.gov.laa.pfla.auth.service.builders.ReportTableModelBuilder;
import uk.gov.laa.pfla.auth.service.models.ReportTableModel;

import java.time.LocalDateTime;
@Repository
public class ReportTableDao {

    @Value("${demo-secret}")
    private String demoEnvVariable;

//    private final JdbcTemplate jdbcTemplate; //pass this into the constructor as an arg once it's ready?
    public ReportTableModel fetchReport(int requestedId) {
        LocalDateTime placeHolderDateTime = LocalDateTime.now();



        return new ReportTableModelBuilder()
                .withId(requestedId).withReportName("AP_and_AR_Combined-DEBT-AGING-SUMMARY-4 " + demoEnvVariable )
                .withReportUrl("www.sharepoint.com/an-example-report.csv")
                .withCreationTime(placeHolderDateTime)
                .createReportModel();

    }



}
