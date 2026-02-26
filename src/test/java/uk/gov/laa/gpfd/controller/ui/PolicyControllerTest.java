package uk.gov.laa.gpfd.controller.ui;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT ,
        classes = uk.gov.laa.gpfd.config.TestDatabaseConfig .class)
@AutoConfigureMockMvc
@ActiveProfiles("testauth")
class PolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void cookiesPageResolvesToCookiesHtml() throws Exception {
        mockMvc.perform(get("/cookies").with(adminUser()))
                .andExpect(status().isOk())
                .andExpect(view().name("cookies"))
                .andExpect(model().attribute("gpfdUrl", "http://localhost"));
    }

    @Test
    void privacyPageResolvesToPrivacyHtml() throws Exception {
        mockMvc.perform(get("/privacy").with(adminUser()))
                .andExpect(status().isOk())
                .andExpect(view().name("privacy"))
                .andExpect(model().attribute("gpfdUrl", "http://localhost"));
    }

    @Test
    void accessibilityPageResolvesToAccessibilityHtml() throws Exception {
        mockMvc.perform(get("/accessibility").with(adminUser()))
                .andExpect(status().isOk())
                .andExpect(view().name("accessibility"))
                .andExpect(model().attribute("gpfdUrl", "http://localhost"));
    }

    private RequestPostProcessor adminUser() {
        return oidcLogin().idToken(token -> token.claim("LAA_APP_ROLES", List.of("Financial")) );
    }
}