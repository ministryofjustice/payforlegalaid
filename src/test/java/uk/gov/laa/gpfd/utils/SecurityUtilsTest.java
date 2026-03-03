package uk.gov.laa.gpfd.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class SecurityUtilsTest {

    @Mock
    private OidcUser oidcUser;

    @Mock
    private OAuth2AuthenticationToken authentication;

    @InjectMocks
    private SecurityUtils securityUtils;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void extractRoles_returnsEmptyList_whenNoAuthentication() {
        SecurityContextHolder.clearContext();

        List<String> roles = securityUtils.extractRoles();

        assertTrue(roles.isEmpty());
    }

    @Test
    void extractRoles_returnsEmptyList_whenPrincipalIsNotOidcUser() {
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn("not-an-oidc-user");

        SecurityContextHolder.getContext().setAuthentication(auth);

        List<String> roles = securityUtils.extractRoles();

        assertTrue(roles.isEmpty());
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
    void extractRoles_returnsEmptyList_whenAttributesNull() {
        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getAttributes()).thenReturn(null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        List<String> roles = securityUtils.extractRoles();

        assertTrue(roles.isEmpty());
    }
    @Test
    void extractRoles_returnsEmptyList_whenNull() {
        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getAttributes()).thenReturn(null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        List<String> roles = securityUtils.extractRoles();
        assertTrue(roles.isEmpty());
    }

    @Test
    void extractRoles_returnsEmptyList_whenAuthenticationIsNull() {
        SecurityContextHolder.clearContext();
        List<String> roles = securityUtils.extractRoles();
        assertTrue(roles.isEmpty());
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
}
