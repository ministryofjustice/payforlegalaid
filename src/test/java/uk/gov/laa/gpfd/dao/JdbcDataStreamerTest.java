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
import static uk.gov.laa.gpfd.data.ReportsTestDataFactory.createTestReport;
import static uk.gov.laa.gpfd.data.ReportsTestDataFactory.createTestReportWithQuery;

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
    void shouldThrowIllegalArgumentExceptionWhenOutputStreamIsNull() {
        var testReport = createTestReport();
        assertThrows(IllegalArgumentException.class, () -> jdbcDataStreamer.stream(testReport, null));
    }

    @Test
    void shouldExecuteQueryWhenValidParametersProvided() {
        var testReport = createTestReportWithQuery();
        var outputStream = new ByteArrayOutputStream();

        jdbcDataStreamer.stream(testReport, outputStream);

        verify(jdbcOperations).query(eq("SELECT * FROM ANY_REPORT.DATA"), any(RowCallbackHandler.class));
    }

    @Test
    void shouldThrowRuntimeExceptionWhenDatabaseAccessFails() {
        var testReport = createTestReportWithQuery();
        var outputStream = new ByteArrayOutputStream();

        doThrow(new RuntimeException("DB error")).when(jdbcOperations).query(anyString(), any(RowCallbackHandler.class));

        assertThrows(RuntimeException.class, () -> jdbcDataStreamer.stream(testReport, outputStream));
    }

}