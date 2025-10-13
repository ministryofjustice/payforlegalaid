package uk.gov.laa.gpfd.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import uk.gov.laa.gpfd.exception.UnableToGetAuthGroupException;
import uk.gov.laa.gpfd.exception.UnableToGetAuthGroupException.UnexpectedAuthClassException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenUtilsTest {

    List<String> groupList = List.of("34fdsfh324-fdsfsdaf324-ds", "asjd324jnfdsf", "hdscv2343rvf");

    @Test
    void shouldReturnGroupsFromTokenWhenDefaultOidcUser() {
        var auth = mock(Authentication.class);
        var principal = mock(DefaultOidcUser.class);

        when(auth.getPrincipal()).thenReturn(principal);
        when(principal.getClaimAsStringList("groups")).thenReturn(groupList);

        assertEquals(groupList, TokenUtils.getGroupsFromToken(auth));

    }

    @Test
    void shouldReturnGroupsFromTokenWhenOauth2User() {
        var auth = mock(Authentication.class);
        var principal = mock(OAuth2User.class);

        when(auth.getPrincipal()).thenReturn(principal);
        when(principal.getAttribute("groups")).thenReturn(groupList);

        assertEquals(groupList, TokenUtils.getGroupsFromToken(auth));

    }

    @Test
    void shouldReturnGroupsFromTokenWhenJwt() {
        var auth = mock(Authentication.class);
        var principal = mock(Jwt.class);

        when(auth.getPrincipal()).thenReturn(principal);
        when(principal.getClaimAsStringList("groups")).thenReturn(groupList);

        assertEquals(groupList, TokenUtils.getGroupsFromToken(auth));

    }

    @Test
    void shouldErrorIfPrincipalIsUnsupportedClass() {
        var auth = mock(Authentication.class);
        var principal = mock(User.class);
        when(auth.getPrincipal()).thenReturn(principal);

        var ex = assertThrows(UnexpectedAuthClassException.class, () -> TokenUtils.getGroupsFromToken(auth));
        assertTrue(ex.getMessage().contains("org.springframework.security.core.userdetails.User"));
    }

    @Test
    void shouldErrorIfPrincipalIsUndefined() {
        var auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(null);

        assertThrows(UnableToGetAuthGroupException.class, () -> TokenUtils.getGroupsFromToken(auth));
    }

}