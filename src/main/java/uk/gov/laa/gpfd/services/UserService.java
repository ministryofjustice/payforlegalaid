package uk.gov.laa.gpfd.services;

import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import uk.gov.laa.gpfd.bean.UserDetails;
import uk.gov.laa.gpfd.exception.AuthUserNotFoundException;
import uk.gov.laa.gpfd.exception.UserServiceException;
import uk.gov.laa.gpfd.graph.GraphClient;

/**
 * Component to encapsulate the retrieval of user information from an
 * external directory (Microsoft Graph)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserService {

    private final GraphClient graphClientHelper;

    /**
     * Retrieve the details for the currently authenticated User, based on the
     * supplied OAuth2AuthorizedClient.
     *
     * @param client a client which is authorized to query user details from the external directory
     * @return a populated UserDetails
     * @throws UserServiceException if an error, or no details, are returned from the external directory
     */
    public UserDetails getUserDetails(OAuth2AuthorizedClient client) {
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

        return UserDetails.builder()
                .userPrincipalName(graphUser.userPrincipalName)
                .givenName(graphUser.givenName)
                .surname(graphUser.surname)
                .preferredName(graphUser.preferredName)
                .email(graphUser.mail)
                .build();
    }

}
