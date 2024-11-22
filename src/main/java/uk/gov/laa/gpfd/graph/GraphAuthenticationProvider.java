package uk.gov.laa.gpfd.graph;

import com.microsoft.graph.authentication.BaseAuthenticationProvider;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;

import java.net.URL;
import java.util.concurrent.CompletableFuture;

/**
 * AuthenticationProvider to supply an already-retrieved access token
 */
public class GraphAuthenticationProvider extends BaseAuthenticationProvider {
  private final OAuth2AuthorizedClient graphAuthorizedClient;

  /**
   * @param graphAuthorizedClient OAuth2AuthorizedClient created by AAD Boot starter.
   */
  public GraphAuthenticationProvider(OAuth2AuthorizedClient graphAuthorizedClient) {
    this.graphAuthorizedClient = graphAuthorizedClient;
  }

  /**
   * Retrieve the token from the authorized client.
   *
   * @param requestUrl the outgoing request URL
   * @return a future with the token
   */
  @Override
  @NonNull
  public CompletableFuture<String> getAuthorizationTokenAsync(@NonNull final URL requestUrl) {
    return CompletableFuture.completedFuture(
        graphAuthorizedClient.getAccessToken().getTokenValue());
  }
}

