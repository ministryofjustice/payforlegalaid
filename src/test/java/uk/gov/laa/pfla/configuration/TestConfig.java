package uk.gov.laa.pfla.configuration;

import com.fasterxml.jackson.databind.MappingJsonFactory;
import liquibase.integration.spring.SpringLiquibase;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
import org.springframework.web.client.RestTemplate;
import uk.gov.laa.pfla.client.AuthenticationProvider;
import uk.gov.laa.pfla.client.RestClient;
import uk.gov.laa.pfla.client.RestTemplateWithErrorHandling;
import uk.gov.laa.pfla.client.interceptor.AuthenticationInterceptor;
import uk.gov.laa.pfla.client.interceptor.HostInterceptor;
import uk.gov.laa.pfla.comparator.WorkbookComparator;
import uk.gov.laa.pfla.scenario.ScenarioContext;
import uk.gov.laa.pfla.service.HttpProvider;
import uk.gov.laa.pfla.util.JsonDeserializer;
import uk.gov.laa.pfla.util.WorkbookUtil;
import uk.gov.laa.pfla.util.workbook.WorkbookCreator;

import javax.sql.DataSource;
import java.util.function.Supplier;

import static java.util.List.of;
import static uk.gov.laa.pfla.client.AuthenticationProvider.basicAuth;
import static uk.gov.laa.pfla.client.interceptor.HostInterceptor.withHost;

@Configuration
public class TestConfig {

    @Value("${spring.liquibase.changelog}")
    private String liquibaseChangeLog;

    @Bean
    @Qualifier("validAuthenticationInterceptor")
    public AuthenticationInterceptor authenticationInterceptor() {
        return this::authenticationProvider;
    }

    @Bean
    public HostInterceptor hostInterceptor() {
        return withHost("http://localhost:8080");
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return basicAuth("test-user", "test-password");
    }

    @Bean
    @Qualifier("authenticatedRestTemplate")
    public RestTemplateWithErrorHandling authenticatedRestTemplate(
            @Qualifier("validAuthenticationInterceptor") AuthenticationInterceptor authenticationInterceptor,
            HostInterceptor hostInterceptor) {
        return new RestTemplateWithErrorHandling() {{
            setInterceptors(of(authenticationInterceptor, hostInterceptor));
        }};
    }

    @Bean
    @Qualifier("invalidCredentialsRestClient")
    public RestTemplateWithErrorHandling invalidCredentialsRestClient(HostInterceptor hostInterceptor) {
        return new RestTemplateWithErrorHandling() {{
            setInterceptors(of((AuthenticationInterceptor) () -> basicAuth("foo", "bar"), hostInterceptor));
        }};
    }

    @Bean
    public Supplier<RestTemplate> restTemplateSupplier() {
        return RestTemplate::new;
    }

    @Bean
    public HttpProvider httpClientServiceSupplier(RestClient restClient, ScenarioContext scenarioContext) {
        return new HttpProvider() {
            @Override
            public RestTemplate getClient() {
                return getClient(restClient, scenarioContext.getAuthenticationState());
            }
        };
    }

    @Bean
    public RestClient restClient(
            RestTemplate restTemplate,
            @Qualifier("authenticatedRestTemplate") RestTemplateWithErrorHandling restTemplateWithErrorHandling,
            @Qualifier("invalidCredentialsRestClient") RestTemplateWithErrorHandling invalidCredentialsRestClient
    ) {
        return new RestClient() {
            @Override
            public RestTemplate authenticated() {
                return restTemplateWithErrorHandling;
            }

            @Override
            public RestTemplate invalidCredentials() {
                return invalidCredentialsRestClient;
            }

            @Override
            public RestTemplate unauthenticated() {
                return restTemplate;
            }
        };
    }

    @Bean
    public ClientRegistrationRepository emptyClientRegistrationRepository() {
        var localRegistration = ClientRegistration.withRegistrationId("graph")
                .clientId("mockClientId")
                .clientSecret("mockClientSecret")
                .scope("read")
                .authorizationUri("test")
                .redirectUri("test2")
                .tokenUri("test3")
                .authorizationGrantType(AuthorizationGrantType.JWT_BEARER)
                .build();

        return new InMemoryClientRegistrationRepository(of(localRegistration));
    }

    @Bean
    public WorkbookComparator workbookComparator() {
        return new WorkbookComparator() {};
    }

    @Bean
    public WorkbookUtil testingExcelService(ScenarioContext scenarioContext, WorkbookCreator workbookCreator) {
        return new WorkbookUtil() {
            @Override
            public Workbook getExcelWorkbook() {
                return getExcelWorkbook(scenarioContext);
            }

            @Override
            public WorkbookCreator workbookCreator() {
                return workbookCreator;
            }
        };
    }

    @Bean
    public WorkbookCreator workbookCreator() {
        return new WorkbookCreator() {};
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return token -> {
                // Simply parse the JWT without verification
                return Jwt.withTokenValue(token)
                        .build();
        };
    }

    @Bean
    public JsonDeserializer jsonDeserializer() {
        return MappingJsonFactory::new;
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
    @Primary
    @ConditionalOnProperty(name = "spring.liquibase.enabled", havingValue = "true")
    public SpringLiquibase liquibase(@Qualifier("writeDataSource") DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(liquibaseChangeLog);
        liquibase.setShouldRun(true);
        return liquibase;
    }

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "gpfd.datasource.read-only")
    DataSource readOnlyDataSource() {
        return new DriverManagerDataSource();
    }

    @Bean
    @Primary
    JdbcTemplate readOnlyJdbcTemplate(@Qualifier("readOnlyDataSource") DataSource dataSource) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        return template;
    }

}
