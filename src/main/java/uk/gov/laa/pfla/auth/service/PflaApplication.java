package uk.gov.laa.pfla.auth.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class PflaApplication {

    //Maps objects (such as model objects -> response objects), using the names of the object fields.
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    //Using the read only credentials from the application.yml to set up a JDBC data source
    @Bean
    @ConfigurationProperties(prefix = "gpfd.datasource.read-only")
    public DataSource readOnlyDataSource() {
        return new DriverManagerDataSource();
    }

    //Using the write credentials from the application.yml to set up a JDBC data source
    @Bean
    @ConfigurationProperties(prefix = "gpfd.datasource.write")
    public DataSource writeDataSource() {
        return new DriverManagerDataSource();
    }


    @Bean
    public JdbcTemplate readOnlyJdbcTemplate(@Qualifier("readOnlyDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public JdbcTemplate writeJdbcTemplate(@Qualifier("writeDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }


    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(new StringHttpMessageConverter());
        messageConverters.add(new ByteArrayHttpMessageConverter());

        restTemplate.setMessageConverters(messageConverters);

        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(PflaApplication.class, args);
    }

}
