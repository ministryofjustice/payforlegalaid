package uk.gov.laa.pfla.service;

import org.springframework.web.client.RestTemplate;
import uk.gov.laa.pfla.client.RestClient;
import uk.gov.laa.pfla.scenario.AuthenticationState;

public interface HttpProvider {
    RestTemplate getClient();

    default RestTemplate getClient(RestClient restClient, AuthenticationState state) {
        return switch (state) {
            case AUTHENTICATED -> restClient.authenticated();
            case UNAUTHENTICATED -> restClient.unauthenticated();
            case INVALID_CREDENTIALS -> restClient.invalidCredentials();
        };
    }

}
