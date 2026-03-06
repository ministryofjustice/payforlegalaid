package uk.gov.laa.gpfd.dao.sql;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Wrapper to use our prexisting {@link RowCallbackHandler}s with a {@link ResultSetExtractor}
 * This is because upgrading to Spring Boot 4, the {@link JdbcTemplate} was not playing nicely with our RowCallbackHandlers
 * But there is lots of logic in them that we don't really want to rewrite for a new paradigm right now
 * @param <T>
 */
public class ResultSetExtractorHelper<T> implements ResultSetExtractor<T> {

    private final RowCallbackHandler rowCallbackHandler;

    public ResultSetExtractorHelper(RowCallbackHandler rowCallbackHandler) {
        this.rowCallbackHandler = rowCallbackHandler;
    }

    @Override
    public T extractData(ResultSet rs) throws SQLException, DataAccessException {
        while (rs.next()) {
            rowCallbackHandler.processRow(rs);
        }
        return null;
    }
}
