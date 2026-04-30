package uk.gov.laa.gpfd.utils;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import uk.gov.laa.gpfd.exception.UnableToParseAuthDetailsException.AuthenticationIsNullException;
import uk.gov.laa.gpfd.exception.UnableToParseAuthDetailsException.NoAttributesOnTokenException;
import uk.gov.laa.gpfd.exception.UnableToParseAuthDetailsException.NoOidSetOnTokenException;
import uk.gov.laa.gpfd.exception.UnableToParseAuthDetailsException.NoRolesException;
import uk.gov.laa.gpfd.exception.UnableToParseAuthDetailsException.NoRolesInAttributeException;
import uk.gov.laa.gpfd.exception.UnableToParseAuthDetailsException.PrincipalIsNullException;
import uk.gov.laa.gpfd.exception.UnableToParseAuthDetailsException.UnexpectedAuthClassException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.stream.Stream.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityUtilsTest {

    @Mock
    private OidcUser oidcUser;

    @Mock
    private OAuth2AuthenticationToken authentication;

    private final SecurityUtils securityUtils = new SecurityUtils();

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
        reset(oidcUser, authentication);
    }

    @AfterAll
    static void cleanupAtEnd() {
        // Ensure these tests doesn't affect other test files
        SecurityContextHolder.clearContext();
    }

    @Test
    void extractRoles_throwsException_whenNoAuthentication() {
        SecurityContextHolder.clearContext();
        assertThrows(AuthenticationIsNullException.class, securityUtils::extractRoles);
    }

    @Test
    void extractRoles_throwsException_whenPrincipalIsNotOidcUser() {
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn("not-an-oidc-user");

        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThrows(UnexpectedAuthClassException.class, securityUtils::extractRoles);
    }

    @Test
    void extractRoles_throwsException_whenPrincipalIsNull() {
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(null);

        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThrows(PrincipalIsNullException.class, securityUtils::extractRoles);
    }

    @Test
    void extractRoles_parsesListOfRolesCorrectly() {
        when(oidcUser.getAttributes()).thenReturn(Map.of("LAA_APP_ROLES", List.of("REP000", "Financial", "Reconciliation")));
        when(authentication.getPrincipal()).thenReturn(oidcUser);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        List<String> roles = securityUtils.extractRoles();

        assertEquals(List.of("REP000", "Financial", "Reconciliation"), roles);
    }

    @Test
    void extractRoles_parsesCommaSeparatedStringCorrectly() {
        when(oidcUser.getAttributes()).thenReturn(Map.of("LAA_APP_ROLES", "REP000, Financial, Reconciliation"));
        when(authentication.getPrincipal()).thenReturn(oidcUser);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        List<String> roles = securityUtils.extractRoles();

        assertEquals(List.of("REP000", "Financial", "Reconciliation"), roles);
    }

    @Test
    void extractRoles_throwsException_whenAttributesAreNull() {
        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getAttributes()).thenReturn(null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThrows(NoAttributesOnTokenException.class, securityUtils::extractRoles);
    }

    @Test
    void extractRoles_throwsException_whenAttributesDoNotContainLAARoles() {
        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getAttributes()).thenReturn(Map.of("stuff", "things", "otherEntry", "differentthings,inhere"));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThrows(NoRolesInAttributeException.class, securityUtils::extractRoles);
    }

    @ParameterizedTest
    @MethodSource("emptyRoleProvider")
    void extractRoles_throwsException_whenRoleSuppliedIsEmpty(Object emptyRole) {
        when(authentication.getPrincipal()).thenReturn(oidcUser);
        Map<String, Object> emptyRoleMap = new HashMap<>();
        emptyRoleMap.put("LAA_APP_ROLES", emptyRole);
        when(oidcUser.getAttributes()).thenReturn(emptyRoleMap);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThrows(NoRolesException.class, securityUtils::extractRoles);
    }

    private static Stream<Object> emptyRoleProvider() {
        return of(
                List.of(),
                "",
                null
        );
    }

    @Test
    void extractRoles_throwsException_whenRoleSuppliedIsEmptyStringList() {
        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getAttributes()).thenReturn(Map.of("LAA_APP_ROLES", ""));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThrows(NoRolesException.class, securityUtils::extractRoles);
    }


    @Test
    void isAuthorized_returnsTrue_whenUserHasAtLeastOneRequiredRole() {
        assertTrue(securityUtils.isAuthorized(
                List.of("A", "B"),
                List.of("C", "B")
        ));
    }

    @Test
    void isAuthorized_returnsFalse_whenUserHasNoRequiredRoles() {
        assertFalse(securityUtils.isAuthorized(
                List.of("A", "B"),
                List.of("C", "D")
        ));
    }

    @Test
    void extractUserId_getsUserIdFromToken() {
        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getAttribute("oid")).thenReturn("b46b6740-685d-4453-9264-ee61d2ecb906");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertEquals(UUID.fromString("b46b6740-685d-4453-9264-ee61d2ecb906"), securityUtils.extractUserId());
    }

    @Test
    void extractUserId_throwsExceptionIfAuthIsNull() {
        SecurityContextHolder.clearContext();
        assertThrows(AuthenticationIsNullException.class, securityUtils::extractUserId);
    }

    @Test
    void extractUserId_throwsExceptionIfPrincipalIsNull() {
        when(authentication.getPrincipal()).thenReturn(null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThrows(PrincipalIsNullException.class, securityUtils::extractUserId);
    }

    @Test
    void extractUserId_throwsExceptionIfAuthTypeIsUnexpected() {
        var authenticationWrongType = mock(Authentication.class);
        var principal = mock(User.class);
        when(authenticationWrongType.getPrincipal()).thenReturn(principal);
        SecurityContextHolder.getContext().setAuthentication(authenticationWrongType);

        assertThrows(UnexpectedAuthClassException.class, securityUtils::extractUserId);
    }

    @Test
    void extractUserId_throwsExceptionIfNoOidSet() {
        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getAttribute("oid")).thenReturn(null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThrows(NoOidSetOnTokenException.class, securityUtils::extractUserId);
    }

}
