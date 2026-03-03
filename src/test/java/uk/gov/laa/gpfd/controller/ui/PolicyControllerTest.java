package uk.gov.laa.gpfd.controller.ui;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.laa.gpfd.utils.BaseMvcTest;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT ,
        classes = uk.gov.laa.gpfd.config.TestDatabaseConfig .class)
@AutoConfigureMockMvc
@ActiveProfiles("testauth")
class PolicyControllerTest extends BaseMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void cookiesPageResolvesToCookiesHtml() throws Exception {
       performAuthenticatedGet("/cookies", List.of("Financial"))
                .andExpect(status().isOk())
                .andExpect(view().name("cookies"))
                .andExpect(model().attribute("gpfdUrl", "http://localhost"));
    }

    @Test
    void privacyPageResolvesToPrivacyHtml() throws Exception {
        performAuthenticatedGet("/privacy", List.of("Financial"))
                .andExpect(status().isOk())
                .andExpect(view().name("privacy"))
                .andExpect(model().attribute("gpfdUrl", "http://localhost"));
    }

    @Test
    void accessibilityPageResolvesToAccessibilityHtml() throws Exception {
        performAuthenticatedGet("/accessibility", List.of("Financial"))
                .andExpect(status().isOk())
                .andExpect(view().name("accessibility"))
                .andExpect(model().attribute("gpfdUrl", "http://localhost"));
    }

}