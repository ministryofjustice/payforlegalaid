package uk.gov.laa.gpfd.services;

import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import uk.gov.laa.gpfd.beans.UserDetails;
import uk.gov.laa.gpfd.exceptions.AuthUserNotFoundException;
import uk.gov.laa.gpfd.exceptions.UserServiceException;
import uk.gov.laa.gpfd.graph.GraphClient;

/**
 * Component to encapsulate the retrieval of user information from an
 * external directory (Microsoft Graph)
 */
@Component
@Slf4j
public class UserService {

    private final GraphClient graphClientHelper;

    public UserService(GraphClient graphClientHelper) {
        this.graphClientHelper = graphClientHelper;
    }

    /**
     * Retrieve the details for the currently authenticated User, based on the
     * supplied OAuth2AuthorizedClient.
     *
     * @param client a client which is authorized to query user details from the external directory
     * @return a populated UserDetails
     * @throws UserServiceException if an error, or no details, are returned from the external directory
     */
    public UserDetails getUserDetails(OAuth2AuthorizedClient client) throws UserServiceException {
        User graphUser;
        try {
            graphUser = graphClientHelper.getGraphUserDetails(client);
            if (graphUser == null || !StringUtils.hasText(graphUser.userPrincipalName)) {
                log.error("Null or insufficient User data returned from Graph: {}", graphUser);
                throw new AuthUserNotFoundException("No user details returned from Graph");
            }
        } catch (ClientException e) {
            throw new UserServiceException("Failed to retrieve Graph User", e);
        }

        UserDetails userDetails = new UserDetails();
        userDetails.setUserPrincipalName(graphUser.userPrincipalName);
        userDetails.setGivenName(graphUser.givenName);
        userDetails.setSurname(graphUser.surname);
        userDetails.setPreferredName(graphUser.preferredName);
        userDetails.setEmail(graphUser.mail);

        return userDetails;
    }

}
