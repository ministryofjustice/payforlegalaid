package uk.gov.laa.gpfd.dao.sql;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import uk.gov.laa.gpfd.dao.sql.ValueExtractor.HeaderValueExtractor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.jdbc.support.JdbcUtils.getResultSetValue;
import static org.springframework.jdbc.support.JdbcUtils.lookupColumnName;
import static uk.gov.laa.gpfd.dao.sql.ValueExtractor.RowValueExtractor;
import static uk.gov.laa.gpfd.dao.sql.ValueExtractor.ofHeader;
import static uk.gov.laa.gpfd.dao.sql.ValueExtractor.ofRow;

class ValueExtractorTest {

    @Test
    @SneakyThrows
    void shouldCreatesHeaderExtractor() {
        var metaData = mock(ResultSetMetaData.class);
        var extractor = ofHeader(metaData);

        assertInstanceOf(HeaderValueExtractor.class, extractor);
    }

    @Test
    @SneakyThrows
    void shouldThrowsNpeForNullMetadata() {
        assertThrows(NullPointerException.class, () -> ofHeader(null));
    }

    @Test
    @SneakyThrows
    void shouldCreatesRowExtractor() {
        var resultSet = mock(ResultSet.class);
        var extractor = ofRow(resultSet);

        assertInstanceOf(RowValueExtractor.class, extractor);
    }

    @Test
    @SneakyThrows
    void shouldThrowsNpeForNullResultSet() {
        assertThrows(NullPointerException.class, () -> ofRow(null));
    }

    @Test
    @SneakyThrows
    void shouldExtractsColumnNames() {
        var metaData = mock(ResultSetMetaData.class);
        when(metaData.getColumnCount()).thenReturn(2);
        when(lookupColumnName(metaData, 1)).thenReturn("id");
        when(lookupColumnName(metaData, 2)).thenReturn("name");

        var extractor = new HeaderValueExtractor(metaData);

        assertEquals("id", extractor.extract(1));
        assertEquals("name", extractor.extract(2));
    }

    @Test
    @SneakyThrows
    void shouldPropagatesSqlException() {
        var metaData = mock(ResultSetMetaData.class);
        when(lookupColumnName(metaData, 1)).thenThrow(new SQLException("Column not found"));

        var extractor = new HeaderValueExtractor(metaData);

        assertThrows(SQLException.class, () -> extractor.extract(1));
    }

    @Test
    @SneakyThrows
    void shouldExtractsValues() {
        var resultSet = mock(ResultSet.class);
        when(getResultSetValue(resultSet, 1)).thenReturn(123);
        when(getResultSetValue(resultSet, 2)).thenReturn("test");
        when(getResultSetValue(resultSet, 3)).thenReturn(null);

        var extractor = new RowValueExtractor(resultSet);

        assertEquals("123", extractor.extract(1));
        assertEquals("test", extractor.extract(2));
        assertEquals("", extractor.extract(3)); // null becomes empty string
    }

    @Test
    @SneakyThrows
    void shouldHandlesSqlException() {
        var resultSet = mock(ResultSet.class);
        when(getResultSetValue(resultSet, 1)).thenThrow(new SQLException("Invalid column"));

        var extractor = new RowValueExtractor(resultSet);

        assertThrows(SQLException.class, () -> extractor.extract(1));
    }

    @Test
    @SneakyThrows
    void shouldHeaderValueExtractorHandleInvalidColumnIndex() {
        var metaData = mock(ResultSetMetaData.class);
        when(metaData.getColumnCount()).thenReturn(1);
        when(lookupColumnName(metaData, 1)).thenReturn("valid");
        when(lookupColumnName(metaData, 2)).thenThrow(new SQLException("Invalid column"));

        var extractor = new HeaderValueExtractor(metaData);

        assertThrows(SQLException.class, () -> extractor.extract(2));
    }

    @Test
    @SneakyThrows
    void shouldRowValueExtractorHandleInvalidColumnIndex() {
        var resultSet = mock(ResultSet.class);
        when(getResultSetValue(resultSet, 0)).thenThrow(new SQLException("Invalid column"));

        var extractor = new RowValueExtractor(resultSet);

        assertThrows(SQLException.class, () -> extractor.extract(0));
    }
}