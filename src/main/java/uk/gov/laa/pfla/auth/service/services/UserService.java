package uk.gov.laa.pfla.auth.service.services;

import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import uk.gov.laa.pfla.auth.service.beans.UserDetails;
import uk.gov.laa.pfla.auth.service.exceptions.AuthUserNotFoundException;
import uk.gov.laa.pfla.auth.service.exceptions.UserServiceException;
import uk.gov.laa.pfla.auth.service.graph.GraphClientHelper;

/**
 * Component to encapsulate the retrieval of user information from an
 * external directory (Microsoft Graph)
 */
@Component
public class UserService {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

  private final GraphClientHelper graphClientHelper;

  public UserService(GraphClientHelper graphClientHelper) {
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
      if( graphUser == null || !StringUtils.hasText(graphUser.userPrincipalName) ) {
        LOGGER.error("Null or insufficient User data returned from Graph: {}", graphUser);
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
