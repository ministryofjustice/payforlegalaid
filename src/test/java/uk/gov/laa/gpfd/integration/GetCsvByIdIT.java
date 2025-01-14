package uk.gov.laa.gpfd.integration;

import com.microsoft.graph.models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import uk.gov.laa.gpfd.graph.AzureGraphClient;
import uk.gov.laa.gpfd.utils.FileUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Client;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@SpringBootTest
@Import(OAuth2TestConfig.class)
class GetCsvByIdIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate writeJdbcTemplate;

    @MockitoBean
    AzureGraphClient mockAzureGraphClient;

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
        writeJdbcTemplate.update("DROP SEQUENCE GPFD_TRACKING_TABLE_SEQUENCE");
        writeJdbcTemplate.update("TRUNCATE TABLE GPFD.CSV_TO_SQL_MAPPING_TABLE");
    }

    @Test
    void shouldReturnCsvWithMatchingId() throws Exception {
        User user = new User();
        user.userPrincipalName = "Test User";

        when(mockAzureGraphClient.getGraphUserDetails(any())).thenReturn(user);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/csv/1")
                .with(oidcLogin()
                        .idToken(token -> token.subject("mockUser")))
                .with(oauth2Client("graph"))).andReturn().getResponse();

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("attachment; filename=CCMS_invoice_analysis-CIS-to-CCMS-import-analysis-2.csv", response.getHeader("Content-Disposition"));
    }

    @Test
    void shouldReturn400WhenGivenInvalidId() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/csv/1001")
                .with(oidcLogin()
                        .idToken(token -> token.subject("mockUser")))
                .with(oauth2Client("graph"))).andReturn().getResponse();

        Assertions.assertEquals(400, response.getStatus());
    }

    @Test
    void shouldReturn404WhenNoReportsFound() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/csv/999")
                .with(oidcLogin()
                        .idToken(token -> token.subject("mockUser")))
                .with(oauth2Client("graph"))).andReturn().getResponse();

        Assertions.assertEquals(404, response.getStatus());
    }
}