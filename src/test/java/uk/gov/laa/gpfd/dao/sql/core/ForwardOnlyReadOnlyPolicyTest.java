package uk.gov.laa.gpfd.dao.sql.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ForwardOnlyReadOnlyPolicyTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    private static final String VALID_SQL = "SELECT * FROM table";

    @Test
    void createStatement_WithValidParameters_CreatesForwardOnlyReadOnlyStatement() throws SQLException {
        var policy = new ForwardOnlyReadOnlyPolicy();
        when(mockConnection.prepareStatement(
                        VALID_SQL,
                        ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_READ_ONLY))
                .thenReturn(mockPreparedStatement);

        var result = policy.createStatement(mockConnection, VALID_SQL);

        assertSame(mockPreparedStatement, result);
        verify(mockConnection).prepareStatement(
                VALID_SQL,
                ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY);
    }

    @Test
    void createStatement_WithNullConnection_ThrowsIllegalArgumentException() {
        var policy = new ForwardOnlyReadOnlyPolicy();

        var exception = assertThrows(IllegalArgumentException.class,
                () -> policy.createStatement(null, VALID_SQL));

        assertEquals("Connection cannot be null", exception.getMessage());
    }

    @Test
    void createStatement_WithNullSql_ThrowsIllegalArgumentException() {
        var policy = new ForwardOnlyReadOnlyPolicy();

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> policy.createStatement(mockConnection, null));

        assertEquals("SQL statement cannot be null or empty", exception.getMessage());
    }

    @Test
    void createStatement_WithEmptySql_ThrowsIllegalArgumentException() {
        var policy = new ForwardOnlyReadOnlyPolicy();

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> policy.createStatement(mockConnection, ""));

        assertEquals("SQL statement cannot be null or empty", exception.getMessage());
    }

    @Test
    void createStatement_WithBlankSql_ThrowsIllegalArgumentException() {
        var policy = new ForwardOnlyReadOnlyPolicy();

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> policy.createStatement(mockConnection, "   "));

        assertEquals("SQL statement cannot be null or empty", exception.getMessage());
    }

    @Test
    void createStatement_WhenSQLExceptionOccurs_PropagatesException() throws SQLException {
        var policy = new ForwardOnlyReadOnlyPolicy();
        when(mockConnection.prepareStatement(
                anyString(),
                anyInt(),
                anyInt()))
                .thenThrow(new SQLException("Database error"));

        var exception = assertThrows(
                SQLException.class,
                () -> policy.createStatement(mockConnection, VALID_SQL));

        assertEquals("Database error", exception.getMessage());
    }

    @Test
    void class_ShouldBeNonSealed_ToAllowExtension() {
        class TestPolicy extends ForwardOnlyReadOnlyPolicy {
            // Test subclass
        }

        assertDoesNotThrow(TestPolicy::new);
    }
}