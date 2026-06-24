package uk.gov.laa.pfla.client.interceptor;

import lombok.SneakyThrows;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@FunctionalInterface
public interface HostInterceptor extends ClientHttpRequestInterceptor {

    static HostInterceptor withHost(String host) {
        return () -> host.endsWith("/") ? host.substring(0, host.length() - 1) : host;
    }

    String getHost();

    @Override
    @SneakyThrows
    default ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
        var newRequest = new HttpRequestWrapper(request) {
            @Override
            public URI getURI() {
                return UriComponentsBuilder.fromUriString(getHost())
                        .path(request.getURI().getPath())
                        .query(request.getURI().getQuery())
                        .build()
                        .toUri();
            }
        };

        return execution.execute(newRequest, body);
    }

}