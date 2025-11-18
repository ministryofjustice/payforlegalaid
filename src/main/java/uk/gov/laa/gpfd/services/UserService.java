package uk.gov.laa.gpfd.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

/**
 * Component to encapsulate the retrieval of user information from an
 * external directory (Microsoft Graph)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserService {

    public static final int USERNAME_MAX_LENGTH_IN_DB = 100;

    /**
     * Retrieve the details for the currently authenticated User, based on the
     * supplied OAuth2AuthorizedClient.
     *
     * @param client a client which is authorized to query user details from the external directory
     * @return a populated UserDetails
     */
    public String getCurrentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;
        if (authentication  != null && authentication.getPrincipal() != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof DefaultOidcUser defaultOidcUser) {
                username = defaultOidcUser.getAttribute("preferred_username");
            } else if (principal instanceof org.springframework.security.core.userdetails.User user) {
                username = user.getUsername();
            } else if (principal instanceof OAuth2User oauth2User) {
                username = oauth2User.getName();
            } else if (principal instanceof org.springframework.security.oauth2.jwt.Jwt jwt){
                username = jwt.getClaimAsString("preferred_username");
            } else {
                String principalStr = principal.toString();
                username = principalStr.substring(0,
                        Math.min(USERNAME_MAX_LENGTH_IN_DB, principalStr.length()));
            }
        }
        return username;
    }

}
