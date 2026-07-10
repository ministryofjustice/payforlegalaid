package uk.gov.laa.pfla.configuration;

import com.fasterxml.jackson.core.TokenStreamFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.laa.pfla.client.interceptor.HostInterceptor;
import uk.gov.laa.pfla.util.JsonDeserializer;

import javax.sql.DataSource;

import static uk.gov.laa.pfla.client.interceptor.HostInterceptor.withHost;

@TestConfiguration
@ActiveProfiles("testat")
public class TestConfig {

    @Bean("readOnlyDataSource")
    @ConfigurationProperties(prefix = "gpfd.datasource.read-only")
    public DataSource readOnlyDataSource() {
        return DataSourceBuilder
                .create()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    public DataSource writeDataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.h2.Driver");
        ds.setUrl("jdbc:h2:file:~/localGpfdDb;MODE=Oracle");
        ds.setUsername("sa");
        ds.setPassword("");
        return ds;
    }

    @Bean
    public JdbcTemplate writeJdbcTemplate(
            @Qualifier("writeDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    @Bean
    @Primary
    public JdbcTemplate jdbcTemplate(
            @Qualifier("writeDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return token -> {
            if (!"valid-token".equals(token)) {
                throw new JwtException("Invalid token");
            }

            return Jwt.withTokenValue(token)
                    .header("alg", "none")
                    .claim("sub", "test-user")
                    .build();
        };
    }

    @Bean
    @Primary
    public ClientRegistrationRepository clientRegistrationRepository() {

        ClientRegistration registration =
                ClientRegistration.withRegistrationId("graph")
                        .clientId("mockClientId")
                        .clientSecret("mockClientSecret")
                        .scope("read")
                        .authorizationUri("test")
                        .redirectUri("test")
                        .tokenUri("test")
                        .authorizationGrantType(
                                AuthorizationGrantType.CLIENT_CREDENTIALS)
                        .build();

        return new InMemoryClientRegistrationRepository(registration);
    }

    @Bean
    JsonDeserializer jsonDeserializer() {
        return new JsonDeserializer() {
            private final ObjectMapper mapper = new ObjectMapper();

            @Override
            public TokenStreamFactory tokenStreamFactory() {
                return mapper.getFactory();
            }
        };
    }

    @Bean
    public HostInterceptor hostInterceptor() {
        return withHost("http://localhost:8080");
    }
}