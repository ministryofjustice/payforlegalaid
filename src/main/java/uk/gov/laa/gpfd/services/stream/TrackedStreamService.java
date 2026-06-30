package uk.gov.laa.gpfd.services.stream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.laa.gpfd.dao.ReportTrackingDao;
import uk.gov.laa.gpfd.exception.StreamErrorException;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TrackedStreamService {

    private final ReportTrackingDao reportTrackingDao;

    /**
     * This tracked stream is basically just wrapping the stream in a try/catch so we can spot any server-side errors
     * and only log the download as started if there are none of those
     *
     * @param rawStream stream of data being returned to the user
     * @param reportId report being downloaded
     * @param userId user doing download
     * @return stream with a light-tracking wrapper around it
     */
    public StreamingResponseBody wrapStream(StreamingResponseBody rawStream, UUID reportId, UUID userId){
        return output -> {
            try {
                rawStream.writeTo(output);
                output.flush();
                reportTrackingDao.insertTrackingRow(reportId, userId);
            } catch (IOException e) {
                throw new StreamErrorException(e.getMessage(), reportId);
            } finally {
                log.debug("Completed server-side response stream for report {}", reportId);
            }
        };
    }

}
