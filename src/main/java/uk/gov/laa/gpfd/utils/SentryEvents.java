package uk.gov.laa.gpfd.utils;

import io.sentry.IScope;
import io.sentry.Sentry;
import io.sentry.SentryLevel;
import io.sentry.protocol.User;
import org.springframework.beans.TypeMismatchException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.util.DisconnectedClientHelper;
import uk.gov.laa.gpfd.exception.FileDownloadException.InvalidDownloadFormatException;
import uk.gov.laa.gpfd.exception.FileDownloadException.ReportNotSupportedForDownloadException;
import uk.gov.laa.gpfd.exception.InvalidReportFormatException;
import uk.gov.laa.gpfd.exception.OperationNotSupportedException;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;
import uk.gov.laa.gpfd.exception.ServiceUnavailableException;

public final class SentryEvents {

    private SentryEvents() {
    }

    public static void captureException(Throwable exception, String action) {
        if (!shouldCaptureException(exception)) {
            return;
        }
        Sentry.captureException(exception, scope -> addMetadata(scope, action));
    }

    public static boolean shouldCaptureException(Throwable exception) {
        return !DisconnectedClientHelper.isClientDisconnectedException(exception)
                && !(exception instanceof TypeMismatchException)
                && !(exception instanceof ReportIdNotFoundException)
                && !(exception instanceof InvalidReportFormatException)
                && !(exception instanceof InvalidDownloadFormatException)
                && !(exception instanceof ReportNotSupportedForDownloadException)
                && !(exception instanceof ErrorResponse response && response.getStatusCode().is4xxClientError());
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