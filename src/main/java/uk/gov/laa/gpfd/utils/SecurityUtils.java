package uk.gov.laa.gpfd.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class SecurityUtils {

    private static final String ROLE_CLAIM = "LAA_APP_ROLES";

    /**
     * Extracts the current user's application roles from the OIDC authentication token.
     * <p>
     * This method first checks the Spring SecurityContext for an authenticated
     * {@link org.springframework.security.oauth2.core.oidc.user.OidcUser}. It then attempts
     * to retrieve the "LAA_APP_ROLES" claim from the user's OIDC attributes.
     * <p>
     * @return a list of role names assigned to the current user, or an empty list if
     *         the user is not authenticated or no roles are present.
     */
    public List<String> extractRoles() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth != null && auth.getPrincipal() instanceof OidcUser oidcUser)) {
            return List.of();
        }

        Map<String, Object> attributes = oidcUser.getAttributes();
        if (attributes == null) {
            return List.of();
        }

        Object rawRoles = attributes.get(ROLE_CLAIM);
        return parseRoles(rawRoles);
    }

    /**
     * Parses a raw roles object extracted from OIDC attributes into a list of role names.
     * <p>
     * Supported formats:
     * <ul>
     *     <li>A {@link java.util.List} of values (converted via {@code toString()})</li>
     *     <li>A comma-separated {@link String}</li>
     * </ul>
     * Any other type results in an empty list.
     *
     * @param roles the raw roles object from OIDC attributes
     * @return a normalized list of role names, or an empty list if parsing fails
     */
    private List<String> parseRoles(Object roles) {
        if (roles instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }

        if (roles instanceof String str) {
            return Arrays.stream(str.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        }

        return List.of();
    }

    /**
     * Determines whether the user is authorized to access a report based on role intersection.
     * <p>
     * Authorization succeeds if the user has at least one of the roles required by the report.
     *
     * @param userRoles   the roles assigned to the current user
     * @param reportRoles the roles required to access the report
     * @return {@code true} if the user has at least one required role; {@code false} otherwise
     */
    public boolean isAuthorized(List<String> userRoles, List<String> reportRoles) {
        return reportRoles.stream().anyMatch(userRoles::contains);
    }

}
