package uk.gov.laa.gpfd.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;

import java.util.List;
import java.util.Map;

import static uk.gov.laa.gpfd.exception.DatabaseReadException.DatabaseFetchException;

@Slf4j
@Service
public record ReportViewsDao(JdbcOperations writeJdbcTemplate) {

    public List<Map<String, Object>> callDataBase(String sqlQuery) throws ReportIdNotFoundException {
        try {
            log.debug("Retrieving data");
            return writeJdbcTemplate.queryForList(sqlQuery);
        } catch (DataAccessException e) {
            throw new DatabaseFetchException("Error reading from DB: " + e);
        }
    }

}
