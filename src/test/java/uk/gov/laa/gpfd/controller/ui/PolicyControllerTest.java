package uk.gov.laa.gpfd.controller.ui;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.laa.gpfd.utils.GpfdUrlClient;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("testauth")
class PolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private GpfdUrlClient gpfdUrlClient;

    @Test
    @WithMockUser(roles = "ADMIN")
    void cookiesPageResolvesToCookiesHtml() throws Exception {
        mockMvc.perform(get("/cookies"))
                .andExpect(status().isOk())
                .andExpect(view().name("cookies"))
                .andExpect(model().attribute("gpfdUrl", "http://localhost"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void privacyPageResolvesToPrivacyHtml() throws Exception {
        mockMvc.perform(get("/privacy"))
                .andExpect(status().isOk())
                .andExpect(view().name("privacy"))
                .andExpect(model().attribute("gpfdUrl", "http://localhost"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void accessibilityPageResolvesToAccessibilityHtml() throws Exception {
        mockMvc.perform(get("/accessibility"))
                .andExpect(status().isOk())
                .andExpect(view().name("accessibility"))
                .andExpect(model().attribute("gpfdUrl", "http://localhost"));
    }
}