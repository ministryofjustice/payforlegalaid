package uk.gov.laa.gpfd.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.gov.laa.gpfd.exception.DatabaseReadException;

import java.util.List;
import java.util.Map;

/**
 * Data Access Object (DAO) for querying report data from the MOJFIN reports database.
 * <p>
 * This service is responsible for executing SQL queries on the MOJFIN database and
 * retrieving results as a list of maps. It provides an abstraction for interacting
 * with the underlying database to fetch report data.
 * </p>
 * <p>
 * If the query returns no results, a {@link DatabaseReadException} is thrown.
 * If an error occurs while querying the database, a {@link DatabaseReadException}
 * is also thrown with details about the error.
 * </p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class ReportViewsDao {
    private static final String NO_RESULT = "No results returned from query to MOJFIN reports database";
    private final JdbcTemplate writeJdbcTemplate;

    /**
     * Executes a SQL query to retrieve data from the MOJFIN reports database.
     * <p>
     * This method takes an SQL query as input, executes it using {@link JdbcTemplate#queryForList(String)},
     * and returns the result as a list of maps, where each map represents a row of the result set with
     * column names as keys and corresponding values. If no results are returned or if an error occurs,
     * an appropriate exception is thrown.
     * </p>
     *
     * @param sqlQuery the SQL query to be executed against the database.
     * @return a list of maps representing the rows returned by the query.
     * @throws DatabaseReadException if no results are returned from the query or if there is an error reading from the database.
     */
    public List<Map<String, Object>> callDataBase(String sqlQuery) {
        try {
            log.debug("Retrieving data {}", sqlQuery);
            var resultList = writeJdbcTemplate.queryForList(sqlQuery);
            if (resultList.isEmpty()) {
                throw new DatabaseReadException(NO_RESULT);
            }

            log.debug("returning result list {}", resultList);
            return resultList;
        } catch (DataAccessException e) {
            throw new DatabaseReadException("Error reading from DB: " + e);
        }
    }

}
