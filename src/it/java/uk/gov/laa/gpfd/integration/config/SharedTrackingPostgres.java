package uk.gov.laa.gpfd.integration.config;

import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * Single Postgres instance for all integration tests in one JVM.
 * <p>
 * {@code @Container} on {@code BaseIT} stops the database when each test class finishes, but Spring
 * caches the application context across classes — later tests then reuse a dead JDBC URL.
 */
public final class SharedTrackingPostgres {

    public static final PostgreSQLContainer<?> CONTAINER = new PostgreSQLContainer<>("postgres:18");

    static {
        CONTAINER.start();
    }

    private SharedTrackingPostgres() {
    }
}
