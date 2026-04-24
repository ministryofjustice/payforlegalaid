package uk.gov.laa.gpfd.controller.ui;

import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CspReportController.class)
@WithMockUser
class CspReportControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MeterRegistry meterRegistry;

    private static final String VALID_REPORT = """
            {
              "csp-report": {
                "document-uri": "http://localhost:8080",
                "violated-directive": "script-src",
                "blocked-uri": "eval"
              }
            }
            """;

    private double violationCount() {
        var counter = meterRegistry.find("csp_violations_total").counter();
        return counter == null ? 0 : counter.count();
    }

    @Test
    void shouldReturn204AndIncrementCounterForValidCspReport() throws Exception {
        double before = violationCount();

        mockMvc.perform(post("/csp-report")
                        .with(csrf())
                        .contentType("application/csp-report")
                        .content(VALID_REPORT))
                .andExpect(status().isNoContent());

        assertThat(violationCount()).isEqualTo(before + 1);
    }

    @Test
    void shouldReturn415ForWrongContentType() throws Exception {
        mockMvc.perform(post("/csp-report")
                        .with(csrf())
                        .contentType("text/plain")
                        .content(VALID_REPORT))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void shouldReturn405ForGetRequest() throws Exception {
        mockMvc.perform(get("/csp-report"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void shouldHandleEmptyJsonBodyAndIncrementCounter() throws Exception {
        double before = violationCount();

        mockMvc.perform(post("/csp-report")
                        .with(csrf())
                        .contentType("application/csp-report")
                        .content("{}"))
                .andExpect(status().isNoContent());

        assertThat(violationCount()).isEqualTo(before + 1);
    }

    @Test
    void shouldIncrementCounterEvenWithNoBody() throws Exception {
        double before = violationCount();

        mockMvc.perform(post("/csp-report")
                        .with(csrf())
                        .contentType("application/csp-report"))
                .andExpect(status().isNoContent());

        assertThat(violationCount()).isEqualTo(before + 1);
    }

    @Test
    void shouldHandleFullBrowserCspPayload() throws Exception {
        mockMvc.perform(post("/csp-report")
                        .with(csrf())
                        .contentType("application/csp-report")
                        .content("""
                        {
                          "csp-report": {
                            "document-uri": "http://localhost:8080",
                            "referrer": "",
                            "violated-directive": "connect-src",
                            "effective-directive": "connect-src",
                            "original-policy": "connect-src 'self'",
                            "blocked-uri": "https://evil.com",
                            "status-code": 0
                          }
                        }
                        """))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldTruncateLongBlockedUri() throws Exception {
        String longUri = "https://evil.com/" + "a".repeat(200);

        mockMvc.perform(post("/csp-report")
                        .with(csrf())
                        .contentType("application/csp-report")
                        .content("""
                        {
                          "csp-report": {
                            "violated-directive": "script-src",
                            "blocked-uri": "%s"
                          }
                        }
                        """.formatted(longUri)))
                .andExpect(status().isNoContent());
    }
}