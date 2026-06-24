package uk.gov.laa.pfla.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

public interface AuthenticationProvider {
    static AuthenticationProvider basicAuth(String username, String password) {
        return new AuthenticationProvider() {
            @Override
            public <T> HttpEntity<T> addAuthentication(T request) {
                var headers = setAuthHeader();
                return new HttpEntity<>(request, headers);
            }

            @Override
            public HttpHeaders setAuthHeader() {
                return new HttpHeaders() {{
                    setBasicAuth(username, password);
                }};
            }
        };
    }

    <T> HttpEntity<T> addAuthentication(T request);

    HttpHeaders setAuthHeader();
}