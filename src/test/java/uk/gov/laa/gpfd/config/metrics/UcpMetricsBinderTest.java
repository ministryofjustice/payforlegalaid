package uk.gov.laa.gpfd.config.metrics;

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import oracle.ucp.jdbc.JDBCConnectionPoolStatistics;
import oracle.ucp.jdbc.PoolDataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UcpMetricsBinderTest {

    private static final Tag MOCK_SOURCE_TAG = Tag.of("datasource", "mocking");

    @Test
    void shouldExposeBorrowedConnections() throws SQLException {
        var registry = new SimpleMeterRegistry();
        var poolDataSource = mock(PoolDataSource.class);

        when(poolDataSource.getBorrowedConnectionsCount()).thenReturn(3);

        new UcpMetricsBinder(poolDataSource, MOCK_SOURCE_TAG).bindTo(registry);

        var gauge = registry.find("ucp.connections.borrowed").gauge();
        assertNotNull(gauge);
        assertEquals(3, gauge.value());
    }

    @Test
    void shouldReturnNaNIfBorrowedConnectionCallErrors() throws SQLException {
        var registry = new SimpleMeterRegistry();
        var poolDataSource = mock(PoolDataSource.class);

        when(poolDataSource.getBorrowedConnectionsCount()).thenThrow(new SQLException("Error :("));

        new UcpMetricsBinder(poolDataSource, MOCK_SOURCE_TAG).bindTo(registry);

        var gauge = registry.find("ucp.connections.borrowed").gauge();
        assertNotNull(gauge);
        assertTrue(Double.isNaN(gauge.value()));
    }

    @Test
    void shouldExposeAvailableConnections() throws SQLException {
        var registry = new SimpleMeterRegistry();
        var poolDataSource = mock(PoolDataSource.class);

        when(poolDataSource.getAvailableConnectionsCount()).thenReturn(7);

        new UcpMetricsBinder(poolDataSource, MOCK_SOURCE_TAG).bindTo(registry);

        var gauge = registry.find("ucp.connections.available").gauge();
        assertNotNull(gauge);
        assertEquals(7, gauge.value());
    }

    @Test
    void shouldReturnNaNIfAvailableConnectionCallErrors() throws SQLException {
        var registry = new SimpleMeterRegistry();
        var poolDataSource = mock(PoolDataSource.class);

        when(poolDataSource.getAvailableConnectionsCount()).thenThrow(new SQLException("Error :("));

        new UcpMetricsBinder(poolDataSource, MOCK_SOURCE_TAG).bindTo(registry);

        var gauge = registry.find("ucp.connections.available").gauge();
        assertNotNull(gauge);
        assertTrue(Double.isNaN(gauge.value()));
    }

    @Test
    void shouldExposeTotalConnections() {
        var registry = new SimpleMeterRegistry();
        var poolDataSource = mock(PoolDataSource.class);
        var statistics = mock(JDBCConnectionPoolStatistics.class);

        when(poolDataSource.getStatistics()).thenReturn(statistics);
        when(statistics.getTotalConnectionsCount()).thenReturn(11);

        new UcpMetricsBinder(poolDataSource, MOCK_SOURCE_TAG).bindTo(registry);

        var gauge = registry.find("ucp.connections.total").gauge();
        assertNotNull(gauge);
        assertEquals(11, gauge.value());
    }

    @Test
    void shouldReturnNaNIfStatisticsNotDefinedYetForTotalConnections() {
        var registry = new SimpleMeterRegistry();
        var poolDataSource = mock(PoolDataSource.class);

        when(poolDataSource.getStatistics()).thenReturn(null);

        new UcpMetricsBinder(poolDataSource, MOCK_SOURCE_TAG).bindTo(registry);

        var gauge = registry.find("ucp.connections.total").gauge();
        assertNotNull(gauge);
        assertTrue(Double.isNaN(gauge.value()));
    }

    @Test
    void shouldExposeMaximumConnections() {
        var registry = new SimpleMeterRegistry();
        var poolDataSource = mock(PoolDataSource.class);

        when(poolDataSource.getMaxPoolSize()).thenReturn(101);

        new UcpMetricsBinder(poolDataSource, MOCK_SOURCE_TAG).bindTo(registry);

        var gauge = registry.find("ucp.connections.maximum").gauge();
        assertNotNull(gauge);
        assertEquals(101, gauge.value());
    }

    @Test
    void shouldExposePendingConnections() {
        var registry = new SimpleMeterRegistry();
        var poolDataSource = mock(PoolDataSource.class);
        var statistics = mock(JDBCConnectionPoolStatistics.class);

        when(poolDataSource.getStatistics()).thenReturn(statistics);
        when(statistics.getPendingRequestsCount()).thenReturn(3);

        new UcpMetricsBinder(poolDataSource, MOCK_SOURCE_TAG).bindTo(registry);

        var gauge = registry.find("ucp.connections.pending").gauge();
        assertNotNull(gauge);
        assertEquals(3, gauge.value());
    }

    @Test
    void shouldReturnNaNIfStatisticsNotDefinedYetForPendingConnections() {
        var registry = new SimpleMeterRegistry();
        var poolDataSource = mock(PoolDataSource.class);

        when(poolDataSource.getStatistics()).thenReturn(null);

        new UcpMetricsBinder(poolDataSource, MOCK_SOURCE_TAG).bindTo(registry);

        var gauge = registry.find("ucp.connections.pending").gauge();
        assertNotNull(gauge);
        assertTrue(Double.isNaN(gauge.value()));
    }

    @Test
    void shouldExposeCreatedConnections() {
        var registry = new SimpleMeterRegistry();
        var poolDataSource = mock(PoolDataSource.class);
        var statistics = mock(JDBCConnectionPoolStatistics.class);

        when(poolDataSource.getStatistics()).thenReturn(statistics);
        when(statistics.getConnectionsCreatedCount()).thenReturn(9);

        new UcpMetricsBinder(poolDataSource, MOCK_SOURCE_TAG).bindTo(registry);

        var gauge = registry.find("ucp.connections.created").gauge();
        assertNotNull(gauge);
        assertEquals(9, gauge.value());
    }

    @Test
    void shouldReturnNaNIfStatisticsNotDefinedYetForCreatedConnections() {
        var registry = new SimpleMeterRegistry();
        var poolDataSource = mock(PoolDataSource.class);

        when(poolDataSource.getStatistics()).thenReturn(null);

        new UcpMetricsBinder(poolDataSource, MOCK_SOURCE_TAG).bindTo(registry);

        var gauge = registry.find("ucp.connections.created").gauge();
        assertNotNull(gauge);
        assertTrue(Double.isNaN(gauge.value()));
    }

    @Test
    void shouldExposeClosedConnections() {
        var registry = new SimpleMeterRegistry();
        var poolDataSource = mock(PoolDataSource.class);
        var statistics = mock(JDBCConnectionPoolStatistics.class);

        when(poolDataSource.getStatistics()).thenReturn(statistics);
        when(statistics.getConnectionsClosedCount()).thenReturn(31);

        new UcpMetricsBinder(poolDataSource, MOCK_SOURCE_TAG).bindTo(registry);

        var gauge = registry.find("ucp.connections.closed").gauge();
        assertNotNull(gauge);
        assertEquals(31, gauge.value());
    }

    @Test
    void shouldReturnNaNIfStatisticsNotDefinedYetForClosedConnections() {
        var registry = new SimpleMeterRegistry();
        var poolDataSource = mock(PoolDataSource.class);

        when(poolDataSource.getStatistics()).thenReturn(null);

        new UcpMetricsBinder(poolDataSource, MOCK_SOURCE_TAG).bindTo(registry);

        var gauge = registry.find("ucp.connections.closed").gauge();
        assertNotNull(gauge);
        assertTrue(Double.isNaN(gauge.value()));
    }


    @Test
    void shouldExposeUtilisation() throws SQLException {
        var registry = new SimpleMeterRegistry();
        var poolDataSource = mock(PoolDataSource.class);

        when(poolDataSource.getBorrowedConnectionsCount()).thenReturn(3);
        when(poolDataSource.getMaxPoolSize()).thenReturn(6);

        new UcpMetricsBinder(poolDataSource, MOCK_SOURCE_TAG).bindTo(registry);

        var gauge = registry.find("ucp.connections.utilisation").gauge();
        assertNotNull(gauge);
        assertEquals(0.5, gauge.value());
    }

    @Test
    void shouldReturnNaNForUtilisationIfBorrowedCountErrors() throws SQLException {
        var registry = new SimpleMeterRegistry();
        var poolDataSource = mock(PoolDataSource.class);

        when(poolDataSource.getBorrowedConnectionsCount()).thenThrow(new SQLException("Error :("));

        new UcpMetricsBinder(poolDataSource, MOCK_SOURCE_TAG).bindTo(registry);

        var gauge = registry.find("ucp.connections.utilisation").gauge();
        assertNotNull(gauge);
        assertTrue(Double.isNaN(gauge.value()));
    }

    @Test
    void shouldReturnNaNForUtilisationIfMaxIsZero() throws SQLException {
        var registry = new SimpleMeterRegistry();
        var poolDataSource = mock(PoolDataSource.class);

        when(poolDataSource.getBorrowedConnectionsCount()).thenReturn(3);
        when(poolDataSource.getMaxPoolSize()).thenReturn(0);

        new UcpMetricsBinder(poolDataSource, MOCK_SOURCE_TAG).bindTo(registry);

        var gauge = registry.find("ucp.connections.utilisation").gauge();
        assertNotNull(gauge);
        assertTrue(Double.isNaN(gauge.value()));
    }

}