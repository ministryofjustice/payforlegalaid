package uk.gov.laa.gpfd.integration.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@TestConfiguration
public class TestDatabaseConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "gpfd.datasource.read-only")
    DataSource readOnlyDataSource() {
        return new DriverManagerDataSource();
    }

    @Bean
    @Primary
    JdbcTemplate readOnlyJdbcTemplate(@Qualifier("readOnlyDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public NamedParameterJdbcOperations namedParameterJdbcOperations(@Qualifier("readOnlyDataSource")DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * Tracking data source for PostgreSQL test container.
     */
    @Bean
    @ConfigurationProperties(prefix = "gpfd.datasource.tracking")
    DataSource trackingDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * Metadata is held alongside report tracking data in the PostgreSQL test container.
     */
    @Bean
    DataSource metadataDataSource(@Qualifier("trackingDataSource") DataSource dataSource) {
        return dataSource;
    }

    @Bean
    JdbcTemplate metadataJdbcTemplate(@Qualifier("metadataDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    NamedParameterJdbcOperations namedMetadataJdbcTemplate(
            @Qualifier("metadataDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

}
