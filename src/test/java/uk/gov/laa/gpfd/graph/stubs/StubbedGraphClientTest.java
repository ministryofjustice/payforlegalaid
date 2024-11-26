package uk.gov.laa.gpfd.graph.stubs;

import com.microsoft.graph.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import uk.gov.laa.gpfd.graph.StubbedGraphClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StubbedGraphClientTest {

    private static final StubbedGraphClient OBJECT_UNDER_TEST = new StubbedGraphClient();

    @Test
    void stubbedTestGraphUserShouldAlwaysReturnStaticUser() {
        // Given
        final OAuth2AuthorizedClient oAuth2AuthorizedClient = null;

        // When
        User user = OBJECT_UNDER_TEST.getGraphUserDetails(oAuth2AuthorizedClient);

        // Then
        assertNotNull(user, "The returned user should not be null");
        assertEquals("12ff941e-3653-405e-988d-449137e39b55", user.id, "User ID should match the stubbed value");
        assertEquals("foo-bar@foo.bar.gov.uk", user.userPrincipalName, "User principal name should match");
        assertEquals("Foo", user.givenName, "User given name should match");
        assertEquals("Bar", user.surname, "User surname should match");
        assertEquals("testPreferredName", user.preferredName, "User preferred name should match");
        assertEquals("foo-bar@foo.bar.gov.uk", user.mail, "User email should match");
    }

}