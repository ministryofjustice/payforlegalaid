package uk.gov.laa.gpfd.integration.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import uk.gov.laa.pfla.configuration.TrackingDbSetup;

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
    public NamedParameterJdbcOperations namedParameterJdbcOperations(@Qualifier("readOnlyDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    DataSource trackingDataSource() {
        return org.springframework.boot.jdbc.DataSourceBuilder.create()
                .url(TrackingDbSetup.POSTGRES.getJdbcUrl())
                .username(TrackingDbSetup.POSTGRES.getUsername())
                .password(TrackingDbSetup.POSTGRES.getPassword())
                .build();
    }

    @Bean
    DataSource metadataDataSource(@Qualifier("trackingDataSource") DataSource dataSource) {
        return dataSource;
    }

    @Bean
    JdbcTemplate trackingJdbcTemplate(@Qualifier("trackingDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    JdbcClient metadataClient(@Qualifier("metadataDataSource") DataSource dataSource) {
        return JdbcClient.create(dataSource);
    }

    @Bean
    Flyway postgresFlyway(@Qualifier("trackingDataSource") DataSource dataSource) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .schemas("glad")
                .defaultSchema("glad")
                .locations("classpath:flyway/migration/test")
                .baselineOnMigrate(true)
                .load();

        flyway.migrate();
        return flyway;
    }
}
