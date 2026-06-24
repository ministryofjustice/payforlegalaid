package uk.gov.laa.pfla.client;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
public class RestTemplateWithErrorHandling extends RestTemplate {

    @Override
    @SneakyThrows
    public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Object... uriVariables) {
        try {
            return this.exchange(url, HttpMethod.GET, null, responseType);
        } catch (HttpClientErrorException | HttpServerErrorException exception) {
            log.info("HTTP Error: {} - {}", exception.getStatusCode(), exception.getStatusText());
            log.info("Response Body: {}", exception.getResponseBodyAsString());
            return ResponseEntity.status(exception.getStatusCode()).body((T) exception.getResponseBodyAsString());
        } catch (ResourceAccessException | ResponseStatusException exception) {
            return ResponseEntity.status(500).body((T) exception.getLocalizedMessage());
        } catch (RestClientException exception) {
            log.info("RestClientException: {}", exception.getMessage());
            throw exception;
        }
    }

}