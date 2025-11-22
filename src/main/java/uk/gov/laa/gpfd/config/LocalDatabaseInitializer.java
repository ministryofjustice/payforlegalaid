package uk.gov.laa.gpfd.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * Initializes the H2 database schema for the local profile.
 * This runs after the application context is fully initialized to ensure
 * the DataSource beans are properly configured.
 */
@Slf4j
@Component
@Profile("local")
@Order(1)
public class LocalDatabaseInitializer implements ApplicationRunner {

    private final DataSource dataSource;

    public LocalDatabaseInitializer(@Qualifier("writeDataSource") DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Initializing local H2 database schema and data...");
        
        try {
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource("schema-local.sql"));
            populator.addScript(new ClassPathResource("data-local.sql"));
            populator.setContinueOnError(false);
            populator.setSeparator(";");
            populator.execute(dataSource);
            
            log.info("Successfully initialized local H2 database schema and data");
        } catch (Exception e) {
            log.error("Failed to initialize local H2 database", e);
            throw e;
        }
    }
}
