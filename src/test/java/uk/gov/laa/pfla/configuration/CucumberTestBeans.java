package uk.gov.laa.pfla.configuration;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import uk.gov.laa.pfla.client.AuthenticationProvider;
import uk.gov.laa.pfla.client.RestClient;
import uk.gov.laa.pfla.client.RestTemplateWithErrorHandling;
import uk.gov.laa.pfla.scenario.AuthenticationState;
import uk.gov.laa.pfla.scenario.ScenarioContext;
import uk.gov.laa.pfla.service.HttpProvider;
import uk.gov.laa.pfla.util.WorkbookUtil;
import uk.gov.laa.pfla.util.workbook.WorkbookCreator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.laa.pfla.client.AuthenticationProvider.basicAuth;

@TestConfiguration
public class CucumberTestBeans {

    @Bean
    RestClient restClient(AuthenticationProvider authenticationProvider) {
        RestClient mock = mock(RestClient.class);

        RestTemplateWithErrorHandling authenticated =
                new RestTemplateWithErrorHandling();

        authenticated.getInterceptors().add(
                (request, body, execution) -> {
                    request.getHeaders()
                            .addAll(authenticationProvider.setAuthHeader());
                    return execution.execute(request, body);
                }
        );

        RestTemplateWithErrorHandling invalid =
                new RestTemplateWithErrorHandling();

        invalid.getInterceptors().add(
                (request, body, execution) -> {
                    request.getHeaders()
                            .addAll(basicAuth("foo", "bar").setAuthHeader());
                    return execution.execute(request, body);
                }
        );

        when(mock.authenticated()).thenReturn(authenticated);
        when(mock.invalidCredentials()).thenReturn(invalid);
        when(mock.unauthenticated()).thenReturn(new RestTemplate());

        return mock;
    }

    @Bean
    public HttpProvider httpProvider(RestClient restClient, ScenarioContext scenarioContext) {
        return new HttpProvider() {

            @Override
            public RestTemplate getClient() {
                return getClient(restClient, scenarioContext.getAuthenticationState());
            }

            @Override
            public void setAuthenticationState(AuthenticationState state) {
                scenarioContext.setAuthenticationState(state);
            }
        };
    }

    @Bean
    WorkbookCreator workbookCreator() {
        return new WorkbookCreator() {};
    }

    @Bean
    public WorkbookUtil workbookUtil(
            ScenarioContext scenarioContext,
            WorkbookCreator workbookCreator) {

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
    AuthenticationProvider authenticationProvider() {
        return basicAuth(
                "test-user",
                "test-password"
        );
    }
}