package uk.gov.laa.gpfd.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UrlBuilderTest {

    private final UrlBuilder urlBuilder = new UrlBuilder();

    @AfterEach
    void afterEach() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void shouldBuildUrlFromContextWithAllBitsSet() {
        var req = new MockHttpServletRequest();
        req.setScheme("http");
        req.setServerName("example.com");
        req.setServerPort(8000);
        req.setContextPath("reports");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(req));

        assertEquals("http://example.com:8000/reports", urlBuilder.getServiceUrl());
    }

    @Test
    void shouldGenerateEvenIfBaseUrl() {
        var req = new MockHttpServletRequest();
        req.setScheme("https");
        req.setServerName("example.com");
        req.setServerPort(443);
        req.setContextPath("");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(req));

        assertEquals("https://example.com", urlBuilder.getServiceUrl());
    }

    @Test
    void shouldIgnorePortForHttpIfDefault() {
        var req = new MockHttpServletRequest();
        req.setScheme("http");
        req.setServerName("example.com");
        req.setServerPort(80);
        req.setContextPath("reports");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(req));

        assertEquals("http://example.com/reports", urlBuilder.getServiceUrl());
    }

    @Test
    void shouldIgnorePortForHttpsIfDefault() {
        var req = new MockHttpServletRequest();
        req.setScheme("https");
        req.setServerName("example.com");
        req.setServerPort(443);
        req.setContextPath("reports");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(req));

        assertEquals("https://example.com/reports", urlBuilder.getServiceUrl());
    }


}