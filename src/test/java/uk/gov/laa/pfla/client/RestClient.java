package uk.gov.laa.pfla.client;

import org.springframework.web.client.RestTemplate;

public interface RestClient {
    RestTemplate authenticated();

    RestTemplate invalidCredentials();

    RestTemplate unauthenticated();

}
