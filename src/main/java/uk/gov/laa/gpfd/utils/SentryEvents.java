package uk.gov.laa.gpfd.utils;

import io.sentry.IScope;
import io.sentry.Sentry;
import io.sentry.SentryLevel;
import io.sentry.protocol.User;

public final class SentryEvents {

    private SentryEvents() {
    }

    public static void captureException(Throwable exception, String action) {
        Sentry.captureException(exception, scope -> addMetadata(scope, action));
    }

    public static void captureMessage(String message, String action) {
        Sentry.captureMessage(message, SentryLevel.WARNING, scope -> addMetadata(scope, action));
    }

    private static void addMetadata(IScope scope, String action) {
        scope.setTag(RequestLogUtils.EVENT_ACTION, action);
        scope.setTag(RequestLogUtils.EVENT_OUTCOME, "failure");
        scope.setUser(new User());
    }
}