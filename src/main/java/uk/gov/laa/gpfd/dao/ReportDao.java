package uk.gov.laa.gpfd.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import uk.gov.laa.gpfd.exception.ReportAccessException;
import uk.gov.laa.gpfd.model.Report;
import uk.gov.laa.gpfd.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static uk.gov.laa.gpfd.exception.DatabaseReadException.DatabaseFetchException;

@Slf4j
@Service
public record ReportDao(
        ResultSetExtractor<Collection<Report>> extractor,
        @Qualifier("metadataClient") JdbcClient metadataClient,
        SecurityUtils securityUtils
) {

    private static final String SELECT_REPORT_BY_ID = """
        SELECT 
            r.ID, 
            r.NAME, 
            r.FILE_NAME,
            r.TEMPLATE_SECURE_DOCUMENT_ID, 
            r.REPORT_CREATION_DATE, 
            r.LAST_DATABASE_REFRESH_DATETIME, 
            r.DESCRIPTION AS REPORT_DESCRIPTION,
            r.NUM_DAYS_TO_KEEP, 
            r.REPORT_OUTPUT_TYPE, 
            r.REPORT_OWNER_ID, 
            r.REPORT_OWNER_NAME,
            r.ACTIVE,
            r.REPORT_OWNER_EMAIL,
            q.ID AS QUERY_ID,
            q.QUERY,
            q.TAB_NAME,
            q."index" AS "INDEX",
            fa.ID AS FIELD_ATTRIBUTE_ID,
            fa.SOURCE_NAME,
            fa.MAPPED_NAME,
            fa.FORMAT,
            fa.FORMAT_TYPE,
            fa.COLUMN_WIDTH,
            rot.ID AS OUTPUT_TYPE_ID,
            rot.EXTENSION,
            rot.DESCRIPTION AS OUTPUT_TYPE_DESCRIPTION
        FROM glad.reports r
        LEFT JOIN glad.report_queries q ON r.ID = q.REPORT_ID
        LEFT JOIN glad.field_attributes fa ON q.ID = fa.REPORT_QUERY_ID
        LEFT JOIN glad.report_output_types rot ON r.REPORT_OUTPUT_TYPE = rot.ID
        WHERE r.ID = ?
        ORDER BY q."index" ASC, fa.column_order ASC
    """;

    static final String SELECT_ALL_REPORTS_SQL = """
      SELECT 
            r.ID, 
            r.NAME, 
            r.FILE_NAME,
            r.TEMPLATE_SECURE_DOCUMENT_ID, 
            r.REPORT_CREATION_DATE, 
            r.LAST_DATABASE_REFRESH_DATETIME, 
            r.DESCRIPTION AS REPORT_DESCRIPTION,
            r.NUM_DAYS_TO_KEEP, 
            r.REPORT_OUTPUT_TYPE, 
            r.REPORT_OWNER_ID, 
            r.REPORT_OWNER_NAME,
            r.ACTIVE,
            r.REPORT_OWNER_EMAIL,
            q.ID AS QUERY_ID,
            q.QUERY,
            q."index" AS "INDEX",
            q.TAB_NAME,
            fa.ID AS FIELD_ATTRIBUTE_ID,
            fa.SOURCE_NAME,
            fa.MAPPED_NAME,
            fa.FORMAT,
            fa.FORMAT_TYPE,
            fa.COLUMN_WIDTH,
            rot.ID AS OUTPUT_TYPE_ID,
            rot.EXTENSION,
            rot.DESCRIPTION AS OUTPUT_TYPE_DESCRIPTION
        FROM glad.reports r
       LEFT JOIN glad.report_queries q ON r.ID = q.REPORT_ID
       LEFT JOIN glad.field_attributes fa ON q.ID = fa.REPORT_QUERY_ID
       LEFT JOIN glad.report_output_types rot ON r.REPORT_OUTPUT_TYPE = rot.ID
       INNER JOIN glad.report_roles rr ON r.ID = rr.REPORT_ID
       INNER JOIN glad.roles ro ON rr.ROLE_ID = ro.ROLE_ID
       WHERE r.ACTIVE = 'Y' AND ro.ROLE_NAME IN (:roles)
    """;

   static final String SELECT_REPORT_ROLES = """
       SELECT r.role_name AS "ROLE_NAME"
       FROM glad.roles r
       JOIN glad.report_roles rr ON rr.role_id = r.role_id
       WHERE rr.report_id = ?
       """;

    /**
     * Fetches a {@link Report} by its unique identifier (UUID) from the database.
     * This method executes a SQL query to retrieve the report and returns it as an {@link Optional}.
     * If no report is found, an empty {@link Optional} is returned.
     *
     * @param reportId the unique identifier (UUID) of the report to fetch
     * @return an {@link Optional} containing the fetched report if found, otherwise an empty {@link Optional}
     * @throws RuntimeException if an error occurs while fetching the report
     */
    public Optional<Report> fetchReportById(UUID reportId) {
        log.debug("Executing SQL query to fetch report by ID: {}", reportId);
        try {
            // Enforce role-based access control for this report
            verifyUserCanAccessReport(reportId);

            return metadataClient.sql(SELECT_REPORT_BY_ID)
                    .param(reportId)
                    .query(extractor)
                    .stream()
                    .findFirst();
        } catch (DataAccessException e) {
            log.error("Error fetching report by ID: {}", reportId, e);
            throw new DatabaseFetchException("Error fetching report by ID: " + reportId);
        }
    }

    /**
     * Fetches all reports from the database.
     *
     * @return a collection of all reports in the system
     * @throws DatabaseFetchException if there's an error accessing the database
     */
    public Collection<Report> fetchReports() throws DatabaseFetchException {
        log.debug("Fetching all reports from database");
        try {
            List<String> roles = securityUtils.extractRoles();
            log.info("Fetching reports from database for RBAC roles: {}", roles);
            return metadataClient.sql(SELECT_ALL_REPORTS_SQL)
                    .param("roles", roles)
                    .query(extractor);
        } catch (DataAccessException e) {
            String errorMessage = "Failed to fetch reports from database";
            log.error("{}: {}", errorMessage, e.getMessage(), e);
            throw new DatabaseFetchException("Failed to fetch reports from database");
        }
    }

    public void verifyUserCanAccessReport(UUID reportId) {
        List<String> userRoles = securityUtils.extractRoles();
        List<String> requiredRoles = loadRequiredRoles(reportId);
        log.info(
                "Report {} requires roles: {} whereas user has: {}",
                reportId, requiredRoles, userRoles
        );

        if (!securityUtils.isAuthorized(userRoles, requiredRoles)) {
            throw new ReportAccessException(reportId);
        }
    }

    private List<String> loadRequiredRoles(UUID reportId) {
        List<String> roles = new ArrayList<>();
        metadataClient.sql(SELECT_REPORT_ROLES)
                .param(reportId)
                .query((RowCallbackHandler) rs -> roles.add(rs.getString("ROLE_NAME")));
        return roles;
    }

}
