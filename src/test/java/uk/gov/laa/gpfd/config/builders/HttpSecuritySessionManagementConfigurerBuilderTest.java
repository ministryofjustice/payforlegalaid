package uk.gov.laa.gpfd.config.builders;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.laa.gpfd.builders.ReportResponseTestBuilder;
import uk.gov.laa.gpfd.services.ReportManagementService;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("testauth")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = uk.gov.laa.gpfd.config.TestDatabaseConfig.class)
class HttpSecuritySessionManagementConfigurerBuilderTest {

    @MockitoBean
    ReportManagementService reportServiceMock;

    @Autowired
    MockMvc mockMvc;

    @Test
    void shouldNotHaveAccessToSecureEndpointAfterSessionExpires() throws Exception {
        var reportId = UUID.fromString("0d4da9ec-b0b3-4371-af10-f375330d85d1");
        var reportResponseMock = new ReportResponseTestBuilder().withId(reportId).createReportResponse();

        when(reportServiceMock.createReportResponse(reportId)).thenReturn(reportResponseMock);

        mockMvc.perform(get("/reports/{id}", reportId)
                        .sessionAttr("SPRING_SECURITY_CONTEXT", "null"))
                .andExpect(status().is3xxRedirection())// Should redirect after session expires
                .andExpect(header().string("Location", "http://localhost/oauth2/authorization/gpfd-azure-dev"));  // Check that redirection goes to /login?expired
    }

    @Test
    void shouldValidSessionDoesNotRedirect() throws Exception {
        var reportId = UUID.fromString("0d4da9ec-b0b3-4371-af10-f375330d85d1");
        var reportResponseMock = new ReportResponseTestBuilder().withId(reportId).createReportResponse();

        when(reportServiceMock.createReportResponse(reportId)).thenReturn(reportResponseMock);

        mockMvc.perform(get("/reports/{id}", reportId).with(oidcLogin()
                .idToken(token -> token.claim("LAA_APP_ROLES", List.of("REP000")))
        ))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(reportId.toString()));
    }
}
