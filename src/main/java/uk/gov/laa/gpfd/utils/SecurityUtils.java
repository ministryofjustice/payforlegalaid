package uk.gov.laa.gpfd.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class SecurityUtils {

    public List<String> extractRoles() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof OidcUser oidcUser)) {
            return List.of();
        }

        Object roles = oidcUser.getAttributes().get("ROLES");
        return parseRoles(roles);
    }

    private List<String> parseRoles(Object roles) {
        if (roles == null) {
            return List.of();
        }

        if (roles instanceof List<?> list) {
            return list.stream()
                    .map(Object::toString)
                    .toList();
        }

        if (roles instanceof String str) {
            return Arrays.stream(str.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        }

        return List.of();
    }

    public boolean isAuthorized(List<String> userRoles, List<String> reportRoles) {
        return reportRoles.stream().anyMatch(userRoles::contains);
    }
}
