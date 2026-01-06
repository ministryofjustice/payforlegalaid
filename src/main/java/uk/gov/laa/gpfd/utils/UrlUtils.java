package uk.gov.laa.gpfd.utils;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public abstract class UrlUtils {

    private UrlUtils() {
        // Can't instantiate utility class
    }

    public static String getServiceUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().build().toString();
    }
}
