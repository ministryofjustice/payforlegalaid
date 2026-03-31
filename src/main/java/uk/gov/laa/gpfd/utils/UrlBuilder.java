package uk.gov.laa.gpfd.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Component
public class UrlBuilder {

    public String getServiceUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().build().toString();
    }
}
