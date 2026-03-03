package uk.gov.laa.gpfd.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthenticatedAuthorizationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.function.Supplier;

@Slf4j
public class ContextBasedAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context) {
        if (authenticationSupplier == null || context == null) {
            throw new IllegalArgumentException("Authentication supplier and context must not be null");
        }

        Authentication authentication = authenticationSupplier.get();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Unauthenticated access attempt");
            return new AuthorizationDecision(false);
        }

        // Delegate to the authenticated authorization manager
        AuthorizationDecision decision = AuthenticatedAuthorizationManager.authenticated()
                .check(authenticationSupplier, context);

        log.info("Authorization decision: " + decision.isGranted());
        return decision;
    }
}
