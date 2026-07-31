package uk.gov.laa.gpfd.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.oauth2.client.ClientAuthorizationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;
import uk.gov.laa.gpfd.exception.sds.TokenProviderException;

import java.util.Objects;

/** Responsible for getting access token from OAuth2 provider. */
@Component
@RequiredArgsConstructor
public class TokenProvider {

    static final String CACHE_NAME = "tokenCache";
    static final String CACHE_KEY = "'sdsAccessToken'";

    @Value("${app.sds-api.client-registration-id}")
    private String clientRegistrationId;

    @Value("${app.sds-api.principal-name}")
    private String principalName;

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    /**
     * Get SDS API access token.
     *
     * @return the access token
     */
    @Cacheable(value = CACHE_NAME, key = CACHE_KEY)
    public OAuth2AccessToken getTokenFromProvider() {
        try {
            OAuth2AuthorizedClient authorizedClient =
                    authorizedClientManager.authorize(buildAuthorizeRequest());

            if (Objects.isNull(authorizedClient)) {
                throw new TokenProviderException("Failed to obtain SDS API access token");
            } else {
                Objects.requireNonNull(authorizedClient);
            }

            return authorizedClient.getAccessToken();
        } catch (ClientAuthorizationException clientAuthorizationException) {
            throw new TokenProviderException(clientAuthorizationException.getMessage());
        }
    }

    @CacheEvict(value = CACHE_NAME, key = CACHE_KEY)
    public void evictToken() {}

    private OAuth2AuthorizeRequest buildAuthorizeRequest() {
        return OAuth2AuthorizeRequest.withClientRegistrationId(clientRegistrationId)
                .principal(principalName)
                .build();
    }
}