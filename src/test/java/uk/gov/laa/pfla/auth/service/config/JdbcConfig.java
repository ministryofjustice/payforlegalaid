//package uk.gov.laa.pfla.auth.service.config;
//
//import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.jdbc.datasource.DriverManagerDataSource;
//import javax.sql.DataSource;
//@Configuration
//public class JdbcConfig extends AbstractJdbcConfig {
//    @Bean
//    @Primary
//    @ConfigurationProperties("spring.datasource")
//    public DataSourceProperties dataSourceProperties() {
//        return new DataSourceProperties();
//    }
//    @Bean
//    @Primary
//    public DataSource dataSource() {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName(dataSourceProperties().getDriverClassName());
//        dataSource.setUrl(dataSourceProperties().getUrl());
//        dataSource.setUsername(dataSourceProperties().getUsername());
//        dataSource.setPassword(dataSourceProperties().getPassword());
//        return dataSource;
//    }
//}