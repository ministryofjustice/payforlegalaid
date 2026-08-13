package uk.gov.laa.gpfd.services.sds;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import uk.gov.laa.gpfd.exception.ServiceUnavailableException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Downloads SDS files from presigned URLs so GPFD can control the response headers and filename.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "gpfd.sds-enabled.enabled", havingValue = "true")
public class SdsDownloadService {

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    public SdsDownloadResult download(String fileUrl) {
        log.debug("Downloading SDS file from presigned URL");

        try {
            var request = HttpRequest.newBuilder(URI.create(fileUrl)).GET().build();
            var response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                response.body().close();
                throw new ServiceUnavailableException("Failed to download file from SDS URL: HTTP " + response.statusCode());
            }

            var contentLengthHeader = response.headers().firstValueAsLong("Content-Length");
            Long contentLength = contentLengthHeader.isPresent() ? contentLengthHeader.getAsLong() : null;
            return new SdsDownloadResult(response.body(), contentLength);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ServiceUnavailableException("SDS download was interrupted");
        } catch (IOException e) {
            throw new ServiceUnavailableException("Failed to download file from SDS URL: " + e.getMessage());
        }
    }

    public record SdsDownloadResult(InputStream stream, Long contentLength) {
        public void close() throws IOException {
            stream.close();
        }
    }
}
