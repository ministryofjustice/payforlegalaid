package uk.gov.laa.gpfd.config;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authorization.AuthenticatedAuthorizationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import uk.gov.laa.gpfd.utils.RequestLogUtils;

import java.util.Objects;
import java.util.function.Supplier;

@Slf4j
public class ContextBasedAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    @Override
    public AuthorizationResult authorize(@NonNull Supplier<? extends @Nullable Authentication> authenticationSupplier, RequestAuthorizationContext context) {
        if (context == null) {
            throw new IllegalArgumentException("Authentication supplier and context must not be null");
        }

        Authentication authentication = authenticationSupplier.get();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.atWarn()
                    .addKeyValue(RequestLogUtils.EVENT_ACTION, "authorization.check")
                    .addKeyValue(RequestLogUtils.EVENT_OUTCOME, "failure")
                    .log("Unauthenticated access attempt");
            return new AuthorizationDecision(false);
        }

        // Delegate to the authenticated authorization manager
        AuthorizationResult decision = AuthenticatedAuthorizationManager.authenticated()
                .authorize(authenticationSupplier, context);

        var decisionGranted = Objects.requireNonNull(decision).isGranted();

        log.atInfo()
                .addKeyValue(RequestLogUtils.EVENT_ACTION, "authorization.check")
                .addKeyValue(RequestLogUtils.EVENT_OUTCOME, decisionGranted ? "success" : "failure")
                .log("Authorization decision: {}", decisionGranted);
        return decision;
    }
}
