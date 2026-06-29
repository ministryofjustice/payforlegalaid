package uk.gov.laa.gpfd.config.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Bean that holds details on postgres db tracking. It both defines these metrics and handles collecting them
 * We poll the database for database metrics less often than Prometheus asks for metrics, and store them.
 * This is to avoid the risk of the database getting spammed by Prometheus requests
 */
@Slf4j
@Component
@ConditionalOnBean(name = "trackingJdbcTemplate")
public class PostgresMetricsCollector implements MeterBinder {

    protected static final String SQL_TOTAL_CONNECTIONS = "SELECT count(*) FROM pg_stat_activity";
    protected static final String SQL_ACTIVE_CONNECTIONS = "SELECT count(*) FROM pg_stat_activity WHERE state = 'active'";
    protected static final String SQL_IDLE_CONNECTIONS = "SELECT count(*) FROM pg_stat_activity WHERE state = 'idle'";
    protected static final String SQL_MAX_CONNECTIONS = "SHOW max_connections";
    protected static final String SQL_COMMITTED_TRANSACTIONS = "SELECT xact_commit FROM pg_stat_database WHERE datname = current_database()";
    protected static final String SQL_ROLLBACK_TRANSACTIONS = "SELECT xact_rollback FROM pg_stat_database WHERE datname = current_database()";

    private final JdbcTemplate trackingJdbcTemplate;

    private final AtomicInteger totalConnections = new AtomicInteger();
    private final AtomicInteger activeConnections = new AtomicInteger();
    private final AtomicInteger idleConnections = new AtomicInteger();
    private final AtomicInteger maxConnections = new AtomicInteger();
    private final AtomicInteger committedTransactions = new AtomicInteger();
    private final AtomicInteger rolledBackTransaction = new AtomicInteger();

    private final List<Tag> trackingTag = List.of(Tag.of("datasource", "tracking"));

    public PostgresMetricsCollector(@Qualifier("trackingJdbcTemplate") JdbcTemplate trackingJdbcTemplate) {
        this.trackingJdbcTemplate = trackingJdbcTemplate;
    }

    @Override
    public void bindTo(@NonNull MeterRegistry meterRegistry) {

        Gauge.builder("db.tracking.connections.total", totalConnections, AtomicInteger::get)
                .tags(trackingTag)
                .description("Total open connections to the database")
                .register(meterRegistry);

        Gauge.builder("db.tracking.connections.active", activeConnections, AtomicInteger::get)
                .tags(trackingTag)
                .description("Total active connections to the database")
                .register(meterRegistry);

        Gauge.builder("db.tracking.connections.idle", idleConnections, AtomicInteger::get)
                .tags(trackingTag)
                .description("Total idle connections to the database")
                .register(meterRegistry);

        Gauge.builder("db.tracking.connections.max", maxConnections, AtomicInteger::get)
                .tags(trackingTag)
                .description("Max connections")
                .register(meterRegistry);

        Gauge.builder("db.tracking.transactions.commits", committedTransactions, AtomicInteger::get)
                .tags(trackingTag)
                .description("Total number of committed transactions (database activity, including reads and writes)")
                .register(meterRegistry);

        Gauge.builder("db.tracking.transactions.rollbacks", rolledBackTransaction, AtomicInteger::get)
                .tags(trackingTag)
                .description("Total number of failed transactions (rollbacks)")
                .register(meterRegistry);

        Gauge.builder("db.tracking.connections.utilisation.active",
                        () -> maxConnections.get() == 0 ? Double.NaN : activeConnections.get() / (double) maxConnections.get())
                .tags(trackingTag)
                .description("Active utilisation rate (active/max)")
                .register(meterRegistry);

        // Metric name can't end in total!
        Gauge.builder("db.tracking.connections.utilisation.total_utilisation",
                        () -> maxConnections.get() == 0 ? Double.NaN : totalConnections.get() / (double) maxConnections.get())
                .tags(trackingTag)
                .description("Total utilisation rate (total/max)")
                .register(meterRegistry);

    }

    // Only run every 30s to avoid hammering the db
    @Scheduled(fixedDelay = 30000)
    public void pollTrackingDbMetrics() {
        log.info("Querying Report Tracking db for metrics");
        try {
            var total = trackingJdbcTemplate.queryForObject(SQL_TOTAL_CONNECTIONS, Integer.class);
            totalConnections.set(total == null ? 0 : total);

            var active = trackingJdbcTemplate.queryForObject(SQL_ACTIVE_CONNECTIONS, Integer.class);
            activeConnections.set(active == null ? 0 : active);

            var idle = trackingJdbcTemplate.queryForObject(SQL_IDLE_CONNECTIONS, Integer.class);
            idleConnections.set(idle == null ? 0 : idle);

            var max = trackingJdbcTemplate.queryForObject(SQL_MAX_CONNECTIONS, Integer.class);
            maxConnections.set(max == null ? 0 : max);

            var committed = trackingJdbcTemplate.queryForObject(SQL_COMMITTED_TRANSACTIONS, Integer.class);
            committedTransactions.set(committed == null ? 0 : committed);

            var rollbacks = trackingJdbcTemplate.queryForObject(SQL_ROLLBACK_TRANSACTIONS, Integer.class);
            rolledBackTransaction.set(rollbacks == null ? 0 : rollbacks);

        } catch (DataAccessException e) {
            log.warn("Failed to poll Postgres metrics", e);
        }
    }

}
