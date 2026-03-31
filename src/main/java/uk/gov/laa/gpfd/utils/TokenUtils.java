package uk.gov.laa.gpfd.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import uk.gov.laa.gpfd.exception.UnableToGetAuthGroupException.AuthenticationIsNullException;
import uk.gov.laa.gpfd.exception.UnableToGetAuthGroupException.PrincipalIsNullException;
import uk.gov.laa.gpfd.exception.UnableToGetAuthGroupException.UnexpectedAuthClassException;

import java.util.List;
import java.util.UUID;

public abstract class TokenUtils {

    private TokenUtils() {
     // Can't instantiate utility class
    }

    public static final UUID ID_REP000 = UUID.fromString("523f38f0-2179-4824-b885-3a38c5e149e8");
    public static final UUID ID_REP012 = UUID.fromString("cc55e276-97b0-4dd8-a919-26d4aa373266");
    public static final UUID ID_REP013 = UUID.fromString("aca2120c-8f82-45a8-a682-8dedfb7997a7");
    public static final UUID ID_REP014 = UUID.fromString("55daf3c1-28f0-4260-9396-2ee6d537abab");

    private static final String GROUPS_CLAIM = "groups";

    /**
     * Get the user's group memberships from the auth token.
     * @param authentication - auth details from Spring context
     * @return - list of groups user is in
     */
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
