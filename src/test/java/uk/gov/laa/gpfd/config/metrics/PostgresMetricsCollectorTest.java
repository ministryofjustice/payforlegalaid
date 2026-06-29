package uk.gov.laa.gpfd.config.metrics;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static uk.gov.laa.gpfd.config.metrics.PostgresMetricsCollector.SQL_ACTIVE_CONNECTIONS;
import static uk.gov.laa.gpfd.config.metrics.PostgresMetricsCollector.SQL_COMMITTED_TRANSACTIONS;
import static uk.gov.laa.gpfd.config.metrics.PostgresMetricsCollector.SQL_IDLE_CONNECTIONS;
import static uk.gov.laa.gpfd.config.metrics.PostgresMetricsCollector.SQL_MAX_CONNECTIONS;
import static uk.gov.laa.gpfd.config.metrics.PostgresMetricsCollector.SQL_ROLLBACK_TRANSACTIONS;
import static uk.gov.laa.gpfd.config.metrics.PostgresMetricsCollector.SQL_TOTAL_CONNECTIONS;

@ExtendWith(MockitoExtension.class)
class PostgresMetricsCollectorTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void before() {
        reset(jdbcTemplate);
        when(jdbcTemplate.queryForObject(SQL_TOTAL_CONNECTIONS, Integer.class)).thenReturn(23);
        when(jdbcTemplate.queryForObject(SQL_ACTIVE_CONNECTIONS, Integer.class)).thenReturn(5);
        when(jdbcTemplate.queryForObject(SQL_IDLE_CONNECTIONS, Integer.class)).thenReturn(13);
        when(jdbcTemplate.queryForObject(SQL_MAX_CONNECTIONS, Integer.class)).thenReturn(1234);
        when(jdbcTemplate.queryForObject(SQL_COMMITTED_TRANSACTIONS, Integer.class)).thenReturn(986421);
        when(jdbcTemplate.queryForObject(SQL_ROLLBACK_TRANSACTIONS, Integer.class)).thenReturn(79);

    }

    @Test
    void shouldExposeTotalConnections() {
        var registry = new SimpleMeterRegistry();

        var metrics = new PostgresMetricsCollector(jdbcTemplate);
        metrics.pollTrackingDbMetrics();
        metrics.bindTo(registry);

        var gauge = registry.find("db.tracking.connections.total").gauge();
        assertNotNull(gauge);
        assertEquals(23, gauge.value());
    }

    @Test
    void shouldZeroIfErrorFetchingTotalConnections() {
        var registry = new SimpleMeterRegistry();
        when(jdbcTemplate.queryForObject(SQL_TOTAL_CONNECTIONS, Integer.class)).thenReturn(null);

        var metrics = new PostgresMetricsCollector(jdbcTemplate);
        metrics.pollTrackingDbMetrics();
        metrics.bindTo(registry);

        var gauge = registry.find("db.tracking.connections.total").gauge();
        assertNotNull(gauge);
        assertEquals(0, gauge.value());
    }

    @Test
    void shouldExposeActiveConnections() {
        var registry = new SimpleMeterRegistry();

        var metrics = new PostgresMetricsCollector(jdbcTemplate);
        metrics.pollTrackingDbMetrics();
        metrics.bindTo(registry);

        var gauge = registry.find("db.tracking.connections.active").gauge();
        assertNotNull(gauge);
        assertEquals(5, gauge.value());
    }

    @Test
    void shouldZeroIfErrorFetchingActiveConnections() {
        var registry = new SimpleMeterRegistry();
        when(jdbcTemplate.queryForObject(SQL_ACTIVE_CONNECTIONS, Integer.class)).thenReturn(null);

        var metrics = new PostgresMetricsCollector(jdbcTemplate);
        metrics.pollTrackingDbMetrics();
        metrics.bindTo(registry);

        var gauge = registry.find("db.tracking.connections.active").gauge();
        assertNotNull(gauge);
        assertEquals(0, gauge.value());
    }

    @Test
    void shouldExposeIdleConnections() {
        var registry = new SimpleMeterRegistry();

        var metrics = new PostgresMetricsCollector(jdbcTemplate);
        metrics.pollTrackingDbMetrics();
        metrics.bindTo(registry);

        var gauge = registry.find("db.tracking.connections.idle").gauge();
        assertNotNull(gauge);
        assertEquals(13, gauge.value());
    }

    @Test
    void shouldZeroIfErrorFetchingIdleConnections() {
        var registry = new SimpleMeterRegistry();
        when(jdbcTemplate.queryForObject(SQL_IDLE_CONNECTIONS, Integer.class)).thenReturn(null);

        var metrics = new PostgresMetricsCollector(jdbcTemplate);
        metrics.pollTrackingDbMetrics();
        metrics.bindTo(registry);

        var gauge = registry.find("db.tracking.connections.idle").gauge();
        assertNotNull(gauge);
        assertEquals(0, gauge.value());
    }

    @Test
    void shouldExposeMaxConnections() {
        var registry = new SimpleMeterRegistry();

        var metrics = new PostgresMetricsCollector(jdbcTemplate);
        metrics.pollTrackingDbMetrics();
        metrics.bindTo(registry);

        var gauge = registry.find("db.tracking.connections.max").gauge();
        assertNotNull(gauge);
        assertEquals(1234, gauge.value());
    }

    @Test
    void shouldZeroIfErrorFetchingMaxConnections() {
        var registry = new SimpleMeterRegistry();
        when(jdbcTemplate.queryForObject(SQL_MAX_CONNECTIONS, Integer.class)).thenReturn(null);

        var metrics = new PostgresMetricsCollector(jdbcTemplate);
        metrics.pollTrackingDbMetrics();
        metrics.bindTo(registry);

        var gauge = registry.find("db.tracking.connections.max").gauge();
        assertNotNull(gauge);
        assertEquals(0, gauge.value());
    }

    @Test
    void shouldExposeCommittedTransactions() {
        var registry = new SimpleMeterRegistry();

        var metrics = new PostgresMetricsCollector(jdbcTemplate);
        metrics.pollTrackingDbMetrics();
        metrics.bindTo(registry);

        var gauge = registry.find("db.tracking.transactions.commits").gauge();
        assertNotNull(gauge);
        assertEquals(986421, gauge.value());
    }

    @Test
    void shouldZeroIfErrorFetchingCommittedConnections() {
        var registry = new SimpleMeterRegistry();
        when(jdbcTemplate.queryForObject(SQL_COMMITTED_TRANSACTIONS, Integer.class)).thenReturn(null);

        var metrics = new PostgresMetricsCollector(jdbcTemplate);
        metrics.pollTrackingDbMetrics();
        metrics.bindTo(registry);

        var gauge = registry.find("db.tracking.transactions.commits").gauge();
        assertNotNull(gauge);
        assertEquals(0, gauge.value());
    }

    @Test
    void shouldExposeRollbackTransactions() {
        var registry = new SimpleMeterRegistry();

        var metrics = new PostgresMetricsCollector(jdbcTemplate);
        metrics.pollTrackingDbMetrics();
        metrics.bindTo(registry);

        var gauge = registry.find("db.tracking.transactions.rollbacks").gauge();
        assertNotNull(gauge);
        assertEquals(79, gauge.value());
    }

    @Test
    void shouldZeroIfErrorFetchingRollbackTransactions() {
        var registry = new SimpleMeterRegistry();
        when(jdbcTemplate.queryForObject(SQL_ROLLBACK_TRANSACTIONS, Integer.class)).thenReturn(null);

        var metrics = new PostgresMetricsCollector(jdbcTemplate);
        metrics.pollTrackingDbMetrics();
        metrics.bindTo(registry);

        var gauge = registry.find("db.tracking.transactions.rollbacks").gauge();
        assertNotNull(gauge);
        assertEquals(0, gauge.value());
    }

    @Test
    void shouldCalculateActiveUtilisationRate() {
        var registry = new SimpleMeterRegistry();

        var metrics = new PostgresMetricsCollector(jdbcTemplate);
        metrics.pollTrackingDbMetrics();
        metrics.bindTo(registry);

        var gauge = registry.find("db.tracking.connections.utilisation.active").gauge();
        assertNotNull(gauge);
        assertEquals((double) 5 / 1234, gauge.value());
    }

    @Test
    void shouldCalculateTotalUtilisationRate() {
        var registry = new SimpleMeterRegistry();

        var metrics = new PostgresMetricsCollector(jdbcTemplate);
        metrics.pollTrackingDbMetrics();
        metrics.bindTo(registry);

        var gauge = registry.find("db.tracking.connections.utilisation.total_utilisation").gauge();
        assertNotNull(gauge);
        assertEquals((double) 23 / 1234, gauge.value());
    }

    @Test
    void shouldNaNWhenCalculatingIfMaxIsZero() {
        var registry = new SimpleMeterRegistry();

        when(jdbcTemplate.queryForObject(SQL_MAX_CONNECTIONS, Integer.class)).thenReturn(0);

        var metrics = new PostgresMetricsCollector(jdbcTemplate);
        metrics.pollTrackingDbMetrics();
        metrics.bindTo(registry);

        var gaugeActive = registry.find("db.tracking.connections.utilisation.active").gauge();
        assertNotNull(gaugeActive);
        assertEquals(Double.NaN, gaugeActive.value());

        var gaugeTotal = registry.find("db.tracking.connections.utilisation.total_utilisation").gauge();
        assertNotNull(gaugeTotal);
        assertEquals(Double.NaN, gaugeTotal.value());

    }

    @Test
    void shouldCatchSqlExceptionsAndAllowProgramToContinue() {
        var registry = new SimpleMeterRegistry();

        when(jdbcTemplate.queryForObject(SQL_ROLLBACK_TRANSACTIONS, Integer.class)).thenThrow(new QueryTimeoutException("Uh oh"));

        var metrics = new PostgresMetricsCollector(jdbcTemplate);
        metrics.pollTrackingDbMetrics();
        metrics.bindTo(registry);

    }

}