package uk.gov.laa.pfla.auth.service.dao;

import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import uk.gov.laa.pfla.auth.service.models.report_view_models.ReportModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Repository
//@Slf4j
public class ReportViewsDao {

    public static final Logger log = LoggerFactory.getLogger(ReportViewsDao.class);

    private JdbcTemplate jdbcTemplate;

    private final ModelMapper mapper = new ModelMapper();

    @Autowired
    public ReportViewsDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T> List<T> fetchReport(String sqlQuery, Class<T> requestedModelClass) {

        final List<T> reportTableObjectList = new ArrayList<>();

        List<Map<String, Object>> resultList;


        log.debug("Calling result list, with sqlQuery: {} ", sqlQuery);
        resultList = jdbcTemplate.queryForList(sqlQuery);
        log.debug("Result list, a list of objects each representing a row in the Report Table: {}", resultList); //Todo: remove some of this logging when going graduating from MVP to phase 2, when we incorporate reports with sensitive data

        try {  // Mapping the results of the database query to a list of reportTableModel objects
            resultList.forEach(obj -> {
                T reportTableObject = mapper.map(obj,  requestedModelClass);
                reportTableObjectList.add(reportTableObject);
//                convertSourceObjToModel(obj,)
            });
            log.debug("reportTableObjectList: {}", reportTableObjectList);
        } catch (MappingException e) {
            log.error("Exception with model map loop: " + e);
        }

        return reportTableObjectList;

    }

//    public <T> T convertSourceObjToModel(Object sourceObject, Class<T> modelClass) {
//        return mapper.map(sourceObject, modelClass);
//    }



}
