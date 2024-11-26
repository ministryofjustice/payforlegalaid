package uk.gov.laa.gpfd.graph;

import com.microsoft.graph.models.User;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Component;

import static com.microsoft.graph.requests.GraphServiceClient.builder;

@Component
@Profile("!local")
public final class AzureGraphClient implements GraphClient {

    @Override
    public User getGraphUserDetails(OAuth2AuthorizedClient graphAuthorizedClient) {
        return builder()
                .authenticationProvider(new GraphAuthenticationProvider(graphAuthorizedClient))
                .buildClient()
                .me()
                .buildRequest()
                .get();
    }

}