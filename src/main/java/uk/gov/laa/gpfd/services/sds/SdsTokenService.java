package uk.gov.laa.gpfd.services.sds;

import com.azure.core.credential.TokenCredential;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Service for obtaining access tokens for the SDS API.
 * Uses Azure AD (Entra) credentials to authenticate with the SDS service.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "gpfd.sds-enabled.enabled", havingValue = "true")
public class SdsTokenService {

    private final TokenCredential tokenCredential;

    @Value("${app.sds-api.scope:https://graph.microsoft.com/.default}")
    private String sdsApiScope;

    /**
     * Get an access token for the SDS API.
     *
     * @return the access token string
     */
    public String getSdsAccessToken() {
        try {
            var accessToken = tokenCredential
                    .getToken(new com.azure.core.credential.TokenRequestContext().addScopes(sdsApiScope))
                    .block();
            if (accessToken == null) {
                throw new RuntimeException("SDS access token was null");
            }
            return accessToken.getToken();
        } catch (RuntimeException e) {
            log.error("Failed to obtain SDS access token", e);
            throw e;
        }
    }
}


