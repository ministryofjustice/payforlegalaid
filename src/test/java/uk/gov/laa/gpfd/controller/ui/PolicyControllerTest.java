package uk.gov.laa.gpfd.controller.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.laa.gpfd.utils.BaseMvcTest;
import uk.gov.laa.gpfd.utils.UrlBuilder;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(PolicyController.class)
class PolicyControllerTest extends BaseMvcTest {

    @MockitoBean
    UrlBuilder urlBuilder;

    @BeforeEach
    void beforeEach() {
        when(urlBuilder.getServiceUrl()).thenReturn("http://localhost");
    }

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