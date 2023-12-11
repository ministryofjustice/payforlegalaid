package uk.gov.laa.pfla.auth.service.dao;

import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import uk.gov.laa.pfla.auth.service.models.ReportTableModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Repository
//@Slf4j
public class ReportTableDao {

    public static final Logger log = LoggerFactory.getLogger(ReportTableDao.class);

    private final List<ReportTableModel> reportTableObjectList = new ArrayList<>();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final ModelMapper mapper = new ModelMapper();

    public List<ReportTableModel> fetchReport(String reportViewName) {

        reportTableObjectList.clear(); // Prevent data accumulating after multiple requests

        List<Map<String, Object>> resultList;


//        String query = "SELECT * FROM ANY_REPORT.V_BANK_MONTH";
        String query =  String.format("SELECT * FROM ANY_REPORT.%s", reportViewName);

        log.debug("just before result list: ");



        resultList = jdbcTemplate.queryForList(query);
        log.debug("Result list, a list of objects each representing a row in the Report Table: " + resultList);

        try {
            resultList.forEach(obj -> {
                ReportTableModel reportTableObject = mapper.map(obj, ReportTableModel.class);
                reportTableObjectList.add(reportTableObject);
            });
            log.debug("reportTableObjectList: " + reportTableObjectList);
        } catch (MappingException e) {
            log.error("Exception with model map loop: " + e);
        }


        return reportTableObjectList;

    }



}
