package uk.gov.laa.gpfd.dao.stream;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import uk.gov.laa.gpfd.dao.support.DirectToExcelRowMapper;
import uk.gov.laa.gpfd.exception.DatabaseReadException;
import uk.gov.laa.gpfd.model.ReportQuerySql;
import uk.gov.laa.gpfd.services.excel.editor.CellValueSetter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * An abstract base implementation of {@link StreamingDao} that provides common streaming functionality
 * using Spring's {@link JdbcOperations}.
 * <p>
 * This abstract class handles the core streaming operations while delegating the actual row mapping
 * to a provided {@link RowMapper} implementation. It provides consistent error handling and logging
 * for all concrete implementations.
 *
 * @param <T> the type of objects to be returned by the stream
 * @see StreamingDao
 * @see JdbcOperations
 * @see RowMapper
 */
@Slf4j
public abstract class AbstractStreamingDao<T> implements StreamingDao<T> {
    protected final JdbcOperations jdbcOperations;
    protected final RowMapper<T> rowMapper;

    protected AbstractStreamingDao(JdbcOperations jdbcOperations, RowMapper<T> rowMapper) {
        this.jdbcOperations = Objects.requireNonNull(jdbcOperations);
        this.rowMapper = Objects.requireNonNull(rowMapper);
    }

    @Override
    public Stream<T> queryForStream(ReportQuerySql sql) {
        return executeQuery(() -> jdbcOperations.queryForStream(sql.value(), rowMapper), sql);
    }

    @Override
    public Stream<T> queryForStream(ReportQuerySql sql, Object... params) {
        return executeQuery(() -> jdbcOperations.queryForStream(sql.value(), rowMapper, params), sql);
    }

    private Stream<T> executeQuery(Supplier<Stream<T>> querySupplier, ReportQuerySql sql) {
        try {
            log.debug("Executing query: {}", sql);
            return querySupplier.get();
        } catch (DataAccessException e) {
            throw new DatabaseReadException.DatabaseFetchException("bad SQL grammar: %s".formatted(sql), e);
        }
    }

    public void queryForExcelStream(ReportQuerySql sql,
                                            Sheet sheet,
                                            List<Pair<String, String>> fieldAttributes,
                                            CellValueSetter cellValueSetter) {
        var mapper = new DirectToExcelRowMapper(sheet, fieldAttributes, cellValueSetter);
        jdbcOperations.query(con -> {
            var ps = con.prepareStatement(
                    sql.value(),
                    ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY
            );
            ps.setFetchSize(1_000);
            return ps;
        }, mapper);
    }

}
