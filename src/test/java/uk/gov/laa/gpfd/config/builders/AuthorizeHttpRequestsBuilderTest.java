package uk.gov.laa.gpfd.config.builders;

import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.FilterChainProxy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.laa.gpfd.Application;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
class AuthorizeHttpRequestsBuilderTest {

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    FilterChainProxy springSecurityFilterChain;

    @BeforeEach
    void setup() {
        mockMvc = webAppContextSetup(webApplicationContext)
                .addFilter(springSecurityFilterChain)
                .build();
    }

    @Test
    void shouldSwaggerUIAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldSwaggerYamlAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/swagger.yml"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldOpenAPIDocsDoesNotExist() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldActuatorEndpointsAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/actuator"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldInfoEndpointAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/actuator/info"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldMetricsEndpointAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNonAuthenticatedUserCannotAccessRestrictedPage() throws Exception {
        mockMvc.perform(get("/reports"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void shouldUnauthenticatedAccessDeniedToOtherEndpoints() throws Exception {
        mockMvc.perform(get("/private-endpoint"))
                .andExpect(status().is3xxRedirection());  // Should be forbidden without authentication
    }

    @Test
    void shouldSwaggerUIAccessibleAfterLogin() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html")
                        .with(user("admin").password("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldActuatorAndSwaggerConfiguredForPermitAll() throws Exception {
        mockMvc.perform(get("/actuator"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUnauthorizedAccessToOtherPagesBeBlocked() throws Exception {
        mockMvc.perform(get("/private-endpoint"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void shouldSwaggerUIPageAccessWithoutCredentials() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldActuatorEndpointsWithCorrectRole() throws Exception {
        mockMvc.perform(get("/actuator/info")
                        .with(user("admin").password("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldSwaggerUIExemptFromAuthentication() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldOtherEndpointsSecuredWithAuthentication() throws Exception {
        mockMvc.perform(get("/other-endpoint"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void shouldSwaggerUIForAllUsers() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldSwaggerUIAccessibleForAuthenticatedUsers() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html")
                        .with(user("admin").password("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldCustomSecurityConfigurationApplied() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/private-endpoint"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void shouldSwaggerUIExemptFromAuthenticationForAllUsers() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }
}