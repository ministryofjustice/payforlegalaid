package uk.gov.laa.gpfd.config.metrics;

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.MeterBinder;
import oracle.ucp.jdbc.PoolDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class MetricsConfig {
    /**
     * Create a metric registry to collect Oracle/MOJFIN connection pool metrics and send to Prometheus
     * We pass in the generic DataSource and then check the type just to avoid typing and injection issues when running the integration tests.
     * @param readOnlyDataSource - the MOJFIN data source
     * @return metric registry
     */
    @Bean
    @ConditionalOnBean(name="readOnlyDataSource")
    public MeterBinder readUcpMetricsBinder(@Qualifier("readOnlyDataSource") DataSource readOnlyDataSource) {

        return meterRegistry -> {
            if (readOnlyDataSource instanceof PoolDataSource readOnlyPool) {
                new UcpMetricsBinder(readOnlyPool, Tag.of("datasource", "readOnly")).bindTo(meterRegistry);
            }
        };
    }

}
