package uk.gov.laa.gpfd.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

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
        schemaPopulator.addScript(new ClassPathResource("any_report_schema.sql"));
        schemaPopulator.addScript(new ClassPathResource("any_report_data.sql"));

        initialiser.setDatabasePopulator(schemaPopulator);
        return initialiser;
    }
}
