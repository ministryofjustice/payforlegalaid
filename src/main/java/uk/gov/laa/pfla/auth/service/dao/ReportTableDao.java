package uk.gov.laa.pfla.auth.service.dao;

import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import uk.gov.laa.pfla.auth.service.models.ReportTableModel;

import java.sql.CallableStatement;
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

    public List<ReportTableModel> fetchReport(String sqlQuery) {

        reportTableObjectList.clear(); // Prevent data accumulating after multiple requests

        List<Map<String, Object>> resultList;


//        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
//                .withProcedureName()

//        String query =  String.format("SELECT * FROM ANY_REPORT.%s", sqlQuery);

        log.debug("Calling result list, with sqlQuery: {} ", sqlQuery);
        resultList = jdbcTemplate.queryForList(sqlQuery);
        log.debug("Result list, a list of objects each representing a row in the Report Table: {}", resultList); //Todo: remove some of this logging when going graduating from MVP to phase 2, when we incorporate reports with sensitive data

        try {  // Mapping the results of the database query to a list of reportTableModel objects
            resultList.forEach(obj -> {
                ReportTableModel reportTableObject = mapper.map(obj, ReportTableModel.class);
                reportTableObjectList.add(reportTableObject);
            });
            log.debug("reportTableObjectList: {}", reportTableObjectList);
        } catch (MappingException e) {
            log.error("Exception with model map loop: " + e);
        }

        return reportTableObjectList;

    }



}
