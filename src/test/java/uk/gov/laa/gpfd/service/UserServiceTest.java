package uk.gov.laa.gpfd.service;

import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.models.User;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import uk.gov.laa.gpfd.bean.UserDetails;
import uk.gov.laa.gpfd.exception.AuthUserNotFoundException;
import uk.gov.laa.gpfd.exception.UserServiceException;
import uk.gov.laa.gpfd.graph.AzureGraphClient;
import uk.gov.laa.gpfd.services.UserService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  @Mock
  OAuth2AuthorizedClient mockOAuth2Client;

  @Mock
  AzureGraphClient mockAzureGraphClient;

  @InjectMocks
  UserService userService;

  @NotNull
  private static User createGraphUser() {
    User graphUser = new User();
    graphUser.userPrincipalName = "testPrincipalName";
    graphUser.givenName = "testGivenName";
    graphUser.surname = "testSurname";
    graphUser.preferredName = "testPreferredName";
    graphUser.mail = "testMail";
    return graphUser;
  }

  @Test
  void whenUserIsReturnedFromGraphUserDetailsAreMappedCorrectly() throws Exception {
    User graphUser = createGraphUser();

    when(mockAzureGraphClient.getGraphUserDetails(mockOAuth2Client)).thenReturn(graphUser);

    UserDetails result = userService.getUserDetails(mockOAuth2Client);

    assertEquals(graphUser.userPrincipalName, result.userPrincipalName());
    assertEquals(graphUser.givenName, result.givenName());
    assertEquals(graphUser.surname, result.surname());
    assertEquals(graphUser.preferredName, result.preferredName());
    assertEquals(graphUser.mail, result.email());
  }

  @Test
  void whenNullGraphUserIsReturnedUserServiceExceptionIsThrown() {
    assertThrows(AuthUserNotFoundException.class, () -> userService.getUserDetails(mockOAuth2Client));
  }

  @Test
  void whenGraphExceptionThenUserServiceExceptionIsThrown() {
    when(mockAzureGraphClient.getGraphUserDetails(mockOAuth2Client)).thenThrow(new ClientException("client error", null));

    assertThrows(UserServiceException.class, () -> userService.getUserDetails(mockOAuth2Client));
  }
}
