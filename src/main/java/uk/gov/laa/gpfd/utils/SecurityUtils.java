package uk.gov.laa.gpfd.utils;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SecurityUtils {

    private static final String CACHED_ROLES_KEY = "CACHED_LAA_APP_ROLES";
    private static final String ROLE_CLAIM = "LAA_APP_ROLES";

    /**
     * Extracts the current user's application roles from the OIDC authentication token.
     * <p>
     * This method first checks the Spring SecurityContext for an authenticated
     * {@link org.springframework.security.oauth2.core.oidc.user.OidcUser}. It then attempts
     * to retrieve the "LAA_APP_ROLES" claim from the user's OIDC attributes.
     * <p>
     * To improve performance, roles are cached in the {@link org.springframework.security.core.Authentication}
     * details for the duration of the request. Subsequent calls return the cached value.
     *
     * @return a list of role names assigned to the current user, or an empty list if
     *         the user is not authenticated or no roles are present.
     */
    public List<String> extractRoles() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth != null && auth.getPrincipal() instanceof OidcUser oidcUser)) {
            return List.of();
        }

        // Try cache first
        List<String> cached = getCachedRoles(auth);
        if (cached != null) {
            return cached;
        }

        // Extract from OIDC attributes
        Object rawRoles = oidcUser.getAttributes().get(ROLE_CLAIM);
        List<String> parsed = parseRoles(rawRoles);

        // Cache for the remainder of the request
        cacheRoles(auth, parsed);

        return parsed;
    }

    /**
     * Attempts to retrieve cached roles from the {@link Authentication} details map.
     * <p>
     * This method returns {@code null} if no cached roles are present, allowing the caller
     * to fall back to extracting roles from the OIDC token.
     *
     * @param auth the current authentication object
     * @return a cached list of roles, or {@code null} if no cached value exists
     */
    private List<String> getCachedRoles(Authentication auth) {
        Object details = auth.getDetails();
        if (details instanceof Map<?, ?> map) {
            Object cached = map.get(CACHED_ROLES_KEY);
            if (cached instanceof List<?> list) {
                return list.stream().map(Object::toString).toList();
            }
        }
        return null;
    }

    /**
     * Caches the resolved user roles inside the {@link Authentication} details map.
     * <p>
     * This cache is scoped to the lifetime of the current request because the
     * {@link org.springframework.security.core.context.SecurityContext} is cleared after completion.
     *
     * @param auth  the current authentication object
     * @param roles the list of roles to cache
     */
    private void cacheRoles(Authentication auth, List<String> roles) {
        if (auth instanceof AbstractAuthenticationToken token) {
            Object details = token.getDetails();

            if (details instanceof Map<?, ?> existingMap) {
                ((Map<Object, Object>) existingMap).put(CACHED_ROLES_KEY, roles);
            } else {
                Map<String, Object> newDetails = new HashMap<>();
                newDetails.put(CACHED_ROLES_KEY, roles);
                token.setDetails(newDetails);
            }
        }
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
