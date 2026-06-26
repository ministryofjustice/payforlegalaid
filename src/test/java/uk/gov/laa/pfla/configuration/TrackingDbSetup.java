package uk.gov.laa.pfla.configuration;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@Testcontainers
public final class TrackingDbSetup {

    public static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:18")
            .withDatabaseName("glad")
            .withUsername("postgres")
            .withPassword("password")
            .withExposedPorts(5432);

    static {
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void register(DynamicPropertyRegistry r) {

        r.add("gpfd.datasource.tracking.jdbcUrl", POSTGRES::getJdbcUrl);
        r.add("gpfd.datasource.tracking.username", POSTGRES::getUsername);
        r.add("gpfd.datasource.tracking.password", POSTGRES::getPassword);

        r.add("spring.flyway.enabled", () -> true);
        r.add("spring.flyway.url", POSTGRES::getJdbcUrl);
        r.add("spring.flyway.user", POSTGRES::getUsername);
        r.add("spring.flyway.password", POSTGRES::getPassword);

    }

}