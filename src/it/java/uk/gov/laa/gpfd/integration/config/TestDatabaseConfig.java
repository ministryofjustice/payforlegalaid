package uk.gov.laa.gpfd.integration.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import uk.gov.laa.gpfd.config.TimeProvider;

import javax.sql.DataSource;
import java.time.LocalTime;

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
     * Spring Boot Flyway auto-config does not reliably migrate the tracking Postgres DB when
     * multiple datasources are present (see TestConfig for Cucumber ATs). Applies schema DDL (V1/V2)
     * plus {@code R__test_metadata.sql} only — not the production catalogue in proddata/.
     */
    @Bean
    Flyway postgresFlyway(@Qualifier("trackingDataSource") DataSource dataSource) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .schemas("glad")
                .createSchemas(true)
                .locations(
                        "classpath:flyway/migration/schema",
                        "classpath:flyway/migration/testdata")
                .baselineOnMigrate(true)
                .load();

        flyway.migrate();

        return flyway;
    }

}