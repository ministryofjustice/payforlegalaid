package uk.gov.laa.pfla.auth.service.graph;

import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.GraphServiceClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Component;

/**
 * Helper class to handle interactions with Microsoft Graph
 */
@Component
public class GraphClientHelper {

    /**
     * Retrieve the 'me' details from Graph for the currently authenticated User
     * @param graphAuthorizedClient - an OAuth2AuthorizedClient with granted access to read user information from Graph
     * @return User populated with details from Graph, or null if no user details were returned.
     * @throws ClientException if an error occurs while querying the user details
     */
    public User getGraphUserDetails(OAuth2AuthorizedClient graphAuthorizedClient)
            throws ClientException {
        return getGraphServiceClient(graphAuthorizedClient)
                .me()
                .buildRequest()
                .get();
    }

    /**
     * Get a GraphServiceClient using the supplied OAuth2AuthorizedClient.
     * @param graphAuthorizedClient an OAuth2AuthorizedClient with granted access to read user information from Graph
     * @return a GraphServiceClient
     * @throws ClientException if an error occurs while building a GraphServiceClient.
     */
    protected GraphServiceClient<okhttp3.Request> getGraphServiceClient(OAuth2AuthorizedClient graphAuthorizedClient)
            throws ClientException {
        return GraphServiceClient.builder()
                .authenticationProvider(new GraphAuthenticationProvider(graphAuthorizedClient))
                .buildClient();
    }

}