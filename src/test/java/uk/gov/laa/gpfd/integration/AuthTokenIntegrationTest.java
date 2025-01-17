package uk.gov.laa.gpfd.integration;

import com.microsoft.graph.models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.laa.gpfd.graph.AzureGraphClient;
import uk.gov.laa.gpfd.utils.FileUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Client;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@AutoConfigureMockMvc
@ActiveProfiles("testauth")
@TestPropertySource(locations = "classpath:application-testauth.yml")
@SpringBootTest
class AuthTokenIntegrationTest {

    @MockitoBean
    AzureGraphClient mockAzureGraphClient;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate writeJdbcTemplate;

    @BeforeEach
    void setupDatabase() {
        String gpfdSqlSchema = FileUtils.readResourceToString("gpfd_schema.sql");
        String gpfdSqlData = FileUtils.readResourceToString("gpfd_data.sql");
        writeJdbcTemplate.execute(gpfdSqlSchema);
        writeJdbcTemplate.execute(gpfdSqlData);

        String anyReportSqlSchema = FileUtils.readResourceToString("any_report_schema.sql");
        String anyReportSqlData = FileUtils.readResourceToString("any_report_data.sql");
        writeJdbcTemplate.execute(anyReportSqlSchema);
        writeJdbcTemplate.execute(anyReportSqlData);
    }

    @AfterEach
    void resetDatabase() {
        writeJdbcTemplate.update("TRUNCATE TABLE GPFD.REPORT_TRACKING");
        writeJdbcTemplate.update("TRUNCATE TABLE GPFD.CSV_TO_SQL_MAPPING_TABLE");
    }

    @ParameterizedTest
    @ValueSource(strings = {"/reports", "/reports/0d4da9ec-b0b3-4371-af10-f375330d85d3", "/csv/0d4da9ec-b0b3-4371-af10-f375330d85d3"})
    void shouldRedirectToLoginWithoutAuthToken(String endpoint) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        Assertions.assertEquals(302, response.getStatus());
        Assertions.assertTrue(response.getRedirectedUrl().contains("/oauth2/authorization/azure"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/reports", "/reports/0d4da9ec-b0b3-4371-af10-f375330d85d3"})
    void shouldReturn200WhenLoginAuthTokenProvided(String endpoint) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get(endpoint)
                .with(SecurityMockMvcRequestPostProcessors.oauth2Login())
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    void getCsvWithIdShouldReturn200WhenLoginAuthTokenProvided() throws Exception {
        User user = new User();
        user.userPrincipalName = "Test User";

        when(mockAzureGraphClient.getGraphUserDetails(any())).thenReturn(user);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/csv/0d4da9ec-b0b3-4371-af10-f375330d85d3")
                .with(SecurityMockMvcRequestPostProcessors.oauth2Login())
                .with(oidcLogin()
                        .idToken(token -> token.subject("mockUser")))
                .with(oauth2Client("graph"))).andReturn().getResponse();

        Assertions.assertEquals(200, response.getStatus());
    }
}