package uk.gov.laa.gpfd.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import uk.gov.laa.gpfd.config.builders.HttpSecuritySessionManagementConfigurerBuilder;
import uk.gov.laa.gpfd.security.SilasRoles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

class MockAuthSecurityConfigTest {

    private final WebApplicationContextRunner runner = new WebApplicationContextRunner()
            .withUserConfiguration(TestConfiguration.class)
            .withPropertyValues(
                    "spring.profiles.active=dev,mockauth",
                    "gpfd.security.mock-auth.enabled=true",
                    "gpfd.security.mock-auth.namespace=laa-get-payments-finance-data-dev");

                @ParameterizedTest
                @ValueSource(strings = {"dev,mockauth", "mockauth,dev"})
                void loadsRealProfilesWithoutEntraConfiguration(String profiles) {
                runner.withInitializer(new ConfigDataApplicationContextInitializer())
                    .withConfiguration(AutoConfigurations.of(OAuth2ClientAutoConfiguration.class))
                    .withPropertyValues("spring.profiles.active=" + profiles,
                        "spring.config.location=file:target/classes/")
                    .run(context -> {
                        assertThat(context).hasNotFailed().doesNotHaveBean(ClientRegistrationRepository.class);
                        assertThat(context.getEnvironment().getProperty(
                            "spring.security.oauth2.client.registration.gpfd-azure-dev.client-secret")).isNull();
                        assertThat(context.getEnvironment().getProperty("spring.autoconfigure.exclude[0]"))
                            .isEqualTo(OAuth2ClientAutoConfiguration.class.getName());
                        assertThat(context.getEnvironment().getProperty("server.forward-headers-strategy"))
                            .isEqualTo("none");
                    });
                }

                @Test
                void normalProfileRetainsOauthAndDeniesMockLoginEvenWhenEnabled() {
                var registration = ClientRegistration.withRegistrationId("entra")
                    .clientId("test-client").clientSecret("synthetic-test-secret")
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                    .authorizationUri("https://identity.example/authorize")
                    .tokenUri("https://identity.example/token")
                    .userInfoUri("https://identity.example/userinfo")
                    .userNameAttributeName("sub").build();
                runner.withPropertyValues("spring.profiles.active=dev")
                    .withBean(ClientRegistrationRepository.class,
                        () -> new InMemoryClientRegistrationRepository(registration))
                    .run(context -> {
                        assertThat(context).doesNotHaveBean(MockAuthSecurityConfig.class);
                        var mvc = mockMvc(context);
                        mvc.perform(get("/login")).andExpect(status().isOk());
                        var redirect = mvc.perform(get("/oauth2/authorization/entra"))
                            .andExpect(status().isFound()).andReturn().getResponse().getRedirectedUrl();
                        assertThat(redirect).startsWith("https://identity.example/authorize?");
                    });
                }

    @Test
    void protectedRequestsUseSyntheticOidcIdentity() {
        runner.run(context -> {
            assertThat(context).doesNotHaveBean(ClientRegistrationRepository.class);
            var mvc = mockMvc(context);
            var result = mvc.perform(get("/private"))
                    .andExpect(status().isOk())
                    .andReturn();
            var body = result.getResponse().getContentAsString();
            assertThat(UUID.fromString(body.substring(0, body.indexOf(':')))).isNotNull();
            assertThat(body).endsWith(":" + String.join(",", SilasRoles.all()));
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "gpfd.security.mock-auth.enabled=false",
            "gpfd.security.mock-auth.namespace=other"
    })
    void deniesLoginWhenAnyGuardFails(String property) {
        runner.withPropertyValues(property).run(context -> {
            var mvc = mockMvc(context);
            mvc.perform(get("/login").header("X-Forwarded-For", "192.0.2.10"))
                    .andExpect(status().isForbidden());
            mvc.perform(post("/login").with(csrf())).andExpect(status().isForbidden());
            mvc.perform(get("/private")).andExpect(status().isForbidden());
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"uat", "prod"})
    void failsStartupForProtectedProfiles(String profile) {
        runner.withPropertyValues("spring.profiles.active=dev,mockauth," + profile,
                        "gpfd.security.mock-auth.enabled=false")
                .run(context -> assertThat(context).hasFailed()
                        .getFailure().hasStackTraceContaining("mockauth must not be active with uat or prod"));
    }

    private MockMvc mockMvc(WebApplicationContext context) {
        return webAppContextSetup(context).apply(springSecurity()).build();
    }

    @org.springframework.boot.test.context.TestConfiguration(proxyBeanMethods = false)
    @EnableWebSecurity
    @EnableWebMvc
    @Import({SecurityConfig.class, CsrfConfig.class, HttpSecuritySessionManagementConfigurerBuilder.class,
            MockAuthAccessPolicy.class, MockAuthSecurityConfig.class, PrivateController.class})
    static class TestConfiguration {
        @Bean
        ContextBasedAuthorizationManager authManager() {
            return new ContextBasedAuthorizationManager();
        }
    }

    @RestController
    static class PrivateController {
        @GetMapping("/private")
        String privateEndpoint(Authentication authentication) {
            var principal = (OidcUser) authentication.getPrincipal();
            return principal.getAttribute("oid") + ":"
                    + String.join(",", principal.<List<String>>getAttribute("LAA_APP_ROLES"));
        }
    }
}