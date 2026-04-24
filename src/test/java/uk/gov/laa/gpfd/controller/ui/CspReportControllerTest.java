package uk.gov.laa.gpfd.controller.ui;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.laa.gpfd.utils.BaseMvcTest;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CspReportController.class)
class CspReportControllerTest extends BaseMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MeterRegistry meterRegistry;

    @MockitoBean
    private Counter counter;

    private static final String VALID_REPORT = """
            {
              "csp-report": {
                "document-uri": "http://localhost:8080",
                "violated-directive": "script-src",
                "blocked-uri": "eval"
              }
            }
            """;

    @BeforeEach
    void setUp() {
        when(meterRegistry.counter("csp_violations_total")).thenReturn(counter);
    }

    @Test
    void shouldReturn204AndIncrementCounterForValidCspReport() throws Exception {
        mockMvc.perform(post("/csp-report")
                        .with(oidcLogin())
                        .with(csrf())
                        .contentType("application/csp-report")
                        .content(VALID_REPORT))
                .andExpect(status().isNoContent());

        verify(counter, times(1)).increment();
    }

    @Test
    void shouldReturn415ForWrongContentType() throws Exception {
        mockMvc.perform(post("/csp-report")
                        .with(oidcLogin())
                        .with(csrf())
                        .contentType("text/plain")
                        .content(VALID_REPORT))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void shouldReturn405ForGetRequest() throws Exception {
        mockMvc.perform(get("/csp-report")
                        .with(oidcLogin()))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void shouldHandleEmptyJsonBodyAndIncrementCounter() throws Exception {
        mockMvc.perform(post("/csp-report")
                        .with(oidcLogin())
                        .with(csrf())
                        .contentType("application/csp-report")
                        .content("{}"))
                .andExpect(status().isNoContent());

        verify(counter, times(1)).increment();
    }

    @Test
    void shouldIncrementCounterEvenWithNoBody() throws Exception {
        mockMvc.perform(post("/csp-report")
                        .with(oidcLogin())
                        .with(csrf())
                        .contentType("application/csp-report"))
                .andExpect(status().isNoContent());

        verify(counter, times(1)).increment();
    }

    @Test
    void shouldHandleFullBrowserCspPayload() throws Exception {
        mockMvc.perform(post("/csp-report")
                        .with(oidcLogin())
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

        verify(counter, times(1)).increment();
    }

    @Test
    void shouldTruncateLongBlockedUri() throws Exception {
        String longUri = "https://evil.com/" + "a".repeat(200);

        mockMvc.perform(post("/csp-report")
                        .with(oidcLogin())
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

        verify(counter, times(1)).increment();
    }
}