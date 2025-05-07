package uk.gov.laa.gpfd.services.excel.template;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.gov.laa.gpfd.config.AppConfig;
import uk.gov.laa.gpfd.exception.TemplateResourceException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SdsTemplateClientTest {

    @Mock
    WebClient.Builder webClientBuilder;

    @Mock
    WebClient sdsWebClient;

    @Mock
    private WebClient.RequestHeadersUriSpec sdsRequestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec sdsRequestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec sdsResponseSpec;

    @Mock
    WebClient awsWebClient;

    @Mock
    private WebClient.RequestHeadersUriSpec awsRequestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec awsRequestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec awsResponseSpec;
    @Mock
    AppConfig appConfig;

    @InjectMocks
    private SdsTemplateClient sdsTemplateClient;

    @BeforeEach
    void setup() {
        var fileName = "excelTemplate.xlsx";
        var urlFromSds = "This is file URL";
        var sdsUrl = "http//localhost:8080";

        when(appConfig.getSdsUrl()).thenReturn(sdsUrl);
        when(webClientBuilder.baseUrl(any())).thenReturn(webClientBuilder);

        when(webClientBuilder.baseUrl(sdsUrl)).thenReturn(webClientBuilder);
        when(webClientBuilder.baseUrl(urlFromSds)).thenReturn(webClientBuilder);

        when(webClientBuilder.build()).thenReturn(sdsWebClient).thenReturn(awsWebClient);

        when(sdsWebClient.get()).thenReturn(sdsRequestHeadersUriSpec);
        when(sdsRequestHeadersUriSpec.uri("/get_file?file_key=" + fileName)).thenReturn(sdsRequestHeadersSpec);
        when(sdsRequestHeadersSpec.retrieve()).thenReturn(sdsResponseSpec);

    }
    @Test
    void findTemplateByIdReturnsTemplateAsInputStream() {
        var fileName = "excelTemplate.xlsx";
        var mockFileContent = "This is a mock file content.";
        var urlFromSds = "This is file URL";
        InputStream mockInputStream = new ByteArrayInputStream(mockFileContent.getBytes());

        when(sdsResponseSpec.bodyToMono(String.class)).thenReturn(Mono.just(urlFromSds));

        when(awsWebClient.get()).thenReturn(awsRequestHeadersUriSpec);
        when(awsRequestHeadersUriSpec.uri("")).thenReturn(awsRequestHeadersSpec);
        when(awsRequestHeadersSpec.retrieve()).thenReturn(awsResponseSpec);
        when(awsResponseSpec.bodyToMono(InputStream.class)).thenReturn(Mono.just(mockInputStream));

        Assertions.assertEquals(mockInputStream, sdsTemplateClient.findTemplateById(fileName));
    }

    @Test
    void shouldThrowWhenGetTemplateFromUrl() {
        var fileName = "excelTemplate.xlsx";
        var urlFromSds = "This is file URL";

        when(sdsResponseSpec.bodyToMono(String.class)).thenReturn(Mono.just(urlFromSds));

        var ex = Assertions.assertThrows(TemplateResourceException.TemplateDownloadException.class, () -> sdsTemplateClient.findTemplateById(fileName));
        Assertions.assertEquals("Unable to download template with id excelTemplate.xlsx", ex.getMessage());
    }

    @Test
    void shouldThrowWhenGetUrlFromSDSThrows() {
        var fileName = "excelTemplate.xlsx";
        var sdsUrl = "http//localhost:8080";

        when(sdsResponseSpec.bodyToMono(InputStream.class)).thenThrow(IllegalArgumentException.class);

        var ex = Assertions.assertThrows(TemplateResourceException.TemplateDownloadException.class, () -> sdsTemplateClient.findTemplateById(fileName));
        Assertions.assertEquals("Unable to get url for download of template with id excelTemplate.xlsx", ex.getMessage());
    }

}
