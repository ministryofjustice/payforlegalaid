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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        // Our requirements currently are that statements are Forward Only/Read Only with a timeout and fetch size set
        var statementPolicy = new StatementPolicy(1000, 30);
        when(mockConnection.prepareStatement(any(), anyInt(), anyInt())).thenReturn(mockPreparedStatement);

        var statementCreator = statementPolicy.createStatementCreator("SELECT * FROM test");
        statementCreator.createPreparedStatement(mockConnection);

        verify(mockPreparedStatement).setQueryTimeout(30);
        verify(mockPreparedStatement).setFetchSize(1000);
        verify(mockConnection).prepareStatement(
                "SELECT * FROM test",
                ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY);

    }

    @SneakyThrows
    @Test
    void shouldBuildStatementCreatorWithZeroValues() {
        var statementPolicy = new StatementPolicy(0, 0);
        when(mockConnection.prepareStatement(any(), anyInt(), anyInt())).thenReturn(mockPreparedStatement);

        var statementCreator = statementPolicy.createStatementCreator("SELECT * FROM test");
        statementCreator.createPreparedStatement(mockConnection);

        verify(mockPreparedStatement).setQueryTimeout(0);
        verify(mockPreparedStatement).setFetchSize(0);

    }

    @Test
    void shouldPropagateExceptionIfThrownByFetchSizePolicySetting() throws SQLException {
        var statementPolicy = new StatementPolicy(-4324, 30);
        var statementCreator = statementPolicy.createStatementCreator("SELECT * FROM test");
        when(mockConnection.prepareStatement(any(), anyInt(), anyInt())).thenReturn(mockPreparedStatement);

        doThrow(new SQLException("DB error")).when(mockPreparedStatement).setFetchSize(anyInt());
        assertThrows(SQLException.class, () -> statementCreator.createPreparedStatement(mockConnection));
    }

    @Test
    void shouldPropagateExceptionIfThrownByQuerySetting() throws SQLException {
        var statementPolicy = new StatementPolicy(12, 30);
        var statementCreator = statementPolicy.createStatementCreator("SELECT * FROM test");
        when(mockConnection.prepareStatement(any(), anyInt(), anyInt())).thenReturn(mockPreparedStatement);

        doThrow(new SQLException("DB error")).when(mockPreparedStatement).setQueryTimeout(anyInt());
        assertThrows(SQLException.class, () -> statementCreator.createPreparedStatement(mockConnection));
    }

    @Test
    void shouldPropagateExceptionIfThrownByConnection() throws SQLException {
        var statementPolicy = new StatementPolicy(-4324, 30);
        var statementCreator = statementPolicy.createStatementCreator("SELECT * FROM test");
        when(mockConnection.prepareStatement(any(), anyInt(), anyInt())).thenThrow(new SQLException("DB error"));

        assertThrows(SQLException.class, () -> statementCreator.createPreparedStatement(mockConnection));
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