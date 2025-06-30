package uk.gov.laa.gpfd.dao.sql.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class QueryTimeoutPolicyTest {

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Test
    void constructor_WithPositiveTimeout_InitializesCorrectly() {
        var policy = new QueryTimeoutPolicy(30);
        assertEquals(30, policy.timeoutSeconds());
    }

    @Test
    void constructor_WithZeroTimeout_InitializesCorrectly() {
        var policy = new QueryTimeoutPolicy(0);
        assertEquals(0, policy.timeoutSeconds());
    }

    @Test
    void configure_WithValidStatementAndPositiveTimeout_SetsTimeout() throws SQLException {
        var policy = new QueryTimeoutPolicy(15);
        policy.configure(mockPreparedStatement);
        verify(mockPreparedStatement).setQueryTimeout(15);
    }

    @Test
    void configure_WithZeroTimeout_SetsZeroTimeout() throws SQLException {
        var policy = new QueryTimeoutPolicy(0);
        policy.configure(mockPreparedStatement);
        verify(mockPreparedStatement).setQueryTimeout(0);
    }

    @Test
    void configure_WithNullStatement_ThrowsIllegalArgumentException() {
        var policy = new QueryTimeoutPolicy(10);
        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> policy.configure(null)
        );
        assertEquals("PreparedStatement cannot be null", exception.getMessage());
    }

    @Test
    void configure_WithNegativeTimeout_ThrowsIllegalStateException() {
        var policy = new QueryTimeoutPolicy(-1);
        var exception = assertThrows(
                IllegalStateException.class,
                () -> policy.configure(mockPreparedStatement)
        );
        assertEquals("Timeout value cannot be negative", exception.getMessage());
        verifyNoInteractions(mockPreparedStatement);
    }

    @Test
    void configure_WhenSQLExceptionOccurs_PropagatesException() throws SQLException {
        var policy = new QueryTimeoutPolicy(20);
        doThrow(new SQLException("Invalid timeout value")).when(mockPreparedStatement).setQueryTimeout(anyInt());

        var exception = assertThrows(
                SQLException.class,
                () -> policy.configure(mockPreparedStatement)
        );
        assertEquals("Invalid timeout value", exception.getMessage());
    }

    @Test
    void recordComponents_VerifyImmutabilityAndBehavior() {
        var policy1 = new QueryTimeoutPolicy(10);
        var policy2 = new QueryTimeoutPolicy(10);
        var policy3 = new QueryTimeoutPolicy(20);

        assertEquals(policy1, policy2);
        assertNotEquals(policy1, policy3);
        assertEquals(policy1.hashCode(), policy2.hashCode());
        assertNotEquals(policy1.hashCode(), policy3.hashCode());
        assertEquals("QueryTimeoutPolicy[timeoutSeconds=10]", policy1.toString());
    }

}