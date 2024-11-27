package uk.gov.laa.gpfd.config;

import liquibase.integration.spring.SpringLiquibase;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.util.List;

/**
* Configuration class for application-level beans and settings.
 * <p>
 * This class defines various beans such as data sources, JDBC templates,
 * model mapper, and a RestTemplate with custom message converters. These configurations
 * are essential for database connectivity, object mapping, and external API integration.
 * </p>
 */
@Configuration
public class AppConfig {

    /**
     * Creates and configures a {@link ModelMapper} bean for object-to-object mapping.
     * <p>
     * The {@code ModelMapper} facilitates mapping between objects such as model entities
     * and DTOs, making it easier to transform data across layers of the application.
     * </p>
     *
     * @return a configured {@link ModelMapper} instance.
     */
    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }

    /**
     * Configures a read-only {@link DataSource} using properties prefixed with
     * "gpfd.datasource.read-only" in the application's configuration file.
     * <p>
     * This data source is intended for read-only operations in the database, such as queries.
     * </p>
     *
     * @return a configured {@link DataSource} for read-only operations.
     */
    @Bean
    @ConfigurationProperties(prefix = "gpfd.datasource.read-only")
    DataSource readOnlyDataSource() {
        return new DriverManagerDataSource();
    }

    /**
     * Configures a write-enabled {@link DataSource} using properties prefixed with
     * "gpfd.datasource.write" in the application's configuration file.
     * <p>
     * This data source is intended for write operations in the database, such as inserts and updates.
     * </p>
     *
     * @return a configured {@link DataSource} for write operations.
     */
    @Bean
    @ConfigurationProperties(prefix = "gpfd.datasource.write")
    DataSource writeDataSource() {
        return new DriverManagerDataSource();
    }

    /**
     * Configures a {@link JdbcTemplate} for read-only database operations.
     * <p>
     * The {@code JdbcTemplate} is built on the {@code readOnlyDataSource} and simplifies
     * querying and interacting with the database in a read-only capacity.
     * </p>
     *
     * @param dataSource the read-only {@link DataSource} to be used by the {@link JdbcTemplate}.
     * @return a configured {@link JdbcTemplate} for read-only operations.
     */
    @Bean
    JdbcTemplate readOnlyJdbcTemplate(@Qualifier("readOnlyDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * Configures a {@link JdbcTemplate} for write-enabled database operations.
     * <p>
     * The {@code JdbcTemplate} is built on the {@code writeDataSource} and simplifies
     * executing updates and inserts into the database.
     * </p>
     *
     * @param dataSource the write-enabled {@link DataSource} to be used by the {@link JdbcTemplate}.
     * @return a configured {@link JdbcTemplate} for write-enabled operations.
     */
    @Bean
    JdbcTemplate writeJdbcTemplate(@Qualifier("writeDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * Configures a {@link RestTemplate} bean for making REST API calls.
     * <p>
     * The {@code RestTemplate} is configured with a custom list of message converters:
     * <ul>
     *     <li>{@link StringHttpMessageConverter}: Converts HTTP messages to and from strings.</li>
     *     <li>{@link ByteArrayHttpMessageConverter}: Converts HTTP messages to and from byte arrays.</li>
     * </ul>
     * These converters enable the application to handle various content types when interacting
     * with external APIs.
     * </p>
     *
     * @return a configured {@link RestTemplate} instance with custom message converters.
     */
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate() {{
            setMessageConverters(List.of(
                    new StringHttpMessageConverter(),
                    new ByteArrayHttpMessageConverter()
            ));
        }};
    }

    /**
     * Creates and configures a {@link SpringLiquibase} bean to be used for database,
     * if the property `spring.liquibase.enabled` is set to `true` in the application properties.
     *
     * This method will set the data source to the specified {@link DataSource} bean, configure the
     * change log file to be used by Liquibase, and ensure that the migrations are executed by
     * setting {@code setShouldRun(true)}.
     *
     * @param dataSource The {@link DataSource} bean to be used by Liquibase for database connectivity.
     * @return A configured {@link SpringLiquibase} instance ready for migration.
     *
     * @see SpringLiquibase
     * @see DataSource
     */
    @Bean
    @ConditionalOnProperty(name = "spring.liquibase.enabled", havingValue = "true")
    public SpringLiquibase liquibase(@Qualifier("writeDataSource") DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:liquibase/db.changelog-master.xml");
        liquibase.setShouldRun(true);
        return liquibase;
    }

}