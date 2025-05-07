package uk.gov.laa.gpfd.services.excel.template;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.gov.laa.gpfd.exception.TemplateResourceException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SdsTemplateClientTest {

    @Mock
    WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private SdsTemplateClient sdsTemplateClient;

    @BeforeEach
    void setup() {
        sdsTemplateClient = new SdsTemplateClient(webClient);
    }

    @Test
    void findTemplateByIdReturnsTemplateAsInputStream() {
        var fileName = "excelTemplate.xlsx";
        String mockFileContent = "This is a mock file content.";
        InputStream mockInputStream = new ByteArrayInputStream(mockFileContent.getBytes());

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/get_file?file_key=" + fileName)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(InputStream.class)).thenReturn(Mono.just(mockInputStream));

        Assertions.assertEquals(mockInputStream, sdsTemplateClient.findTemplateById(fileName));
    }

    @Test
    void shouldThrowWhenWebClientThrows() {
        var fileName = "excelTemplate.xlsx";

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/get_file?file_key=" + fileName)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(InputStream.class)).thenThrow(IllegalArgumentException.class);

        var ex = Assertions.assertThrows(TemplateResourceException.TemplateDownloadException.class, () -> sdsTemplateClient.findTemplateById(fileName));
        Assertions.assertEquals("Unable to download template with id excelTemplate.xlsx", ex.getMessage());
    }

}
