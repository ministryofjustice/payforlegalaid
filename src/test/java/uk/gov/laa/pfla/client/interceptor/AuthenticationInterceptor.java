package uk.gov.laa.pfla.client.interceptor;

import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import uk.gov.laa.pfla.client.AuthenticationProvider;

@FunctionalInterface
public interface AuthenticationInterceptor extends ClientHttpRequestInterceptor {

    AuthenticationProvider getAuthenticator();

    @Override
    @SneakyThrows
    default ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
        HttpHeaders headers = request.getHeaders();
        headers.setAll(getAuthenticator().addAuthentication(request).getHeaders().toSingleValueMap());
        return execution.execute(request, body);
    }

}