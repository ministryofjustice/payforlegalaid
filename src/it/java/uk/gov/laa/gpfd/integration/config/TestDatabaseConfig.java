package uk.gov.laa.gpfd.integration.config;

import org.flywaydb.core.Flyway;
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

    /**
     * Manually configure Flyway to apply migrations to the PostgreSQL test container.
     * This ensures the metadata tables are created and seeded properly for integration tests.
     */
    @Bean
    Flyway metadataFlyway(@Qualifier("trackingDataSource") DataSource dataSource) {
        try {
            // First, create the glad schema if it doesn't exist
            JdbcTemplate template = new JdbcTemplate(dataSource);
            template.execute("CREATE SCHEMA IF NOT EXISTS glad");
            
            Flyway flyway = Flyway.configure()
                    .dataSource(dataSource)
                    .schemas("glad")
                    .defaultSchema("glad")
                    .locations("classpath:flyway/migration/schema")
                    .baselineOnMigrate(true)
                    .load();
            
            flyway.migrate();
            return flyway;
        } catch (Exception e) {
            System.err.println("=== Flyway migration failed: " + e.getMessage() + " ===");
            e.printStackTrace();
            throw e;
        }
    }

}
