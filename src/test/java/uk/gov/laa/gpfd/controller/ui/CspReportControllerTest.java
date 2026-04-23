package uk.gov.laa.gpfd.controller.ui;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Client;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("testauth")
class CspReportControllerTest {

    @Autowired
    MockMvc mockMvc;

    private static final String VALID_REPORT = """
            {
              "csp-report": {
                "document-uri": "http://localhost:8080",
                "violated-directive": "script-src",
                "blocked-uri": "eval"
              }
            }
            """;

    @Test
    void shouldReturn204ForValidCspReport() throws Exception {
        mockMvc.perform(post("/csp-report")
                        .with(oidcLogin())
                        .with(oauth2Client("graph"))
                        .with(csrf())
                        .contentType("application/csp-report")
                        .content(VALID_REPORT))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn415ForWrongContentType() throws Exception {
        mockMvc.perform(post("/csp-report")
                        .with(oidcLogin())
                        .with(oauth2Client("graph"))
                        .with(csrf())
                        .contentType("text/plain")
                        .content(VALID_REPORT))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void shouldReturn405ForGetRequest() throws Exception {
        mockMvc.perform(get("/csp-report")
                .with(oidcLogin())
                .with(oauth2Client("graph")))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void shouldHandleEmptyJsonBody() throws Exception {
        mockMvc.perform(post("/csp-report")
                        .with(oidcLogin())
                        .with(oauth2Client("graph"))
                        .with(csrf())
                        .contentType("application/csp-report")
                        .content("{}"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldHandleFullBrowserCspPayload() throws Exception {
        mockMvc.perform(post("/csp-report")
                        .with(oidcLogin())
                        .with(oauth2Client("graph"))
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
}