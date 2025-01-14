package uk.gov.laa.gpfd.config;

import lombok.extern.slf4j.Slf4j;
import org.h2.tools.DeleteDbFiles;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@Slf4j
@Configuration
@Profile("local")
public class DatabaseConfigLocal {

    @Bean
    public DataSourceInitializer dataSourceInitializer(@Qualifier("writeDataSource") DataSource dataSource) {
        DataSourceInitializer initialiser = new DataSourceInitializer();
        initialiser.setDataSource(dataSource);

        ResourceDatabasePopulator schemaPopulator = new ResourceDatabasePopulator();
        schemaPopulator.addScript(new ClassPathResource("gpfd_schema.sql"));
        schemaPopulator.addScript(new ClassPathResource("gpfd_data.sql"));

        initialiser.setDatabasePopulator(schemaPopulator);

        // Make sure we clean up at the end of this session
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                log.info("Attempting to delete local H2 database files");
                DeleteDbFiles.execute("~", "localGpfdDb", false);
                log.info("Database file deleted.");
            } catch (Exception e) {
                log.error("Error deleting database files: {}", e.getMessage());
            }
        }));

        return initialiser;
    }
}
