package uk.gov.laa.gpfd.config;

import java.io.IOException;
import java.util.Map;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mock.env.MockEnvironment;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

class HikariConfigurationTest {

    public static final String TARGET_CLASSES_APPLICATION_YML = "target/classes/application.yml";
    public static final String APPLICATION = "application";
    public static final String GPFD_DATASOURCE_TRACKING = "gpfd.datasource.tracking";

    @Test
    void shouldBindExplicitDefaultsToSharedTrackingAndMetadataPool() throws IOException {
        MockEnvironment environment = loadApplicationProperties();

        try (HikariDataSource dataSource = bindTrackingDataSource(environment)) {
            assertAll(
                    () -> assertEquals(600000L, dataSource.getIdleTimeout()),
                    () -> assertEquals(1800000L, dataSource.getMaxLifetime()),
                    () -> assertEquals(30000L, dataSource.getConnectionTimeout()),
                    () -> assertEquals(120000L, dataSource.getKeepaliveTime()),
                    () -> assertEquals(5000L, dataSource.getValidationTimeout()),
                    () -> assertEquals(10, dataSource.getMinimumIdle()),
                    () -> assertEquals(10, dataSource.getMaximumPoolSize()),
                    () -> assertSame(dataSource, new AppConfig().metadataDataSource(dataSource))
            );
        }
    }

    @Test
    void shouldAllowEnvironmentVariableToOverrideConnectionTimeout() throws IOException {
        MockEnvironment environment = loadApplicationProperties();
        environment.getPropertySources().addFirst(new SystemEnvironmentPropertySource(
                "systemEnvironment",
                Map.<String, Object>of("GPFD_DATASOURCE_TRACKING_CONNECTIONTIMEOUT", "10000")));

        try (HikariDataSource dataSource = bindTrackingDataSource(environment)) {
            assertEquals(10000L, dataSource.getConnectionTimeout());
            assertEquals(10, dataSource.getMinimumIdle());
            assertEquals(10, dataSource.getMaximumPoolSize());
        }
    }

    private MockEnvironment loadApplicationProperties() throws IOException {
        MockEnvironment environment = new MockEnvironment();
        FileSystemResource resource = new FileSystemResource(TARGET_CLASSES_APPLICATION_YML);
        for (var source : new YamlPropertySourceLoader().load(APPLICATION, resource)) {
            environment.getPropertySources().addLast(source);
        }
        return environment;
    }

    private HikariDataSource bindTrackingDataSource(MockEnvironment environment) {
        HikariDataSource dataSource = assertInstanceOf(
                HikariDataSource.class, new AppConfig().trackingDataSource());
        Binder.get(environment).bind(GPFD_DATASOURCE_TRACKING, Bindable.ofInstance(dataSource));
        return dataSource;
    }
}
