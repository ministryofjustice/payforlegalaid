package uk.gov.laa.gpfd.integration;

import com.microsoft.graph.models.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
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
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@SpringBootTest
@Import(OAuth2TestConfig.class)
class ServerSideErrorIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate writeJdbcTemplate;

    @MockBean
    AzureGraphClient mockAzureGraphClient;

    @Test
    void getReportsShouldReturn500WhenCannotConnectToDb() throws Exception {
        MockHttpServletResponse response =  mockMvc.perform(get("/reports")
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        Assertions.assertEquals(500, response.getStatus());
    }

    @Test
    void getReportWithIdShouldReturn500WhenCannotConnectToDb() throws Exception {
        MockHttpServletResponse response =  mockMvc.perform(get("/reports/1")
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        Assertions.assertEquals(500, response.getStatus());
    }

    @Test
    void getCsvWithIdShouldReturn500WhenCannotConnectToDbForMappingTable() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/csv/1")
                .with(oidcLogin()
                        .idToken(token -> token.subject("mockUser")))
                .with(oauth2Client("graph"))).andReturn().getResponse();

        Assertions. assertEquals(500, response.getStatus());
    }

    @Test
    void getCsvWithIdShouldReturn500WhenCannotConnectToDbForReportTable() throws Exception {
        setupGpfdDatabase();
        User user = new User();
        user.userPrincipalName = "Test User";

        when(mockAzureGraphClient.getGraphUserDetails(any())).thenReturn(user);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/csv/1")
                .with(oidcLogin()
                        .idToken(token -> token.subject("mockUser")))
                .with(oauth2Client("graph"))).andReturn().getResponse();

        Assertions. assertEquals(500, response.getStatus());
        resetDatabase();
    }

    private void setupGpfdDatabase() {
        String gpfdSqlSchema = FileUtils.readResourceToString("gpfd_schema.sql");
        String gpfdSqlData = FileUtils.readResourceToString("gpfd_data.sql");
        writeJdbcTemplate.execute(gpfdSqlSchema);
        writeJdbcTemplate.execute(gpfdSqlData);
    }

    private void resetDatabase() {
        writeJdbcTemplate.update("DROP TABLE GPFD.REPORT_TRACKING");
        writeJdbcTemplate.update("DROP SEQUENCE GPFD_TRACKING_TABLE_SEQUENCE");
        writeJdbcTemplate.update("DROP TABLE GPFD.CSV_TO_SQL_MAPPING_TABLE");
    }
}