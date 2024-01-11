package uk.gov.laa.pfla.auth.service.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.gov.laa.pfla.auth.service.models.report_view_models.ReportModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//import static uk.gov.laa.pfla.auth.service.helpers.DateMapper.getDateConverter;

@Service
@Slf4j
@AllArgsConstructor
public class ReportViewsDao {

    private final ModelMapper mapper = new ModelMapper();

    private final JdbcTemplate jdbcTemplate;

    public List<ReportModel> fetchReport(String sqlQuery, Class<? extends ReportModel> requestedModelClass) {
//        mapper.addConverter(getDateConverter());

        final List<ReportModel> reportViewObjectList = new ArrayList<>();

        List<Map<String, Object>> resultList = callDataBase(sqlQuery);


        try {  // Mapping the results of the database query to a list of reportModel objects
            resultList.forEach(obj -> {
                ReportModel reportTableObject = mapper.map(obj, requestedModelClass);

                // Add db data straight to csv here
                log.info("Current resultlist map objects: ");
                obj.forEach((obj1, obj2) -> {
                    log.info(String.valueOf(obj1)); //todo - this print statement is just for illustrative purposes
                    log.info(String.valueOf(obj1.getClass()));
                    log.info(String.valueOf(obj2));
                    log.info(String.valueOf(obj2.getClass()));

                });


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
