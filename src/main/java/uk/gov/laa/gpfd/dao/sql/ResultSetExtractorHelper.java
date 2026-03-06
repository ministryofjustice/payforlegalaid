package uk.gov.laa.gpfd.dao.sql;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetExtractorHelper<T> implements ResultSetExtractor<T> {

    private final RowCallbackHandler rowCallbackHandler;

    public ResultSetExtractorHelper(RowCallbackHandler rowCallbackHandler) {
        this.rowCallbackHandler = rowCallbackHandler;
    }

    @Override
    public T extractData(ResultSet rs) throws SQLException, DataAccessException {
        while (rs.next()){
            rowCallbackHandler.processRow(rs);
        }
        return null;
    }
}
