package uk.gov.laa.gpfd.services.excel.template;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import uk.gov.laa.gpfd.config.SdsClientConfig;
import uk.gov.laa.gpfd.exception.TemplateResourceException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Component
@Slf4j
@AllArgsConstructor
public class SdsTemplateClient implements TemplateClient {

    private final SdsClientConfig sdsConfig;
    private final RestClient.Builder restClientBuilder;

    private static final String SDS_ENDPOINT = "/get_file?file_key=";

    @Override
    @SneakyThrows
    public InputStream findTemplateById(String id) throws TemplateResourceException.TemplateDownloadException {
        InputStream stream = null;

        if (id == null || id.isEmpty()) {
            log.info("No id provided for document template");
            throw new TemplateResourceException.TemplateDownloadException("Unable to download template without id");
        }

        try {
            var restClient = restClientBuilder.baseUrl(getUrlFromSecureDocumentStorage(id)).build();
            stream = Objects.requireNonNull(restClient.get().uri("")
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        handleErrors(response);
                    })
                    .body(InputStream.class));

        } catch (TemplateResourceException.TemplateDownloadException ex) {
            log.error("Error occurred getting url from document storage: {}", ex);
            throw ex;
        } catch (TemplateResourceException.ExcelTemplateDownloadRetryException ex) {
            // Add logic here to retry downloading template using url from doc store
            log.info("Attempting retry of download using url provided by document store");
            throw new TemplateResourceException.TemplateDownloadException("Partial retry failed; unable to download template with id " + id);

        } catch (TemplateResourceException.ExcelTemplateRetryException ex) {
            // Add logic here to retry full download transaction, including getting url from doc store
            log.info("Retry full transaction; unable to access template with provided url");
            throw new TemplateResourceException.TemplateDownloadException("Full retry failed; unable to download template with id " + id);

        } catch (Exception ex) {
            log.error("Error occurred during template download:", ex);
            throw new TemplateResourceException.TemplateDownloadException("Unable to download template with id " + id);
        }

        return stream;
    }

    private static void handleErrors(ClientHttpResponse response) throws IOException {
        switch(response.getStatusCode().value()) {
            case 403:
                throw new TemplateResourceException.ExcelTemplateRetryException("Failure to download template, full download transaction");
            case 408, 500, 503:
                throw new TemplateResourceException.ExcelTemplateDownloadRetryException("Failure to download template, will retry accessing template with download url");
            default:
                log.info("Failed to download template; will not attempt retry");
                throw new TemplateResourceException.TemplateDownloadException("Failure to download template, will not retry");
        }
    }

    private String getUrlFromSecureDocumentStorage(String fileKey) {
        String url = "";
        try {
            var restClient = restClientBuilder.baseUrl(sdsConfig.getSdsBaseUrl()).build();
            url = restClient.get().uri(SDS_ENDPOINT + fileKey).retrieve().onStatus(HttpStatusCode::isError, (request, response) -> {
                handleErrors(response);
            }).body(String.class);
        } catch (TemplateResourceException.ExcelTemplateRetryException | TemplateResourceException.ExcelTemplateDownloadRetryException ex) {
            // Add logic here to retry getting download url from doc store
            log.info("Attempting retry of get url for template download");
            throw new TemplateResourceException.TemplateDownloadException("Retry failed; unable to get url for download of template with id " + fileKey);
        } catch (Exception ex) {
            throw new TemplateResourceException.TemplateDownloadException("Unable to get url for download of template with id " + fileKey);
        }

        return url;
    }

}

