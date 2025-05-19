package uk.gov.laa.gpfd.services.excel.template;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import uk.gov.laa.gpfd.config.SdsClientConfig;
import uk.gov.laa.gpfd.exception.TemplateResourceException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withForbiddenRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withRawStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServiceUnavailable;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withTooManyRequests;

@RestClientTest(SdsTemplateClient.class)
@ExtendWith(MockitoExtension.class)
@Import(SdsClientConfig.class)
public class SdsTemplateClientTest_withAnnotation {

    private MockRestServiceServer server;

    @Autowired
    RestClient.Builder restClientBuilder;

    @Autowired
    SdsTemplateClient templateClient;

    @Mock
    SdsClientConfig sdsConfig;

    private static final String FILENAME = "excelTemplate.xlsx";
    private static final String S3_BUCKET_BASE_URL = "https://aws-s3bucket.aws";
    private static final String SDS_BASE_URL = "https://localhost:8080";
    private static final String SDS_ENDPOINT = "/get_file?file_key=";
    private static final String SDS_URL = SDS_BASE_URL + SDS_ENDPOINT + FILENAME;
    private static final String TEMPLATE_DOWNLOAD_URL = S3_BUCKET_BASE_URL + FILENAME;



    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("sds-client.sds-base-url", () -> SDS_BASE_URL);
    }


    @BeforeEach
    void setup() {
        server = MockRestServiceServer.bindTo(restClientBuilder).build();
    }

    @Test
    void shouldReturnInputStreamWhenRequestsSucceed() {
        String data = "Hello, world!";
        InputStream input = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));

        server.expect(requestTo(SDS_URL)).andRespond(withSuccess(TEMPLATE_DOWNLOAD_URL, MediaType.APPLICATION_JSON));
        server.expect(requestTo(TEMPLATE_DOWNLOAD_URL)).andRespond(withSuccess(new InputStreamResource(input), MediaType.APPLICATION_OCTET_STREAM));

        Assertions.assertNotNull(templateClient.findTemplateById(FILENAME));
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 429})
    void shouldThrowWhenGetTemplateReturnsError_failFastScenario(int status){
        server.expect(requestTo(SDS_URL)).andRespond(withSuccess(TEMPLATE_DOWNLOAD_URL, MediaType.APPLICATION_JSON));

        if (status == 400)
            server.expect(requestTo(TEMPLATE_DOWNLOAD_URL)).andRespond(withResourceNotFound());

        if (status == 429)
            server.expect(requestTo(TEMPLATE_DOWNLOAD_URL)).andRespond(withTooManyRequests());

        var ex = Assertions.assertThrows(TemplateResourceException.TemplateDownloadException.class, () -> templateClient.findTemplateById(FILENAME));
        Assertions.assertFalse(ex.getMessage().contains("retry failed; unable to download template with id " + FILENAME));
    }

    @ParameterizedTest
    @ValueSource(ints = {403, 408, 500, 503})
    void shouldNotThrowWhenGetTemplateReturnsErrors_retryScenario(int status){
        server.expect(requestTo(SDS_URL)).andRespond(withSuccess(TEMPLATE_DOWNLOAD_URL, MediaType.APPLICATION_JSON));

        if (status == 403)
            server.expect(requestTo(TEMPLATE_DOWNLOAD_URL)).andRespond(withForbiddenRequest());

        if (status == 408)
            server.expect(requestTo(TEMPLATE_DOWNLOAD_URL)).andRespond(withRawStatus(408));

        if (status == 500)
            server.expect(requestTo(TEMPLATE_DOWNLOAD_URL)).andRespond(withServerError());

        if (status == 503)
            server.expect(requestTo(TEMPLATE_DOWNLOAD_URL)).andRespond(withServiceUnavailable());

        var ex = Assertions.assertThrows(TemplateResourceException.TemplateDownloadException.class, () -> templateClient.findTemplateById(FILENAME));
        Assertions.assertTrue(ex.getMessage().contains("retry failed; unable to download template with id " + FILENAME));
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 429})
    void shouldThrowWhenGetTemplateReturnsErrorsFromDocStore_failFastScenario(int status){
        if (status == 400)
            server.expect(requestTo(SDS_URL)).andRespond(withResourceNotFound());

        if (status == 429)
            server.expect(requestTo(SDS_URL)).andRespond(withTooManyRequests());

        var ex = Assertions.assertThrows(TemplateResourceException.TemplateDownloadException.class, () -> templateClient.findTemplateById(FILENAME));
        Assertions.assertFalse(ex.getMessage().contains("retry failed; unable to download template with id " + FILENAME));
    }

    @ParameterizedTest
    @ValueSource(ints = {403, 408, 500, 503})
    void shouldThrowWhenGetTemplateReturnsErrorsFromDocStore_retryScenario(int status){
        if (status == 403)
            server.expect(requestTo(SDS_URL)).andRespond(withForbiddenRequest());
        if (status == 408)
            server.expect(requestTo(SDS_URL)).andRespond(withRawStatus(408));

        if (status == 500)
            server.expect(requestTo(SDS_URL)).andRespond(withServerError());

        if (status == 503)
            server.expect(requestTo(SDS_URL)).andRespond(withServiceUnavailable());

        server.expect(requestTo(TEMPLATE_DOWNLOAD_URL)).andRespond(withSuccess(TEMPLATE_DOWNLOAD_URL, MediaType.APPLICATION_JSON));


        var ex = Assertions.assertThrows(TemplateResourceException.TemplateDownloadException.class, () -> templateClient.findTemplateById(FILENAME));
        Assertions.assertTrue(ex.getMessage().contains("Retry failed; unable to get url for download of template with id " + FILENAME));    }

    }
