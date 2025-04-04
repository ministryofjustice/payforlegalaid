package uk.gov.laa.gpfd.integration;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("testauth")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application-testauth.yml")
class AuthTokenIT extends BaseIT {

    @ParameterizedTest
    @ValueSource(strings = {"/reports", "/reports/0d4da9ec-b0b3-4371-af10-f375330d85d3", "/csv/0d4da9ec-b0b3-4371-af10-f375330d85d3"})
    void shouldRedirectToLoginWithoutAuthToken(String endpoint) throws Exception {
        performGetRequest(endpoint)
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("http://localhost/oauth2/authorization/azure*"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/reports", "/reports/0d4da9ec-b0b3-4371-af10-f375330d85d3"})
    @WithMockUser(username = "Mock User")
    void shouldReturn200WhenLoginAuthTokenProvided(String endpoint) throws Exception {
        performGetRequest(endpoint)
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "Mock User")
    void getCsvWithIdShouldReturn200WhenLoginAuthTokenProvided() throws Exception {
        performGetRequest("/csv/0d4da9ec-b0b3-4371-af10-f375330d85d3")
            .andExpect(status().isOk());
    }
}