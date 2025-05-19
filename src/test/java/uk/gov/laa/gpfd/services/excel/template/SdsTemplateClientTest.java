package uk.gov.laa.gpfd.services.excel.template;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import uk.gov.laa.gpfd.config.SdsClientConfig;
import uk.gov.laa.gpfd.exception.TemplateResourceException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SdsTemplateClientTest {

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
    SdsClientConfig sdsConfig;

    @InjectMocks
    private SdsTemplateClient sdsTemplateClient;

    private static final String FILENAME = "excelTemplate.xlsx";
    private static final String S3_BUCKET_URL = "https://aws-s3bucket.aws";
    private static final String SDS_BASE_URL = "http//localhost:8080";


    @BeforeEach
    void setup() {
        when(sdsConfig.getSdsBaseUrl()).thenReturn(SDS_BASE_URL);
        when(restClientBuilder.baseUrl((String) any())).thenReturn(restClientBuilder);

        when(restClientBuilder.baseUrl(SDS_BASE_URL)).thenReturn(restClientBuilder);
        when(restClientBuilder.baseUrl(S3_BUCKET_URL)).thenReturn(restClientBuilder);

        when(restClientBuilder.build()).thenReturn(sdsRestClient).thenReturn(awsRestClient);

        when(sdsRestClient.get()).thenReturn(sdsRequestHeadersUriSpec);
        when(sdsRequestHeadersUriSpec.uri("/get_file?file_key=" + FILENAME)).thenReturn(sdsRequestHeadersSpec);
        when(sdsRequestHeadersSpec.retrieve()).thenReturn(sdsResponseSpec);

    }
    @Test
    void shouldFindTemplateByIdReturnsTemplateAsInputStream() {
        var mockFileContent = "This is a mock file content.";
        InputStream mockInputStream = new ByteArrayInputStream(mockFileContent.getBytes());

        when(sdsResponseSpec.body(String.class)).thenReturn(S3_BUCKET_URL);

        when(awsRestClient.get()).thenReturn(awsRequestHeadersUriSpec);
        when(awsRequestHeadersUriSpec.uri("")).thenReturn(awsRequestHeadersSpec);
        when(awsRequestHeadersSpec.retrieve()).thenReturn(awsResponseSpec);
        when(awsResponseSpec.body(InputStream.class)).thenReturn(mockInputStream);
        when(awsResponseSpec.onStatus(any(), any())).thenReturn(awsResponseSpec);

        Assertions.assertEquals(mockInputStream, sdsTemplateClient.findTemplateById(FILENAME));
    }

    @Test
    void shouldThrowWhenGetTemplateFromUrl() {
        when(sdsResponseSpec.body(String.class)).thenReturn(S3_BUCKET_URL);

        var ex = Assertions.assertThrows(TemplateResourceException.TemplateDownloadException.class, () -> sdsTemplateClient.findTemplateById(FILENAME));
        Assertions.assertEquals("Unable to download template with id excelTemplate.xlsx", ex.getMessage());
    }

    @Test
    void shouldThrowWhenGetUrlFromSDSThrows() {
        when(sdsResponseSpec.body(InputStream.class)).thenThrow(IllegalArgumentException.class);

        var ex = Assertions.assertThrows(TemplateResourceException.TemplateDownloadException.class, () -> sdsTemplateClient.findTemplateById(FILENAME));
        Assertions.assertEquals("Unable to get url for download of template with id excelTemplate.xlsx", ex.getMessage());
    }
}
