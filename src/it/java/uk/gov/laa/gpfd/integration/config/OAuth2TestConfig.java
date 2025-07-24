package uk.gov.laa.gpfd.integration.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
public class OAuth2TestConfig {

    @MockitoBean
    private RestTemplate restTemplate;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        ClientRegistration graphRegistration = ClientRegistration
                .withRegistrationId("graph")
                .clientId("test-client-id")
                .authorizationUri("authorizationUri")
                .tokenUri("tokenUri")
                .redirectUri("redirectUri")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .build();

        return new InMemoryClientRegistrationRepository(graphRegistration);
    }

    @Bean
    public OAuth2AuthorizedClientRepository authorizedClientRepository() {
        return new HttpSessionOAuth2AuthorizedClientRepository();
    }
}