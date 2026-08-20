package uk.gov.laa.gpfd.utils;

public final class LogSanitiser {

    private LogSanitiser() {
    }

    public static String sanitise(String value) {
        if (value == null) {
            return null;
        }

        // Prevent CRLF / log forging attacks.
        return value.replaceAll("\\p{Cntrl}", "_");
    }
}
