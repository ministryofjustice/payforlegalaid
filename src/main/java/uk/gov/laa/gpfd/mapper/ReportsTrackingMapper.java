package uk.gov.laa.gpfd.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.laa.gpfd.config.AppConfig;
import uk.gov.laa.gpfd.model.ImmutableReportsTracking;
import uk.gov.laa.gpfd.model.Report;
import uk.gov.laa.gpfd.model.ReportsTracking;

/**
 * Mapper that converts {@link Report} entities to {@link ReportsTracking} DTOs with tracking information.
 * <p>
 * This mapper enriches report data with:
 * <ul>
 *   <li>Tracking-specific metadata (ID, creation date)</li>
 *   <li>Access information (creator, owner)</li>
 *   <li>Resource URLs (report download, template)</li>
 * </ul>
 */
@Component
public class ReportsTrackingMapper extends AbstractReportMapper  {

    @Autowired
    public ReportsTrackingMapper(AppConfig appConfig) {
        super(appConfig);
    }

    /**
     * Maps a report entity to a tracking DTO with additional metadata.
     *
     * @param report the report entity to map (must not be {@code null})
     * @param currentUserName the username of the current user initiating the tracking (must not be {@code null} or empty)
     * @return fully populated {@link ReportsTracking} DTO
     * @throws IllegalArgumentException if any input is invalid
     * @throws IllegalStateException if URL construction fails
     */
    public ReportsTracking map(Report report, String currentUserName) {
        return ImmutableReportsTracking.builder()
                .id(report.getId())
                .name(report.getName())
                .reportUrl(constructReportUrl(report))
                .creationDate(currentTimestamp())
                .reportId(report.getId())
                .reportCreator(currentUserName)
                .reportOwner(report.getReportOwnerName())
                .reportOutputType(report.getReportOutputType().toString())
                .templateUrl(report.getTemplateSecureDocumentId())
                .build();
    }

}
