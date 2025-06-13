package uk.gov.laa.gpfd.dao;

import org.apache.poi.ss.usermodel.Sheet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import uk.gov.laa.gpfd.model.Mapping;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JdbcWorkbookDataStreamerTest {

    @Mock
    private JdbcOperations jdbcOperations;

    @Mock
    private Sheet mockSheet;

    @Mock
    private Mapping mockMapping;

    private static class TestStreamer extends JdbcWorkbookDataStreamer {
        public TestStreamer(JdbcOperations jdbcOperations) {
            super(jdbcOperations);
        }

        @Override
        protected String getSql(Mapping mapping) {
            return "SELECT * FROM test_table";
        }

        @Override
        protected PreparedStatementCreator createStatementCreator(String sql) {
            return connection -> connection.prepareStatement(sql);
        }

        @Override
        protected RowCallbackHandler createRowCallbackHandler(Sheet sheet, Mapping mapping) {
            return rs -> {}; // No-op handler for testing
        }
    }

    @Test
    void queryToSheet_shouldExecuteQueryWithCorrectParameters() {
        var streamer = new TestStreamer(jdbcOperations);
        streamer.queryToSheet(mockSheet, mockMapping);

        verify(jdbcOperations).query(any(PreparedStatementCreator.class), any(RowCallbackHandler.class));
    }

    @Test
    void queryToSheet_shouldCallGetSqlWithMapping() {
        var streamer = spy(new TestStreamer(jdbcOperations));
        streamer.queryToSheet(mockSheet, mockMapping);

        verify(streamer).getSql(mockMapping);
    }

    @Test
    void queryToSheet_shouldCallCreateStatementCreatorWithSql() {
        TestStreamer streamer = spy(new TestStreamer(jdbcOperations));
        when(streamer.getSql(mockMapping)).thenReturn("TEST_SQL");

        streamer.queryToSheet(mockSheet, mockMapping);

        verify(streamer).createStatementCreator("TEST_SQL");
    }

    @Test
    void queryToSheet_shouldCallCreateRowCallbackHandlerWithSheetAndMapping() {
        var streamer = spy(new TestStreamer(jdbcOperations));

        streamer.queryToSheet(mockSheet, mockMapping);

        verify(streamer).createRowCallbackHandler(mockSheet, mockMapping);
    }

    @Test
    void getJdbcOperations_shouldReturnConstructorParameter() {
        var streamer = new TestStreamer(jdbcOperations);
        assertSame(jdbcOperations, streamer.jdbcOperations);
    }
}