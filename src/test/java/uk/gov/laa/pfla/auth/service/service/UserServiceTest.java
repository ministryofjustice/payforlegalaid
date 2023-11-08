//package uk.gov.laa.pfla.auth.service.service;
//
////import com.microsoft.graph.core.ClientException;
////import com.microsoft.graph.models.User;
////import org.jetbrains.annotations.NotNull;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
//import uk.gov.laa.pfla.auth.service.beans.UserDetails;
//import uk.gov.laa.pfla.auth.service.exceptions.AuthUserNotFoundException;
//import uk.gov.laa.pfla.auth.service.exceptions.UserServiceException;
////import uk.gov.laa.pfla.auth.service.graph.GraphClientHelper;
////import uk.gov.laa.pfla.auth.service.services.UserService;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class UserServiceTest {
//  @Mock
//  OAuth2AuthorizedClient mockOAuth2Client;
//
//  @Mock
//  GraphClientHelper mockGraphClientHelper;
//
//  @InjectMocks
//  UserService userService;
//
//  @NotNull
//  private static User createGraphUser() {
//    User graphUser = new User();
//    graphUser.userPrincipalName = "testPrincipalName";
//    graphUser.givenName = "testGivenName";
//    graphUser.surname = "testSurname";
//    graphUser.preferredName = "testPreferredName";
//    graphUser.mail = "testMail";
//    return graphUser;
//  }
//
//  @Test
//  void whenUserIsReturnedFromGraphUserDetailsAreMappedCorrectly() throws Exception {
//    User graphUser = createGraphUser();
//
//    when(mockGraphClientHelper.getGraphUserDetails(mockOAuth2Client)).thenReturn(graphUser);
//
//    UserDetails result = userService.getUserDetails(mockOAuth2Client);
//
//    assertEquals(graphUser.userPrincipalName, result.getUserPrincipalName());
//    assertEquals(graphUser.givenName, result.getGivenName());
//    assertEquals(graphUser.surname, result.getSurname());
//    assertEquals(graphUser.preferredName, result.getPreferredName());
//    assertEquals(graphUser.mail, result.getEmail());
//  }
//
//  @Test
//  void whenNullGraphUserIsReturnedUserServiceExceptionIsThrown() {
//    assertThrows(AuthUserNotFoundException.class, () -> userService.getUserDetails(mockOAuth2Client));
//  }
//
//  @Test
//  void whenGraphExceptionThenUserServiceExceptionIsThrown() {
//    when(mockGraphClientHelper.getGraphUserDetails(mockOAuth2Client)).thenThrow(new ClientException("client error", null));
//
//    assertThrows(UserServiceException.class, () -> userService.getUserDetails(mockOAuth2Client));
//  }
//}
