package uk.gov.laa.gpfd.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.laa.gpfd.config.AppConfig;
import uk.gov.laa.gpfd.model.GetReportById200Response;
import uk.gov.laa.gpfd.model.Report;


/**
 * A component mapper that converts {@link Report} entities to {@link GetReportById200Response} DTOs.
 * <p>
 * This mapper handles the transformation of report data including the construction of
 * download URLs based on the application configuration.
 * </p>
 */
@Component
public final class GetReportById200ResponseMapper extends AbstractReportMapper implements ResourceResponseMapper<Report, GetReportById200Response> {

    @Autowired
    private GetReportById200ResponseMapper(AppConfig appConfig) {
        super(appConfig);
    }

    /**
     * Maps a {@link Report} entity to a {@link GetReportById200Response} DTO.
     * <p>
     * The mapping includes:
     * <ul>
     *   <li>Report identifier</li>
     *   <li>Report name</li>
     *   <li>Constructed download URL based on report type and service configuration</li>
     * </ul>
     * </p>
     *
     * @param report the report entity to map, must not be {@code null}
     * @return the fully mapped response DTO
     * @throws IllegalArgumentException if report is {@code null} or contains invalid data
     * @throws IllegalStateException if URL construction fails due to invalid configuration
     */
    public GetReportById200Response map(Report report) {
        var response = new GetReportById200Response();
        response.setId(report.getId());
        response.setReportName(report.getName());
        response.setReportDownloadUrl(constructDownloadUrl(report));

        return response;
    }

}
