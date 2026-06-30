package uk.gov.laa.gpfd.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.laa.gpfd.model.FileExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.of;
import static uk.gov.laa.gpfd.services.stream.DataStream.APPLICATION_EXCEL;

@ExtendWith(MockitoExtension.class)
class ResponseBuilderTest {

    @Mock
    StreamingResponseBody streamingResponseBody;

    ResponseBuilder responseBuilder = new ResponseBuilder();

    @Test
    void shouldCreateResponseWhenGivenStreamAndDetails() {
        var response = responseBuilder.buildResponse(streamingResponseBody, "filename.csv", FileExtension.CSV, 10L);
        assertTrue(response.getStatusCode().is2xxSuccessful());

        var headers = response.getHeaders();
        var contentDisposition = headers.getContentDisposition();
        assertTrue(contentDisposition.isAttachment());
        assertEquals("filename.csv", contentDisposition.getFilename());

        var contentType = headers.getContentType();
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, contentType);

        var contentLength = headers.getContentLength();
        assertEquals(10L, contentLength);

        assertEquals(streamingResponseBody, response.getBody());
    }

    @ParameterizedTest()
    @MethodSource("extensionTypeTestCases")
    void shouldSetCorrectContentTypeForEachFileExtension(FileExtension extensionToTest, MediaType expectedMediaType) {
        var response = responseBuilder.buildResponse(streamingResponseBody, "filename", extensionToTest);
        var contentType = response.getHeaders().getContentType();
        assertEquals(expectedMediaType, contentType);
    }

    @Test
    void shouldNotSetContentLengthIfNotProvided() {
        var response = responseBuilder.buildResponse(streamingResponseBody, "filename.csv", FileExtension.CSV);
        var headers = response.getHeaders();
        assertNull(headers.get("Content-Length"));
    }

    private static Stream<Arguments> extensionTypeTestCases() {
        return Stream.of(
                of(FileExtension.CSV, MediaType.APPLICATION_OCTET_STREAM),
                of(FileExtension.XLSX, MediaType.valueOf(APPLICATION_EXCEL)),
                of(FileExtension.S3STORAGE, MediaType.APPLICATION_OCTET_STREAM)
        );
    }

}