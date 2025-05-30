package uk.gov.laa.gpfd.dao.stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import uk.gov.laa.gpfd.model.ReportQuerySql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.laa.gpfd.exception.DatabaseReadException.DatabaseFetchException;

import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class AbstractStreamingDaoTest {

    @Mock
    private JdbcOperations jdbcOperations;

    @Mock
    private RowMapper<String> rowMapper;

    @Mock
    private Stream<String> mockStream;

    private AbstractStreamingDao<String> testDao;

    @BeforeEach
    void setUp() {
        testDao = new TestStreamingDao(jdbcOperations, rowMapper);
    }

    @Test
    void shouldThrowNullPointerExceptionWhenJdbcOperationsIsNull() {
        assertThrows(NullPointerException.class, () -> new TestStreamingDao(null, rowMapper));
    }

    @Test
    void shouldThrowNullPointerExceptionWhenRowMapperIsNull() {
        assertThrows(NullPointerException.class, () -> new TestStreamingDao(jdbcOperations, null));
    }

    @Test
    void shouldExecuteQueryWithSqlOnly() {
        var rawSql = "SELECT * FROM ANY_REPORT.test";
        var sql = ReportQuerySql.of(rawSql);
        when(jdbcOperations.queryForStream(rawSql, rowMapper)).thenReturn(mockStream);

        var result = testDao.queryForStream(sql);

        assertEquals(mockStream, result);
        verify(jdbcOperations).queryForStream(rawSql, rowMapper);
    }

    @Test
    void shouldExecuteQueryWithParams() {
        var rawSql = "SELECT * FROM ANY_REPORT.test WHERE id = ?";
        var sql = ReportQuerySql.of(rawSql);
        var params = new Object[]{1};
        when(jdbcOperations.queryForStream(rawSql, rowMapper, params)).thenReturn(mockStream);

        var result = testDao.queryForStream(sql, params);

        assertEquals(mockStream, result);
        verify(jdbcOperations).queryForStream(rawSql, rowMapper, params);
    }

    @Test
    void shouldWrapDataAccessException() {
        var rawSql = "SELECT * FROM ANY_REPORT.test";
        var sql = ReportQuerySql.of(rawSql);
        var originalException = new DataAccessException("Test exception") {};
        when(jdbcOperations.queryForStream(rawSql, rowMapper)).thenThrow(originalException);

        var thrown = assertThrows(DatabaseFetchException.class, () -> testDao.queryForStream(sql));

        assertEquals("bad SQL grammar: " + sql, thrown.getMessage());
        assertEquals(originalException, thrown.getCause());
    }

    @Test
    void shouldWrapDataAccessExceptionWhenCallingWithParams() {
        var rawSql = "SELECT * FROM ANY_REPORT.test WHERE id = ?";
        var sql = ReportQuerySql.of(rawSql);
        var params = new Object[]{1};
        var originalException = new DataAccessException("Test exception") {};
        when(jdbcOperations.queryForStream(rawSql, rowMapper, params)).thenThrow(originalException);

        var thrown = assertThrows(DatabaseFetchException.class, () -> testDao.queryForStream(sql, params));

        assertEquals("bad SQL grammar: " + sql, thrown.getMessage());
        assertEquals(originalException, thrown.getCause());
    }

    private static class TestStreamingDao extends AbstractStreamingDao<String> {
        public TestStreamingDao(JdbcOperations jdbcOperations, RowMapper<String> rowMapper) {
            super(jdbcOperations, rowMapper);
        }
    }
}