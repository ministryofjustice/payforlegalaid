package uk.gov.laa.gpfd.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import uk.gov.laa.gpfd.exception.UnableToGetAuthGroupException.AuthenticationIsNullException;
import uk.gov.laa.gpfd.exception.UnableToGetAuthGroupException.PrincipalIsNullException;
import uk.gov.laa.gpfd.exception.UnableToGetAuthGroupException.UnexpectedAuthClassException;

import java.util.List;

public abstract class TokenUtils {

    private TokenUtils() {
     // Can't instantiate utility class
    }

    private static final String GROUPS_CLAIM = "groups";

    public static List<String> getGroupsFromToken(Authentication authentication){

        if (authentication == null){
            throw new AuthenticationIsNullException();
        }
        var principal = authentication.getPrincipal();

        if (principal == null){
            throw new PrincipalIsNullException();
        }

        if (principal instanceof DefaultOidcUser defaultOidcUser){
            return defaultOidcUser.getClaimAsStringList(GROUPS_CLAIM);
        }
        if (principal instanceof OAuth2User oAuth2User) {
            return oAuth2User.getAttribute(GROUPS_CLAIM);
        }
        if (principal instanceof Jwt jwt){
            return jwt.getClaimAsStringList(GROUPS_CLAIM);
        }

        throw new UnexpectedAuthClassException(principal.getClass().getName());
    }

}
