package uk.gov.laa.gpfd.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.laa.gpfd.builders.ReportResponseTestBuilder;
import uk.gov.laa.gpfd.services.ReportManagementService;
import uk.gov.laa.gpfd.config.TestSecurityConfig;
import uk.gov.laa.gpfd.config.TestDatabaseConfig;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.laa.gpfd.integration.data.ReportTestData.ReportType.CSV_REPORT;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest(classes = {TestSecurityConfig.class, TestDatabaseConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application-test.yml")
class SecurityConfigLocalIT extends BaseIT {

    @MockitoBean
    ReportManagementService reportManagementService;

    // Local profile just ignores Azure and requires no login session.
    @Test
    void shouldNotRedirectToAzureLoginEvenIfNoActiveSession() throws Exception {
        var reportId = UUID.fromString(CSV_REPORT.getReportData().id());
        var reportResponseMock = new ReportResponseTestBuilder().withId(reportId).createReportResponse();

        when(reportManagementService.createReportResponse(reportId)).thenReturn(reportResponseMock);

        mockMvc.perform(get("/reports/{id}", reportId)
                        .sessionAttr("SPRING_SECURITY_CONTEXT", "null"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(reportId.toString()));
    }

    @Test
    void shouldLoadPageIfValidSession() throws Exception {
        var reportId = UUID.fromString(CSV_REPORT.getReportData().id());
        var reportResponseMock = new ReportResponseTestBuilder().withId(reportId).createReportResponse();

        when(reportManagementService.createReportResponse(reportId)).thenReturn(reportResponseMock);

        mockMvc.perform(get("/reports/{id}", reportId))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(reportId.toString()));
    }

    @Test
    public void shouldOnlyAllowSameOriginExternalFrames() throws Exception {
        var reportId = UUID.fromString(CSV_REPORT.getReportData().id());
        var reportResponseMock = new ReportResponseTestBuilder().withId(reportId).createReportResponse();

        when(reportManagementService.createReportResponse(reportId)).thenReturn(reportResponseMock);

        mockMvc.perform(get("/reports/{id}", reportId))
                .andExpect(status().isOk())
                // This is the header that tells the browser what to allow.
                .andExpect(header().string("X-Frame-Options", "SAMEORIGIN"));
    }

    @Test
    void shouldReturnCspReportOnlyHeader() throws Exception {
        var reportId = UUID.fromString(CSV_REPORT.getReportData().id());
        var reportResponseMock = new ReportResponseTestBuilder().withId(reportId).createReportResponse();

        when(reportManagementService.createReportResponse(reportId)).thenReturn(reportResponseMock);

        mockMvc.perform(get("/reports/{id}", reportId))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        "Content-Security-Policy-Report-Only",
                        containsString("script-src 'self'")
                ))
                .andExpect(header().string(
                        "Content-Security-Policy-Report-Only",
                        containsString("report-uri /csp-report")
                ));
    }

    @Test
    void shouldAcceptCspViolationReport() throws Exception {
        mockMvc.perform(post("/csp-report")
                        .contentType("application/csp-report")
                        .content("""
                {
                  "csp-report": {
                    "document-uri": "http://localhost:8080",
                    "violated-directive": "script-src",
                    "blocked-uri": "eval"
                  }
                }
                """))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldRejectCspReportWithWrongContentType() throws Exception {
        mockMvc.perform(post("/csp-report")
                        .contentType("text/plain")
                        .content("{}"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void shouldRejectGetRequestToCspEndpoint() throws Exception {
        mockMvc.perform(get("/csp-report"))
                .andExpect(status().isMethodNotAllowed());
    }
}
