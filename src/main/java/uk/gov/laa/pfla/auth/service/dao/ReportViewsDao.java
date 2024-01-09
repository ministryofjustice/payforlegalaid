package uk.gov.laa.pfla.auth.service.dao;

import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
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
@NoArgsConstructor
public class ReportViewsDao {

    public static final Logger log = LoggerFactory.getLogger(ReportViewsDao.class);

    private JdbcTemplate jdbcTemplate;

    private final ModelMapper mapper = new ModelMapper();

    @Autowired
    public ReportViewsDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ReportModel> fetchReport(String sqlQuery, Class<? extends ReportModel>requestedModelClass) {

        final List<ReportModel> reportViewObjectList = new ArrayList<>();

        List<Map<String, Object>> resultList = callDataBase(sqlQuery);


        try {  // Mapping the results of the database query to a list of reportModel objects
            resultList.forEach(obj -> {
                ReportModel reportTableObject = mapper.map(obj,  requestedModelClass);

                // Add db data straight to csv here
//                obj.forEach((obj1, obj2) -> {
//                    System.out.println((String)obj1); //todo - this print statement is just for illustrative purposes
//                    System.out.println((String)obj2);
//                });



                reportViewObjectList.add(reportTableObject);
//                convertSourceObjToModel(obj,)
            });
            log.debug("reportViewObjectList: {}", reportViewObjectList);
        } catch (MappingException e) {
            log.error("Exception with model map loop: " + e);
        }

        return reportViewObjectList;

    }

    @NotNull
    public List<Map<String, Object>> callDataBase(String sqlQuery) {
        List<Map<String, Object>> resultList;
        log.debug("Calling database for result list, with sqlQuery: {} ", sqlQuery);
        resultList = jdbcTemplate.queryForList(sqlQuery);
        log.debug("Result list, a list of objects each representing a row in the Report Table: {}", resultList); //Todo: remove some of this logging when going graduating from MVP to phase 2, when we incorporate reports with sensitive data
        return resultList;
    }

//    public <T> T convertSourceObjToModel(Object sourceObject, Class<T> modelClass) {
//        return mapper.map(sourceObject, modelClass);
//    }



}
