package uk.gov.laa.gpfd.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import uk.gov.laa.gpfd.controller.ui.WebJarController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE;

@ExtendWith(MockitoExtension.class)
class WebJarControllerTest {

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private WebJarController webJarController;

    @Test
    void serveWebJarShouldReturnResourceWithCorrectPath() {
        var requestPath = "/webjars/jquery/3.6.0/jquery.min.js";
        when(request.getAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE)).thenReturn(requestPath);

        var response = webJarController.serveWebJar(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertInstanceOf(ClassPathResource.class, response.getBody());

        ClassPathResource resource = (ClassPathResource) response.getBody();
        assertEquals("META-INF/resources/webjars/jquery/3.6.0/jquery.min.js", resource.getPath());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/webjars/some-lib/1.0.0/script.js", "/webjars/bootstrap/5.1.3/css/bootstrap.css","/webjars/some-lib/2.0.0/images/logo.png" })
    void serveWebJarShouldSetCorrectContentType(String requestPath) {
        when(request.getAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE)).thenReturn(requestPath);

        var response = webJarController.serveWebJar(request);

        assertNotEquals("application/octet-stream", response.getHeaders().getFirst("Content-Type"));
    }

    @Test
    void serveWebJarShouldSetDefaultContentTypeForUnknownExtension() {
        var requestPath = "/webjars/custom-lib/1.2.3/file.unknown";
        when(request.getAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE)).thenReturn(requestPath);

        var response = webJarController.serveWebJar(request);

        assertEquals("application/octet-stream", response.getHeaders().getFirst("Content-Type"));
    }

}