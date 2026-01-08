package uk.gov.laa.gpfd.mapper;

import org.springframework.stereotype.Component;
import uk.gov.laa.gpfd.model.FileExtension;
import uk.gov.laa.gpfd.model.Report;
import uk.gov.laa.gpfd.utils.UrlBuilder;

import java.net.URI;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Abstract base class for report mappers that provides common functionality
 * for constructing report URLs and basic mapping operations.
 */
@Component
public abstract class AbstractReportMapper {

    protected final UrlBuilder urlBuilder;

    protected AbstractReportMapper(UrlBuilder urlBuilder) {
        this.urlBuilder = urlBuilder;
    }

    /**
     * Constructs a download URL for the given report.
     *
     * @param report the report to construct URL for
     * @return the constructed URI
     * @throws IllegalStateException if URL construction fails
     */
    protected URI constructDownloadUrl(Report report) {
        try {
            var baseUrl = urlBuilder.getServiceUrl();
            var reportId = report.getIdAsString();
            if (Objects.equals(report.getOutputType().getExtension(), FileExtension.S3STORAGE.getExtension())) {
                return URI.create("%s/%s/%s/%s".formatted(baseUrl,"reports", reportId, "file"));
            }
            var extensionPath = report.getOutputType().getSubPath();
            return URI.create("%s/%s/%s".formatted(baseUrl, extensionPath, reportId));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to construct report URL", e);
        }
    }

    /**
     * Creates a new timestamp for the current time.
     *
     * @return current timestamp
     */
    protected Timestamp currentTimestamp() {
        return Timestamp.valueOf(LocalDateTime.now());
    }

}