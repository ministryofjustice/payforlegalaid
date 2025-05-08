package uk.gov.laa.gpfd.services.excel.template;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import uk.gov.laa.gpfd.config.AppConfig;
import uk.gov.laa.gpfd.exception.TemplateResourceException;

import java.io.InputStream;

@Component
@Slf4j
@AllArgsConstructor
public class SdsTemplateClient implements TemplateClient {

    private final AppConfig appConfig;
    private final RestClient.Builder restClientBuilder;


    @Override
    @SneakyThrows
    public InputStream findTemplateById(String id) {
        InputStream response;
        try{
            var restClient = restClientBuilder.baseUrl(getUrlFromSecureDocumentStorage(id)).build();
            response = restClient.get().uri("").retrieve().body(InputStream.class);
        } catch (TemplateResourceException ex) {
            log.error("Error occurred getting url from Secure Document Storage: %s", ex);
            throw ex;
        } catch (Exception ex) {
            log.error("Error occurred during template download:", ex);
            throw new TemplateResourceException.TemplateDownloadException("Unable to download template with id " + id);
        }

        return response;
    }

    private String getUrlFromSecureDocumentStorage(String fileKey) {
        String url;
        try {
            var restClient = restClientBuilder.baseUrl(appConfig.getSdsUrl()).build();
            url = restClient.get().uri("/get_file?file_key=" + fileKey).retrieve().body(String.class);
        } catch (Exception ex) {
            throw new TemplateResourceException.TemplateDownloadException("Unable to get url for download of template with id " + fileKey);
        }

        return url;
    }

}

