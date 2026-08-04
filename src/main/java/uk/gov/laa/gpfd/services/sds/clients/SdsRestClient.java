package uk.gov.laa.gpfd.services.sds.clients;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Configuration for SDS RestClient with request/response logging and header sanitization.
 * Provides a pre-configured RestClient bean with automatic trace context propagation.
 */
@Configuration
@ConditionalOnProperty(name = "gpfd.sds-enabled.enabled", havingValue = "true")
public class SdsRestClient {

    public static final String HTTP_RESPONSE_STATUS_CODE = "http.response.status_code";
    public static final String EVENT_DURATION = "event.duration";
    public static final String URL_FULL = "url.full";
    public static final String HTTP_REQUEST_METHOD = "http.request.method";

    @Bean
    ClientHttpRequestInterceptor loggingInterceptor() {
        return new LoggingClientHttpRequestInterceptor();
    }

    /**
     * Creates a RestClient with Micrometer observation support. Micrometer automatically propagates
     * trace context and baggage (including correlation ID).
     */
    @Bean
    RestClient restClient(
            RestClient.Builder builder, ClientHttpRequestInterceptor loggingInterceptor) {
        return builder.requestInterceptor(loggingInterceptor).build();
    }

    /**
     * Creates a RestClient for SDS API with Micrometer observation support. Micrometer automatically
     * propagates trace context and baggage (including correlation ID).
     */
    @Bean
    RestClient sdsRestClient(
            RestClient.Builder builder,
            @Value("${app.sds-api.url}") String sdsApiUrl,
            ClientHttpRequestInterceptor loggingInterceptor) {
        return builder.baseUrl(sdsApiUrl).requestInterceptor(loggingInterceptor).build();
    }

    @Slf4j
    static class LoggingClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

        private static final Set<String> SENSITIVE_HEADERS =
                Set.of("authorization", "x-api-key", "cookie", "set-cookie", "x-auth-token");

        @Override
        public ClientHttpResponse intercept(
                HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

            long startTime = System.currentTimeMillis();
            String method = request.getMethod().name();
            String uri = request.getURI().toString();

            if (log.isDebugEnabled()) {
                log.atDebug()
                        .addKeyValue("http.request.method", method)
                        .addKeyValue("url.full", uri)
                        .addKeyValue("http.request.headers", sanitizeHeaders(request))
                        .log("Outbound request");
            } else {
                log.atInfo()
                        .addKeyValue("http.request.method", method)
                        .addKeyValue("url.full", uri)
                        .log("Outbound request");
            }

            ClientHttpResponse response;
            try {
                response = execution.execute(request, body);

                long duration = System.currentTimeMillis() - startTime;
                int statusCode = response.getStatusCode().value();

                if (statusCode >= 200 && statusCode < 300) {
                    log.atInfo()
                            .addKeyValue(HTTP_REQUEST_METHOD, method)
                            .addKeyValue(URL_FULL, uri)
                            .addKeyValue(HTTP_RESPONSE_STATUS_CODE, statusCode)
                            .addKeyValue(EVENT_DURATION, duration)
                            .log("Outbound response");
                } else if (statusCode >= 400 && statusCode < 500) {
                    log.atWarn()
                            .addKeyValue(HTTP_REQUEST_METHOD, method)
                            .addKeyValue(URL_FULL, uri)
                            .addKeyValue(HTTP_RESPONSE_STATUS_CODE, statusCode)
                            .addKeyValue(EVENT_DURATION, duration)
                            .log("Outbound client error");
                } else if (statusCode >= 500) {
                    log.atError()
                            .addKeyValue(HTTP_REQUEST_METHOD, method)
                            .addKeyValue(URL_FULL, uri)
                            .addKeyValue(HTTP_RESPONSE_STATUS_CODE, statusCode)
                            .addKeyValue(EVENT_DURATION, duration)
                            .log("Outbound server error");
                }

                return response;

            } catch (IOException e) {
                long duration = System.currentTimeMillis() - startTime;
                log.atError()
                        .addKeyValue("http.request.method", method)
                        .addKeyValue("url.full", uri)
                        .addKeyValue("event.duration", duration)
                        .addKeyValue("error.message", e.getMessage())
                        .setCause(e)
                        .log("Outbound request failed");
                throw e;
            }
        }

        /**
         * Sanitizes request headers by masking sensitive values.
         *
         * @param request the HTTP request
         * @return sanitized headers as string
         */
        private String sanitizeHeaders(HttpRequest request) {
            Map<String, List<String>> headers = new HashMap<>();
            request.getHeaders().forEach(headers::put);

            return headers.entrySet().stream()
                    .map(entry -> formatHeader(entry.getKey(), entry.getValue()))
                    .collect(Collectors.joining(", ", "{", "}"));
        }

        /**
         * Formats a single header entry, redacting sensitive values.
         *
         * @param key the header key
         * @param values the header values
         * @return formatted header string
         */
        private String formatHeader(String key, List<String> values) {
            String lowerKey = key.toLowerCase();
            boolean isSensitive = SENSITIVE_HEADERS.stream().anyMatch(lowerKey::contains);

            List<String> sanitizedValues = isSensitive ? List.of("[REDACTED]") : values;
            return key + "=" + sanitizedValues;
        }
    }
}
