package uk.gov.laa.gpfd.integration;

import com.microsoft.graph.models.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.laa.gpfd.config.TestAuthConfig;
import uk.gov.laa.gpfd.graph.AzureGraphClient;
import uk.gov.laa.gpfd.utils.FileUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Client;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("testauth")
@Import({OAuth2TestConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application-test.yml")
class GetCsvByIdIT extends TestAuthConfig {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate writeJdbcTemplate;

    @MockitoBean
    private AzureGraphClient mockAzureGraphClient;

    @BeforeAll
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

    @AfterAll
    void resetDatabase() {
        writeJdbcTemplate.execute("DROP TABLE IF EXISTS GPFD.REPORT_TRACKING");
        writeJdbcTemplate.execute("DROP TABLE IF EXISTS GPFD.CSV_TO_SQL_MAPPING_TABLE");
    }

    @Test
    void shouldReturnCsvWithMatchingId() throws Exception {
        User user = new User();
        user.userPrincipalName = "Test User";

        when(mockAzureGraphClient.getGraphUserDetails(any())).thenReturn(user);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/csv/0d4da9ec-b0b3-4371-af10-f375330d85d3")
                .with(oidcLogin()
                        .idToken(token -> token.subject("mockUser")))
                .with(oauth2Client("graph"))).andReturn().getResponse();

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("attachment; filename=CIS to CCMS payment value Defined.csv", response.getHeader("Content-Disposition"));
    }

    @Test
    void shouldReturn404WhenNoReportsFound() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/csv/0d4da9ec-b0b3-4371-af10-321")
                .with(oidcLogin()
                        .idToken(token -> token.subject("mockUser")))
                .with(oauth2Client("graph"))).andReturn().getResponse();

        Assertions.assertEquals(404, response.getStatus());
    }
}