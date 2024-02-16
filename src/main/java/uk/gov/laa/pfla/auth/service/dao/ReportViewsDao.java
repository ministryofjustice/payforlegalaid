package uk.gov.laa.pfla.auth.service.dao;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
@Slf4j
@AllArgsConstructor
public class ReportViewsDao {


    private final JdbcTemplate jdbcTemplate;


    @NotNull
    public List<Map<String, Object>> callDataBase(String sqlQuery) throws DataAccessException {
        List<Map<String, Object>> resultList;
        log.debug("Calling database for result list, with sqlQuery: {} ", sqlQuery);
        resultList = jdbcTemplate.queryForList(sqlQuery);
        log.debug("Result list, a list of objects each representing a row in the Report Table: {}", resultList); //Todo: remove some of this logging when going graduating from MVP to phase 2, when we incorporate reports with sensitive data
        return resultList;
    }


}
