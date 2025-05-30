package uk.gov.laa.gpfd.dao.support;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResultSetToMapMapperTest {

    @Mock
    private ResultSet resultSet;

    @Mock
    private ResultSetMetaData metaData;

    private final ResultSetToMapMapper mapper = new ResultSetToMapMapper();

    @Test
    void shouldReturnMapWithColumnValues() throws SQLException {
        when(resultSet.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(3);

        when(metaData.getColumnLabel(1)).thenReturn("id");
        when(metaData.getColumnLabel(2)).thenReturn("name");
        when(metaData.getColumnLabel(3)).thenReturn("age");

        when(resultSet.getObject(1)).thenReturn(1L);
        when(resultSet.getObject(2)).thenReturn("Alice");
        when(resultSet.getObject(3)).thenReturn(30);

        var result = mapper.mapRow(resultSet, 1);

        assertEquals(3, result.size());
        assertEquals(1L, result.get("id"));
        assertEquals("Alice", result.get("name"));
        assertEquals(30, result.get("age"));
    }

    @Test
    void shouldPreserveColumnOrder() throws SQLException {
        when(resultSet.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(2);

        when(metaData.getColumnLabel(1)).thenReturn("first_name");
        when(metaData.getColumnLabel(2)).thenReturn("last_name");

        when(resultSet.getObject(1)).thenReturn("John");
        when(resultSet.getObject(2)).thenReturn("Doe");

        var result = mapper.mapRow(resultSet, 1);

        assertInstanceOf(LinkedHashMap.class, result, "Should preserve insertion order");
        assertEquals("John", ((LinkedHashMap<?, ?>) result).values().toArray()[0]);
        assertEquals("Doe", ((LinkedHashMap<?, ?>) result).values().toArray()[1]);
    }

    @Test
    void shouldThrowExceptionWhenResultSetIsInvalid() throws SQLException {
        when(resultSet.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(1);
        when(metaData.getColumnLabel(1)).thenReturn("test");
        when(resultSet.getObject(1)).thenThrow(new SQLException("DB error"));

        assertThrows(SQLException.class, () -> mapper.mapRow(resultSet, 1));
    }

    @Test
    void shouldHandleNullValues() throws SQLException {
        when(resultSet.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(1);
        when(metaData.getColumnLabel(1)).thenReturn("nullable_column");
        when(resultSet.getObject(1)).thenReturn(null);

        var result = mapper.mapRow(resultSet, 1);

        assertNull(result.get("nullable_column"));
    }
}