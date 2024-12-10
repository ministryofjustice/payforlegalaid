package uk.gov.laa.gpfd.graph;

import com.microsoft.graph.authentication.BaseAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;

import java.net.URL;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;

/**
 * AuthenticationProvider to supply an already-retrieved access token
 */
@RequiredArgsConstructor
public class GraphAuthenticationProvider extends BaseAuthenticationProvider {
    private final OAuth2AuthorizedClient graphAuthorizedClient;

    /**
     * Retrieve the token from the authorized client.
     *
     * @param requestUrl the outgoing request URL
     * @return a future with the token
     */
    @Override
    public CompletableFuture<String> getAuthorizationTokenAsync(URL requestUrl) {
        return completedFuture(graphAuthorizedClient.getAccessToken().getTokenValue());
    }
}

