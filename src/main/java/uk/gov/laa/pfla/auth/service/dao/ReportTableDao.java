package uk.gov.laa.pfla.auth.service.dao;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import uk.gov.laa.pfla.auth.service.models.ReportTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class ReportTableDao {


    private final List<ReportTableModel> reportTableObjectList = new ArrayList<>();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final ModelMapper mapper = new ModelMapper();

    public List<ReportTableModel> fetchReport(int requestedId) {

        reportTableObjectList.clear(); // Prevent data accumulating after multiple requests

        List<Map<String, Object>> resultList;


        String query = "SELECT * FROM ANY_REPORT.V_BANK_MONTH";

        log.debug("just before result list: ");

        resultList = jdbcTemplate.queryForList(query);
        log.debug("Result list, a list of objects each representing a row in the DB: " + resultList);

        try {
            resultList.forEach(obj -> {
                ReportTableModel reportTableObject = mapper.map(obj, ReportTableModel.class);
                reportTableObjectList.add(reportTableObject);
            });
        } catch (MappingException e) {
            log.error("Exception with model map loop: " + e);
        }


        return reportTableObjectList;




//        LocalDateTime placeHolderDateTime = LocalDateTime.now();
//
//
//
//        return new ReportTableModelBuilder()
//                .withId(requestedId).withReportName("AP_and_AR_Combined-DEBT-AGING-SUMMARY-4")
//                .withReportUrl("www.sharepoint.com/an-example-report.csv")
//                .withCreationTime(placeHolderDateTime)
//                .createReportModel();

    }



}
