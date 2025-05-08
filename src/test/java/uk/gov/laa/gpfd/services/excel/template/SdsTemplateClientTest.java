package uk.gov.laa.gpfd.services.excel.template;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import uk.gov.laa.gpfd.config.AppConfig;
import uk.gov.laa.gpfd.exception.TemplateResourceException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SdsTemplateClientTest {

    @Mock
    RestClient.Builder restClientBuilder;

    @Mock
    RestClient sdsRestClient;

    @Mock
    private RestClient.RequestHeadersUriSpec sdsRequestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec sdsRequestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec sdsResponseSpec;

    @Mock
    RestClient awsRestClient;

    @Mock
    private RestClient.RequestHeadersUriSpec awsRequestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec awsRequestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec awsResponseSpec;
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
        when(restClientBuilder.baseUrl((String) any())).thenReturn(restClientBuilder);

        when(restClientBuilder.baseUrl(sdsUrl)).thenReturn(restClientBuilder);
        when(restClientBuilder.baseUrl(urlFromSds)).thenReturn(restClientBuilder);

        when(restClientBuilder.build()).thenReturn(sdsRestClient).thenReturn(awsRestClient);

        when(sdsRestClient.get()).thenReturn(sdsRequestHeadersUriSpec);
        when(sdsRequestHeadersUriSpec.uri("/get_file?file_key=" + fileName)).thenReturn(sdsRequestHeadersSpec);
        when(sdsRequestHeadersSpec.retrieve()).thenReturn(sdsResponseSpec);

    }
    @Test
    void findTemplateByIdReturnsTemplateAsInputStream() {
        var fileName = "excelTemplate.xlsx";
        var mockFileContent = "This is a mock file content.";
        var urlFromSds = "This is file URL";
        InputStream mockInputStream = new ByteArrayInputStream(mockFileContent.getBytes());

        when(sdsResponseSpec.body(String.class)).thenReturn(urlFromSds);

        when(awsRestClient.get()).thenReturn(awsRequestHeadersUriSpec);
        when(awsRequestHeadersUriSpec.uri("")).thenReturn(awsRequestHeadersSpec);
        when(awsRequestHeadersSpec.retrieve()).thenReturn(awsResponseSpec);
        when(awsResponseSpec.body(InputStream.class)).thenReturn(mockInputStream);

        Assertions.assertEquals(mockInputStream, sdsTemplateClient.findTemplateById(fileName));
    }

    @Test
    void shouldThrowWhenGetTemplateFromUrl() {
        var fileName = "excelTemplate.xlsx";
        var urlFromSds = "This is file URL";

        when(sdsResponseSpec.body(String.class)).thenReturn(urlFromSds);

        var ex = Assertions.assertThrows(TemplateResourceException.TemplateDownloadException.class, () -> sdsTemplateClient.findTemplateById(fileName));
        Assertions.assertEquals("Unable to download template with id excelTemplate.xlsx", ex.getMessage());
    }

    @Test
    void shouldThrowWhenGetUrlFromSDSThrows() {
        var fileName = "excelTemplate.xlsx";
        var sdsUrl = "http//localhost:8080";

        when(sdsResponseSpec.body(InputStream.class)).thenThrow(IllegalArgumentException.class);

        var ex = Assertions.assertThrows(TemplateResourceException.TemplateDownloadException.class, () -> sdsTemplateClient.findTemplateById(fileName));
        Assertions.assertEquals("Unable to get url for download of template with id excelTemplate.xlsx", ex.getMessage());
    }

}
