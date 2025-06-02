package uk.gov.laa.gpfd.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.laa.gpfd.config.AppConfig;
import uk.gov.laa.gpfd.model.Report;

import java.net.URI;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Abstract base class for report mappers that provides common functionality
 * for constructing report URLs and basic mapping operations.
 */
@Component
public abstract class AbstractReportMapper {

    protected final AppConfig appConfig;
    protected final String baseUrl;

    @Autowired
    protected AbstractReportMapper(AppConfig appConfig) {
        this.appConfig = appConfig;
        baseUrl = appConfig.getServiceUrl().replaceAll("/+$", "");
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
            var extensionPath = report.getOutputType().getSubPath();
            var reportId = report.getIdAsString();

            return URI.create("%s/%s/%s".formatted(baseUrl, extensionPath, reportId));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to construct report URL", e);
        }
    }

    /**
     * Constructs a simple string URL for the given report.
     *
     * @param report the report to construct URL for
     * @return the constructed URL string
     * @throws IllegalStateException if URL construction fails
     */
    protected String constructReportUrl(Report report) {
        return constructDownloadUrl(report).toString();
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