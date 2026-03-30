package uk.gov.laa.gpfd.config;

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

        ClientRegistration testRegistration = ClientRegistration
            .withRegistrationId("gpfd-azure-dev")
            .clientId("test-client-id")
            .authorizationUri("authorizationUri")
            .tokenUri("tokenUri")
            .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .scope("openid")
            .build();

        return new InMemoryClientRegistrationRepository(testRegistration);
    }

    @Bean
    public OAuth2AuthorizedClientRepository authorizedClientRepository() {
        return new HttpSessionOAuth2AuthorizedClientRepository();
    }
}
