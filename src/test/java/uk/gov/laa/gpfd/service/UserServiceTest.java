package uk.gov.laa.gpfd.service;

import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import uk.gov.laa.gpfd.data.AzureGraphUserTestDataFactory;
import uk.gov.laa.gpfd.exception.AuthUserNotFoundException;
import uk.gov.laa.gpfd.exception.UserServiceException;
import uk.gov.laa.gpfd.graph.AzureGraphClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
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

    @Test
    void shouldReturnValidUserWhenGraphReturnsCompleteUserDetails() {
        var graphUser = AzureGraphUserTestDataFactory.aValidUserSmallUser();
        when(mockAzureGraphClient.getGraphUserDetails(mockOAuth2Client)).thenReturn(graphUser);

        var result = userService.getUserDetails(mockOAuth2Client);

        assertEquals(graphUser.userPrincipalName, result.userPrincipalName());
        assertEquals(graphUser.givenName, result.givenName());
        assertEquals(graphUser.surname, result.surname());
        assertEquals(graphUser.preferredName, result.preferredName());
        assertEquals(graphUser.mail, result.email());
    }

    @Test
    void shouldThrowAuthUserNotFoundExceptionWhenGraphReturnsNullUser() {
        assertThrows(AuthUserNotFoundException.class, () -> userService.getUserDetails(mockOAuth2Client));
    }

    @Test
    void shouldThrowUserServiceExceptionWhenClientExceptionOccurs() {
        when(mockAzureGraphClient.getGraphUserDetails(mockOAuth2Client)).thenThrow(new ClientException("client error", null));

        // Then
        assertThrows(UserServiceException.class, () -> userService.getUserDetails(mockOAuth2Client));
    }

    @Test
    void shouldReturnErrorMessageWhenNoDetailsAreReturnedFromGraph() {
        // Given
        when(mockAzureGraphClient.getGraphUserDetails(mockOAuth2Client)).thenReturn(null);

        // When
        var exception = assertThrows(AuthUserNotFoundException.class, () -> userService.getUserDetails(mockOAuth2Client));

        // Then
        assertEquals("No user details returned from Graph", exception.getMessage());
    }

    @Test
    void shouldThrowAuthUserNotFoundExceptionWhenGraphUserDataIsIncomplete() {
        // Given
        var incompleteUser = new User(); // Missing userPrincipalName and email
        when(mockAzureGraphClient.getGraphUserDetails(mockOAuth2Client)).thenReturn(incompleteUser);

        // When & Assert
        var exception = assertThrows(AuthUserNotFoundException.class, () -> userService.getUserDetails(mockOAuth2Client));

        // Then
        assertEquals("No user details returned from Graph", exception.getMessage());
    }

    @Test
    void shouldThrowUserServiceExceptionWhenGraphClientThrowsError() {
        // Given
        when(mockAzureGraphClient.getGraphUserDetails(mockOAuth2Client)).thenThrow(new ClientException("Error", null));

        // When & Assert
        var exception = assertThrows(UserServiceException.class, () -> userService.getUserDetails(mockOAuth2Client));

        // Then
        assertEquals("Failed to retrieve Graph User", exception.getMessage());
        assertInstanceOf(ClientException.class, exception.getCause());
    }

    @Test
    void shouldThrowAuthUserNotFoundExceptionWhenOAuth2ClientIsNull() {
        // When
        // Then
        assertThrows(AuthUserNotFoundException.class, () -> userService.getUserDetails(null));
    }

    @Test
    void shouldThrowAuthUserNotFoundExceptionWhenUserPrincipalNameIsEmpty() {
        // Given
        var invalidUser = new User();
        invalidUser.userPrincipalName = "";
        when(mockAzureGraphClient.getGraphUserDetails(mockOAuth2Client)).thenReturn(invalidUser);

        // When
        var exception = assertThrows(AuthUserNotFoundException.class, () -> userService.getUserDetails(mockOAuth2Client));

        // Then
        assertEquals("No user details returned from Graph", exception.getMessage());
    }

    @Test
    void shouldMapOtherFieldsWhenUserHasNoGivenName() {
        // Given
        var partialUser = AzureGraphUserTestDataFactory.aValidUserSmallUser();
        partialUser.givenName = null;
        when(mockAzureGraphClient.getGraphUserDetails(mockOAuth2Client)).thenReturn(partialUser);

        // When
        var result = userService.getUserDetails(mockOAuth2Client);

        // Then
        assertNull(result.givenName());
        assertEquals(partialUser.userPrincipalName, result.userPrincipalName());
        assertEquals(partialUser.surname, result.surname());
    }

    @Test
    void shouldMapOtherFieldsWhenUserHasNoSurname() {
        // Given
        var partialUser = AzureGraphUserTestDataFactory.aValidUserSmallUser();
        partialUser.surname = null;
        when(mockAzureGraphClient.getGraphUserDetails(mockOAuth2Client)).thenReturn(partialUser);

        // When
        var result = userService.getUserDetails(mockOAuth2Client);

        // Then
        assertNull(result.surname());
        assertEquals(partialUser.userPrincipalName, result.userPrincipalName());
        assertEquals(partialUser.givenName, result.givenName());
    }

    @Test
    void shouldThrowAuthUserNotFoundExceptionWhenUserPropertiesAreMissing() {
        // Given
        var incompleteUser = new User(); // Completely empty user object
        when(mockAzureGraphClient.getGraphUserDetails(mockOAuth2Client)).thenReturn(incompleteUser);

        // When
        var exception = assertThrows(AuthUserNotFoundException.class, () -> userService.getUserDetails(mockOAuth2Client));

        // Then
        assertEquals("No user details returned from Graph", exception.getMessage());
    }

    @Test
    void shouldUsePreferredNameWhenGivenNameIsNull() {
        // Given
        var userWithNullGivenName = AzureGraphUserTestDataFactory.aValidUser();
        userWithNullGivenName.givenName = null;
        when(mockAzureGraphClient.getGraphUserDetails(mockOAuth2Client)).thenReturn(userWithNullGivenName);

        // When
        var result = userService.getUserDetails(mockOAuth2Client);

        // Then
        assertEquals(userWithNullGivenName.preferredName, result.preferredName());
    }
}
