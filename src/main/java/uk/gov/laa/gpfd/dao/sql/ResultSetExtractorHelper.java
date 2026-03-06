package uk.gov.laa.gpfd.dao.sql;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class ResultSetExtractorHelper<T> implements ResultSetExtractor<T> {

    private final RowCallbackHandler rowCallbackHandler;

    public ResultSetExtractorHelper(RowCallbackHandler rowCallbackHandler) {
        this.rowCallbackHandler = rowCallbackHandler;
    }

    @Override
    public T extractData(ResultSet rs) throws SQLException, DataAccessException {
        long last = System.nanoTime();
        while (rs.next()){
            long now = System.nanoTime();
            long deltaMicros = (now - last) / 1000;
            log.warn("Calling next row been {} microseconds since last", deltaMicros);
            rowCallbackHandler.processRow(rs);
            last = System.nanoTime();
        }
        return null;
    }
}
