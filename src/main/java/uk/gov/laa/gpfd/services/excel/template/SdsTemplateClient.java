package uk.gov.laa.gpfd.services.excel.template;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import uk.gov.laa.gpfd.exception.TemplateResourceException;

import java.io.InputStream;

@Component
@Slf4j
public class SdsTemplateClient implements TemplateClient {

    private final WebClient webClient;

    public SdsTemplateClient(WebClient client) {
        this.webClient = client;
    }

    @Override
    @SneakyThrows
    public InputStream findTemplateById(String id) {
        //TODO call mock api with string id key
        InputStream response;
        try {
            response = webClient.get().uri("/get_file?file_key=" + id).retrieve().bodyToMono(InputStream.class).block();
        } catch (Exception ex) {
            log.error("Error occurred during template download: %s", ex);
            throw new TemplateResourceException.TemplateDownloadException("Unable to download template with id " + id);
        }
        return response;
    }

}

