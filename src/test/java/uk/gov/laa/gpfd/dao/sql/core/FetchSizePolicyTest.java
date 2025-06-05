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

@ExtendWith(MockitoExtension.class)
class FetchSizePolicyTest {

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Test
    void constructor_WithPositiveFetchSize_InitializesCorrectly() {
        var policy = new FetchSizePolicy(100);
        assertEquals(100, policy.fetchSize());
    }

    @Test
    void constructor_WithZeroFetchSize_InitializesCorrectly() {
        var policy = new FetchSizePolicy(0);
        assertEquals(0, policy.fetchSize());
    }

    @Test
    void configure_WithValidStatement_SetsFetchSize() throws SQLException {
        var policy = new FetchSizePolicy(50);
        policy.configure(mockPreparedStatement);
        verify(mockPreparedStatement).setFetchSize(50);
    }

    @Test
    void configure_WithZeroFetchSize_SetsZeroFetchSize() throws SQLException {
        var policy = new FetchSizePolicy(0);
        policy.configure(mockPreparedStatement);
        verify(mockPreparedStatement).setFetchSize(0);
    }

    @Test
    void configure_WithNullStatement_ThrowsIllegalArgumentException() {
        var policy = new FetchSizePolicy(100);
        assertThrows(IllegalArgumentException.class, () -> policy.configure(null));
    }

    @Test
    void configure_WhenSQLExceptionOccurs_PropagatesException() throws SQLException {
        var policy = new FetchSizePolicy(100);
        doThrow(new SQLException("DB error")).when(mockPreparedStatement).setFetchSize(anyInt());

        assertThrows(SQLException.class, () -> policy.configure(mockPreparedStatement));
    }

    @Test
    void recordComponents_VerifyImmutability() {
        var policy1 = new FetchSizePolicy(100);
        var policy2 = new FetchSizePolicy(100);
        var policy3 = new FetchSizePolicy(200);

        assertEquals(policy1, policy2);
        assertNotEquals(policy1, policy3);
        assertEquals(policy1.hashCode(), policy2.hashCode());
        assertNotEquals(policy1.hashCode(), policy3.hashCode());
        assertEquals("FetchSizePolicy[fetchSize=100]", policy1.toString());
    }
}