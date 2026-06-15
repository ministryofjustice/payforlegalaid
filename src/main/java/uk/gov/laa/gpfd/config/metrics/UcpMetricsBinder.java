package uk.gov.laa.gpfd.config.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.MeterBinder;
import oracle.ucp.jdbc.PoolDataSource;
import org.jspecify.annotations.NonNull;

import java.sql.SQLException;
import java.util.List;

public class UcpMetricsBinder implements MeterBinder {

    private final PoolDataSource dataSource;
    private final Tag sourceTag;

    public UcpMetricsBinder(PoolDataSource dataSource, Tag sourceTag) {
        this.dataSource = dataSource;
        this.sourceTag = sourceTag;
    }

    @Override
    public void bindTo(@NonNull MeterRegistry meterRegistry) {

        var tags = List.of(sourceTag);

        Gauge.builder("ucp.connections.total", dataSource,
                        poolDataSource -> {
                            var stats = poolDataSource.getStatistics();
                            if (stats == null) {
                                return Double.NaN;
                            }
                            return stats.getTotalConnectionsCount();
                        })
                .tags(tags)
                .description("Total pool connections")
                .register(meterRegistry);

        Gauge.builder("ucp.connections.available", dataSource, poolDataSource -> {
                    try {
                        return poolDataSource.getAvailableConnectionsCount();
                    } catch (SQLException _) {
                        return Double.NaN;
                    }
                })
                .tags(tags)
                .description("Available (idle) connections")
                .register(meterRegistry);

        Gauge.builder("ucp.connections.borrowed", dataSource, poolDataSource -> {
                    try {
                        return poolDataSource.getBorrowedConnectionsCount();
                    } catch (SQLException _) {
                        return Double.NaN;
                    }
                })
                .tags(tags)
                .description("Borrowed (active) connections")
                .register(meterRegistry);

        Gauge.builder("ucp.connections.maximum", dataSource, PoolDataSource::getMaxPoolSize)
                .tags(tags)
                .description("Maximum connections")
                .register(meterRegistry);

        Gauge.builder("ucp.connections.pending", dataSource,
                        poolDataSource -> {
                            var stats = poolDataSource.getStatistics();
                            if (stats == null) {
                                return Double.NaN;
                            }
                            return stats.getPendingRequestsCount();
                        })
                .tags(tags)
                .description("Connections pending")
                .register(meterRegistry);

        Gauge.builder("ucp.connections.created", dataSource,
                        poolDataSource -> {
                            var stats = poolDataSource.getStatistics();
                            if (stats == null) {
                                return Double.NaN;
                            }
                            return stats.getConnectionsCreatedCount();
                        })
                .tags(tags)
                .description("Connections created")
                .register(meterRegistry);

        Gauge.builder("ucp.connections.closed", dataSource,
                        poolDataSource -> {
                            var stats = poolDataSource.getStatistics();
                            if (stats == null) {
                                return Double.NaN;
                            }
                            return stats.getConnectionsClosedCount();
                        })
                .tags(tags)
                .description("Connections closed")
                .register(meterRegistry);

        Gauge.builder("ucp.connections.utilisation", dataSource,
                        poolDataSource -> {
                            try {
                                var active = poolDataSource.getBorrowedConnectionsCount();
                                var max = poolDataSource.getMaxPoolSize();
                                return max == 0 ? Double.NaN : (double) active / max;
                            } catch (SQLException _) {
                                return Double.NaN;
                            }
                        })
                .tags(tags)
                .description("Ratio of active pools to max pool usage")
                .register(meterRegistry);

    }
}
