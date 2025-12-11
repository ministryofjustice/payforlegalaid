package uk.gov.laa.gpfd.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GpfdUrlClient {

    @Value("${gpfd.url}")
    private String gpfdUrl;

    public String getGpfdUrl() {
        return gpfdUrl;
    }
}