package uk.gov.laa.gpfd.graph;

import com.microsoft.graph.models.User;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;

/**
 * A client interface for interacting with Microsoft Graph API to retrieve user details.
 *
 * <p>The implementation of this interface is responsible for fetching user-related
 * information from the Microsoft Graph API, using an authorized client to authenticate
 * the request. This provides an abstraction layer for Graph API calls.</p>
 *
 * @see com.microsoft.graph.models.User
 * @see org.springframework.security.oauth2.client.OAuth2AuthorizedClient
 */
public sealed interface GraphClient permits AzureGraphClient, StubbedGraphClient {

    /**
     * Retrieves the details of a user from Microsoft Graph.
     *
     * <p>This method uses an instance of {@link OAuth2AuthorizedClient} to authenticate
     * with Microsoft Graph API and fetch user information such as ID, name, email,
     * and other profile attributes.</p>
     *
     * @param graphAuthorizedClient an OAuth2 authorized client containing the access
     *                              token and other authentication details required
     *                              to make requests to Microsoft Graph API.
     *                              <b>Must not be null.</b>
     * @return a {@link User} object containing the user details retrieved from Microsoft
     * Graph. If no user is found, the behavior depends on the implementation
     * (e.g., it might return {@code null} or throw an exception).
     * @throws IllegalArgumentException if {@code graphAuthorizedClient} is null.
     * @throws GraphClientException     if an error occurs while communicating with the
     *                                  Microsoft Graph API.
     */
    User getGraphUserDetails(OAuth2AuthorizedClient graphAuthorizedClient);

}
