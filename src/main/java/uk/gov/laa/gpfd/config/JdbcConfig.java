package uk.gov.laa.gpfd.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class JdbcConfig {

    @Bean public NamedParameterJdbcOperations namedParameterJdbcOperations( @Qualifier("readOnlyDataSource") DataSource dataSource ) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
