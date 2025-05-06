package uk.gov.laa.gpfd.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JdbcDataStreamerTest {

    @Mock
    private JdbcOperations jdbcOperations;

    @InjectMocks
    private JdbcDataStreamer jdbcDataStreamer;

    @Test
    void shouldThrowIllegalArgumentExceptionWhenSqlIsNull() {
        var outputStream = new ByteArrayOutputStream();

        assertThrows(IllegalArgumentException.class, () -> jdbcDataStreamer.stream(null, outputStream));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenSqlIsBlank() {
        var outputStream = new ByteArrayOutputStream();

        assertThrows(IllegalArgumentException.class, () -> jdbcDataStreamer.stream("   ", outputStream));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenOutputStreamIsNull() {
        assertThrows(IllegalArgumentException.class, () -> jdbcDataStreamer.stream("SELECT * FROM table", null));
    }

    @Test
    void shouldExecuteQueryWhenValidParametersProvided() {
        var testSql = "SELECT * FROM test";
        var outputStream = new ByteArrayOutputStream();

        jdbcDataStreamer.stream(testSql, outputStream);

        verify(jdbcOperations).query(eq(testSql), any(RowCallbackHandler.class));
    }

    @Test
    void shouldThrowRuntimeExceptionWhenDatabaseAccessFails() {
        var testSql = "SELECT * FROM table";
        var outputStream = new ByteArrayOutputStream();

        doThrow(new RuntimeException("DB error")).when(jdbcOperations).query(anyString(), any(RowCallbackHandler.class));

        assertThrows(RuntimeException.class, () -> jdbcDataStreamer.stream(testSql, outputStream));
    }

}