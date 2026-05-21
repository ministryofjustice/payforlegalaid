package uk.gov.laa.gpfd.integration;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.laa.gpfd.builders.ReportResponseTestBuilder;
import uk.gov.laa.gpfd.services.ReportManagementService;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.laa.gpfd.integration.data.ReportTestData.ReportType.CSV_REPORT;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.allOf;
import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigIT extends BaseIT {
    @MockitoBean
    ReportManagementService reportManagementService;

    @Test
    void shouldReturnCspReportOnlyHeader() throws Exception {
        var reportId = UUID.fromString(CSV_REPORT.getReportData().id());
        var reportResponseMock = new ReportResponseTestBuilder().withId(reportId).createReportResponse();

        when(reportManagementService.createReportResponse(reportId)).thenReturn(reportResponseMock);

        performGetRequestWithRoles("/reports/" + reportId, List.of("Financial"))
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
        // Redirects because GET /csp-report isn't allowed without login
        mockMvc.perform(get("/csp-report"))
                .andExpect(status().is3xxRedirection());

        performGetRequestWithRoles("/csp-report", List.of("Financial"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void shouldEnforceCsrfProtectionForPostRequests() throws Exception {
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
    void shouldAcceptPostWithValidCsrfToken() throws Exception {
        mockMvc.perform(post("/csp-report")
                        .with(csrf())
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
    void shouldGenerateCsrfTokenForRequest() throws Exception {

        mockMvc.perform(get("/reports")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectPostWithoutCsrfToken() throws Exception {

        mockMvc.perform(post("/some-protected-endpoint"))
                .andExpect(status().isForbidden());
    }
}
