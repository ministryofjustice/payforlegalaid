package uk.gov.laa.gpfd.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.gov.laa.gpfd.exception.DatabaseReadException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class ReportViewsDaoTest {

    @Mock
    private JdbcTemplate writeJdbcTemplate;

    @InjectMocks
    private ReportViewsDao reportViewsDao;

    @Test
    void shouldRetrieveDataSuccessfully() {
        // Given
        String sqlQuery = "SELECT * FROM reports";
        List<Map<String, Object>> mockResult = List.of(Map.of("column1", "value1"));

        when(writeJdbcTemplate.queryForList(sqlQuery)).thenReturn(mockResult);

        // When
        List<Map<String, Object>> result = reportViewsDao.callDataBase(sqlQuery);

        // Then
        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "Result should contain one row");
        assertEquals("value1", result.get(0).get("column1"), "Row data should match");
        verify(writeJdbcTemplate, times(1)).queryForList(sqlQuery);
    }

    @Test
    void shouldThrowExceptionWhenResultIsEmpty() {
        String sqlQuery = "SELECT * FROM reports";
        when(writeJdbcTemplate.queryForList(sqlQuery)).thenReturn(Collections.emptyList());

        DatabaseReadException exception = assertThrows(DatabaseReadException.class,
                () -> reportViewsDao.callDataBase(sqlQuery));
        assertEquals("No results returned from query to MOJFIN reports database", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDataAccessFails() {
        String sqlQuery = "SELECT * FROM reports";
        when(writeJdbcTemplate.queryForList(sqlQuery)).thenThrow(new DataAccessException("Database error") {});

        DatabaseReadException exception = assertThrows(DatabaseReadException.class,
                () -> reportViewsDao.callDataBase(sqlQuery));
        assertEquals("Error reading from DB: uk.gov.laa.gpfd.dao.ReportViewsDaoTest$1: Database error", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenResultListIsNull() {
        String sqlQuery = "SELECT * FROM reports";
        when(writeJdbcTemplate.queryForList(sqlQuery)).thenReturn(null);

        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> reportViewsDao.callDataBase(sqlQuery));
        assertEquals("Cannot invoke \"java.util.List.isEmpty()\" because \"resultList\" is null", exception.getMessage());
    }

    @Test
    void shouldHandleInvalidQueryWithSyntaxError() {
        String sqlQuery = "INVALID SQL QUERY";
        when(writeJdbcTemplate.queryForList(sqlQuery)).thenThrow(new DataAccessException("Syntax error") {});

        DatabaseReadException exception = assertThrows(DatabaseReadException.class,
                () -> reportViewsDao.callDataBase(sqlQuery));
        assertEquals("Error reading from DB: uk.gov.laa.gpfd.dao.ReportViewsDaoTest$2: Syntax error", exception.getMessage());
    }

    @Test
    void shouldReturnMultipleRowsSuccessfully() {
        String sqlQuery = "SELECT * FROM reports";
        List<Map<String, Object>> mockResult = List.of(Map.of("column1", "value1"), Map.of("column1", "value2"));
        when(writeJdbcTemplate.queryForList(sqlQuery)).thenReturn(mockResult);

        List<Map<String, Object>> result = reportViewsDao.callDataBase(sqlQuery);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("value1", result.get(0).get("column1"));
        assertEquals("value2", result.get(1).get("column1"));
    }

    @Test
    void shouldHandleSpecialCharactersInResult() {
        String sqlQuery = "SELECT * FROM reports";
        List<Map<String, Object>> mockResult = List.of(Map.of("column1", "!@#$%^&*()"));
        when(writeJdbcTemplate.queryForList(sqlQuery)).thenReturn(mockResult);

        List<Map<String, Object>> result = reportViewsDao.callDataBase(sqlQuery);

        assertNotNull(result);
        assertEquals("!@#$%^&*()", result.get(0).get("column1"));
    }

    @Test
    void shouldThrowExceptionForInvalidDataType() {
        String sqlQuery = "SELECT * FROM reports";
        when(writeJdbcTemplate.queryForList(sqlQuery)).thenThrow(new ClassCastException("Cannot cast"));

        ClassCastException exception = assertThrows(ClassCastException.class,
                () -> reportViewsDao.callDataBase(sqlQuery));
        assertEquals("Cannot cast", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForEmptyQuery() {
        String sqlQuery = "";
        DatabaseReadException exception = assertThrows(DatabaseReadException.class,
                () -> reportViewsDao.callDataBase(sqlQuery));
        assertEquals("No results returned from query to MOJFIN reports database", exception.getMessage());
    }

    @Test
    void shouldRetrieveDataWithValidHeaders() {
        String sqlQuery = "SELECT * FROM reports";
        List<Map<String, Object>> mockResult = List.of(Map.of("column1", "value1"));
        when(writeJdbcTemplate.queryForList(sqlQuery)).thenReturn(mockResult);

        List<Map<String, Object>> result = reportViewsDao.callDataBase(sqlQuery);

        assertNotNull(result);
        assertEquals("value1", result.get(0).get("column1"));
    }

    @Test
    void shouldRetrieveDataWithSpecificParameters() {
        String sqlQuery = "SELECT * FROM reports WHERE report_name = 'Valid Report'";
        List<Map<String, Object>> mockResult = List.of(Map.of("column1", "valid"));
        when(writeJdbcTemplate.queryForList(sqlQuery)).thenReturn(mockResult);

        List<Map<String, Object>> result = reportViewsDao.callDataBase(sqlQuery);

        assertNotNull(result);
        assertEquals("valid", result.get(0).get("column1"));
    }

    @Test
    void shouldHandleLargeDataSetSuccessfully() {
        String sqlQuery = "SELECT * FROM reports";
        List<Map<String, Object>> mockResult = Collections.nCopies(1000, Map.of("column1", "value"));
        when(writeJdbcTemplate.queryForList(sqlQuery)).thenReturn(mockResult);

        List<Map<String, Object>> result = reportViewsDao.callDataBase(sqlQuery);

        assertEquals(1000, result.size());
    }

    @Test
    void shouldHandleSQLInjectionAttemptGracefully() {
        String sqlQuery = "SELECT * FROM reports WHERE name = 'DROP TABLE users;'";
        List<Map<String, Object>> mockResult = List.of(Map.of("column1", "value"));
        when(writeJdbcTemplate.queryForList(sqlQuery)).thenReturn(mockResult);

        List<Map<String, Object>> result = reportViewsDao.callDataBase(sqlQuery);

        assertNotNull(result);
        assertEquals("value", result.get(0).get("column1"));
    }

    @Test
    void shouldRetrieveLargeResultWithValidParameters() {
        String sqlQuery = "SELECT * FROM reports WHERE report_name = 'Large Report'";
        List<Map<String, Object>> mockResult = Collections.nCopies(500, Map.of("column1", "value"));
        when(writeJdbcTemplate.queryForList(sqlQuery)).thenReturn(mockResult);

        List<Map<String, Object>> result = reportViewsDao.callDataBase(sqlQuery);

        assertEquals(500, result.size());
    }

    @Test
    void shouldHandleSpecialCharactersInSQLQuery() {
        String sqlQuery = "SELECT * FROM reports WHERE name = 'Special$Character$'";
        List<Map<String, Object>> mockResult = List.of(Map.of("column1", "value"));
        when(writeJdbcTemplate.queryForList(sqlQuery)).thenReturn(mockResult);

        List<Map<String, Object>> result = reportViewsDao.callDataBase(sqlQuery);

        assertEquals("value", result.get(0).get("column1"));
    }
}