package uk.gov.laa.gpfd.integration;

import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.laa.gpfd.graph.AzureGraphClient;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("testauth")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application-testauth.yml")
class AuthTokenIT extends BaseIT {

    @ParameterizedTest
    @ValueSource(strings = {"/reports", "/reports/0d4da9ec-b0b3-4371-af10-f375330d85d3", "/csv/0d4da9ec-b0b3-4371-af10-f375330d85d3"})
    void shouldRedirectToLoginWithoutAuthToken(String endpoint) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();
        Assertions.assertEquals(302, response.getStatus());
        Assertions.assertTrue(Objects.requireNonNull(response.getRedirectedUrl()).contains("/oauth2/authorization/azure"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/reports", "/reports/0d4da9ec-b0b3-4371-af10-f375330d85d3"})
    void shouldReturn200WhenLoginAuthTokenProvided(String endpoint) throws Exception {
        MockHttpServletResponse response = getResponseForAuthenticatedRequest(endpoint);
        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    void getCsvWithIdShouldReturn200WhenLoginAuthTokenProvided() throws Exception {
        MockHttpServletResponse response = getResponseForAuthenticatedRequest("/csv/0d4da9ec-b0b3-4371-af10-f375330d85d3");
        Assertions.assertEquals(200, response.getStatus());
    }
}