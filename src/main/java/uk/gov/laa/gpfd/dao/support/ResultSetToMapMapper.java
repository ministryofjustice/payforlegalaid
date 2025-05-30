package uk.gov.laa.gpfd.dao.support;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A {@link RowMapper} implementation that converts a {@link ResultSet} row into a {@link Map}
 * where column names are mapped to their corresponding values.
 */
public final class ResultSetToMapMapper implements RowMapper<Map<String, Object>> {

    /**
     * Converts a single row of a {@link ResultSet} to a {@link Map}.
     *
     * @param rs the ResultSet to map (pre-positioned for the current row)
     * @param rowNum the number of the current row (ignored in this implementation)
     * @return a new {@link Map} containing the column name-value pairs
     * @throws SQLException if a database access error occurs
     * @throws NullPointerException if the ResultSet is null
     */
    @Override
    public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
        var row = new LinkedHashMap<String, Object>();
        var metaData = rs.getMetaData();

        for (var i = 1; i <= metaData.getColumnCount(); i++) {
            row.put(metaData.getColumnLabel(i), rs.getObject(i));
        }

        return row;
    }
}