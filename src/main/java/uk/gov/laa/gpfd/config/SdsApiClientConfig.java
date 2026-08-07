package uk.gov.laa.gpfd.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.laa.gpfd.services.sds.SdsTokenService;
import uk.gov.laa.gpfd.services.sds.client.ApiClient;
import uk.gov.laa.gpfd.services.sds.client.api.FilesApi;
import uk.gov.laa.gpfd.services.sds.client.api.HealthApi;

import java.io.IOException;

/**
 * Configuration for SDS API client with Bearer token authentication.
 * Provides OkHttpClient with interceptor that adds Bearer token to all requests.
 */
@Configuration
@ConditionalOnProperty(name = "gpfd.sds-enabled.enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class SdsApiClientConfig {

    private final SdsTokenService sdsTokenService;

    @Value("${app.sds-api.url}")
    private String sdsApiUrl;

    /**
     * Interceptor that adds Bearer token to all SDS API requests.
     */
    @Bean
    public Interceptor sdsBearerTokenInterceptor() {
        return chain -> {
            String accessToken = sdsTokenService.getSdsAccessToken();
            Request originalRequest = chain.request();

            Request requestWithToken = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + accessToken)
                    .build();

            return chain.proceed(requestWithToken);
        };
    }

    /**
     * Configured OkHttpClient for SDS API with Bearer token interceptor.
     */
    @Bean
    public OkHttpClient sdsOkHttpClient(Interceptor sdsBearerTokenInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(sdsBearerTokenInterceptor)
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build();
    }

    /**
     * Configured ApiClient for SDS with base URL and OkHttpClient.
     */
    @Bean
    public ApiClient sdsApiClient(OkHttpClient sdsOkHttpClient) {
        ApiClient apiClient = new ApiClient(sdsOkHttpClient);
        apiClient.setBasePath(sdsApiUrl);
        return apiClient;
    }

    /**
     * FilesApi bean for file operations.
     */
    @Bean
    public FilesApi filesApi(ApiClient sdsApiClient) {
        return new FilesApi(sdsApiClient);
    }

    /**
     * HealthApi bean for health check operations.
     */
    @Bean
    public HealthApi healthApi(ApiClient sdsApiClient) {
        return new HealthApi(sdsApiClient);
    }
}