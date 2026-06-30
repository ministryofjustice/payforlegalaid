package uk.gov.laa.gpfd.services.stream;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.laa.gpfd.config.AsyncTestConfig;
import uk.gov.laa.gpfd.dao.ReportTrackingDao;
import uk.gov.laa.gpfd.exception.StreamErrorException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Import(AsyncTestConfig.class)
class TrackedStreamServiceTest {

    private static final UUID REPORT_ID = UUID.fromString("0d4da9ec-b0b3-4371-af10-f375330d85d1");
    private static final UUID USER_ID = UUID.fromString("5aee3d3d-15d3-41ba-9646-06429a183f68");

    @Mock
    ReportTrackingDao reportTrackingDao;

    @InjectMocks
    TrackedStreamService trackedStreamService;

    @SneakyThrows
    @Test
    void tracksDownloadWhenStreamFinished() {
        StreamingResponseBody rawStream = output -> output.write("data".getBytes());

        StreamingResponseBody wrappedStream = trackedStreamService.wrapStream(rawStream,REPORT_ID, USER_ID);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        wrappedStream.writeTo(outputStream);

        verify(reportTrackingDao).insertTrackingRow(REPORT_ID, USER_ID);
    }

    @SneakyThrows
    @Test
    void catchesAndRethrowsAnyStreamingErrors() {
        StreamingResponseBody rawStream = _ -> {throw new IOException("sad");};

        StreamingResponseBody wrappedStream = trackedStreamService.wrapStream(rawStream,REPORT_ID, USER_ID);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        assertThrows(StreamErrorException.class, () -> wrappedStream.writeTo(outputStream));

    }

}