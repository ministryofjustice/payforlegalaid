package uk.gov.laa.gpfd.dao.sql.core;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatementPolicyTest {

    @Mock
    Connection mockConnection;

    @Mock
    PreparedStatement mockPreparedStatement;

    @SneakyThrows
    @BeforeEach
    void beforeEach() {
        reset(mockConnection, mockPreparedStatement);
    }

    @SneakyThrows
    @Test
    void shouldBuildStatementCreatorWhichCanGenerateCorrectStatements() {
        var statementPolicy = new StatementPolicy(1000, 30);

        doReturn(mockPreparedStatement)
                .when(mockConnection)
                .prepareStatement(
                        any(),
                        ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_READ_ONLY
                );

        var statementCreator =
                statementPolicy.createStatementCreator("SELECT * FROM test");

        statementCreator.createPreparedStatement(mockConnection);

        verify(mockPreparedStatement).setQueryTimeout(30);
        verify(mockPreparedStatement).setFetchSize(1000);

        verify(mockConnection).prepareStatement(
                "SELECT * FROM test",
                ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY
        );
    }

    @SneakyThrows
    @Test
    void shouldBuildStatementCreatorWithZeroValues() {
        var statementPolicy = new StatementPolicy(0, 0);

        doReturn(mockPreparedStatement)
                .when(mockConnection)
                .prepareStatement(
                        any(),
                        ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_READ_ONLY
                );

        var statementCreator =
                statementPolicy.createStatementCreator("SELECT * FROM test");

        statementCreator.createPreparedStatement(mockConnection);

        verify(mockPreparedStatement).setQueryTimeout(0);
        verify(mockPreparedStatement).setFetchSize(0);
    }

    @Test
    void shouldPropagateExceptionIfThrownByFetchSizePolicySetting() throws SQLException {
        var statementPolicy = new StatementPolicy(-4324, 30);
        var statementCreator = statementPolicy.createStatementCreator("SELECT * FROM test");

        doReturn(mockPreparedStatement)
                .when(mockConnection)
                .prepareStatement(
                        any(),
                        ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_READ_ONLY
                );

        doThrow(new SQLException("DB error"))
                .when(mockPreparedStatement)
                .setFetchSize(anyInt());

        assertThrows(
                SQLException.class,
                () -> statementCreator.createPreparedStatement(mockConnection)
        );
    }

    @Test
    void shouldPropagateExceptionIfThrownByQuerySetting() throws SQLException {
        var statementPolicy = new StatementPolicy(12, 30);
        var statementCreator = statementPolicy.createStatementCreator("SELECT * FROM test");

        doReturn(mockPreparedStatement)
                .when(mockConnection)
                .prepareStatement(
                        any(),
                        ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_READ_ONLY
                );

        doThrow(new SQLException("DB error"))
                .when(mockPreparedStatement)
                .setQueryTimeout(anyInt());

        assertThrows(
                SQLException.class,
                () -> statementCreator.createPreparedStatement(mockConnection)
        );
    }

    @Test
    void shouldPropagateExceptionIfThrownByConnection() throws SQLException {
        // given
        var statementPolicy = new StatementPolicy(-4324, 30);
        var statementCreator =
                statementPolicy.createStatementCreator("SELECT * FROM test");

        doThrow(new SQLException("DB error"))
                .when(mockConnection)
                .prepareStatement(any(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

        // when / then
        assertThrows(
                SQLException.class,
                () -> statementCreator.createPreparedStatement(mockConnection)
        );
    }

    @Test
    void shouldThrowExceptionIfTimeoutNegative() {
        assertThrows(IllegalStateException.class, () -> new StatementPolicy(3123, -3));
    }

    @Test
    void shouldThrowExceptionIfConnectionNull() {
        var statementPolicy = new StatementPolicy(12, 30);
        var statementCreator = statementPolicy.createStatementCreator("SELECT * FROM test");

        assertThrows(IllegalArgumentException.class, () -> statementCreator.createPreparedStatement(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    @NullSource
    void shouldThrowExceptionIfInvalidSql(String sql) {
        var statementPolicy = new StatementPolicy(12, 30);

        var exception = assertThrows(IllegalArgumentException.class, () -> statementPolicy.createStatementCreator(sql));

        assertEquals("SQL statement cannot be null or empty", exception.getMessage());
    }
}