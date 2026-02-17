package uk.gov.laa.gpfd.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import uk.gov.laa.gpfd.model.Report;

import java.util.*;

import static uk.gov.laa.gpfd.exception.DatabaseReadException.DatabaseFetchException;

@Slf4j
@Service
public record ReportDao(
        ResultSetExtractor<Collection<Report>> extractor,
        JdbcOperations readOnlyJdbcTemplate,
        NamedParameterJdbcOperations namedReadOnlyJdbcTemplate
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
            q."INDEX",
            fa.ID AS FIELD_ATTRIBUTE_ID,
            fa.SOURCE_NAME,
            fa.MAPPED_NAME,
            fa.FORMAT,
            fa.FORMAT_TYPE,
            fa.COLUMN_WIDTH,
            rot.ID AS OUTPUT_TYPE_ID,
            rot.EXTENSION,
            rot.DESCRIPTION AS OUTPUT_TYPE_DESCRIPTION
        FROM GPFD.REPORTS r
        LEFT JOIN GPFD.REPORT_QUERIES q ON r.ID = q.REPORT_ID
        LEFT JOIN GPFD.FIELD_ATTRIBUTES fa ON q.ID = fa.REPORT_QUERY_ID
        LEFT JOIN GPFD.REPORT_OUTPUT_TYPES rot ON r.REPORT_OUTPUT_TYPE = rot.ID
        WHERE r.ID = ?
        ORDER BY q."INDEX" ASC, fa.COLUMN_ORDER ASC
    """;

    private static final String SELECT_ALL_REPORTS_SQL = """
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
            q."INDEX",
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
        FROM GPFD.REPORTS r
        LEFT JOIN GPFD.REPORT_QUERIES q ON r.ID = q.REPORT_ID
        LEFT JOIN GPFD.FIELD_ATTRIBUTES fa ON q.ID = fa.REPORT_QUERY_ID
        LEFT JOIN GPFD.REPORT_OUTPUT_TYPES rot ON r.REPORT_OUTPUT_TYPE = rot.ID
        WHERE r.ACTIVE = 'Y'
    """;

    private static final String SELECT_REPORTS_BY_ROLE_SQL = """
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
            q."INDEX",
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
        FROM GPFD.REPORTS r
        LEFT JOIN GPFD.REPORT_QUERIES q ON r.ID = q.REPORT_ID 
        LEFT JOIN GPFD.FIELD_ATTRIBUTES fa ON q.ID = fa.REPORT_QUERY_ID 
        LEFT JOIN GPFD.REPORT_OUTPUT_TYPES rot ON r.REPORT_OUTPUT_TYPE = rot.ID 
        INNER JOIN GPFD.REPORT_ROLES rr ON r.ID = rr.REPORT_ID 
        INNER JOIN GPFD.ROLES ro ON rr.ROLE_ID = ro.ROLE_ID 
        WHERE ro.ROLE_NAME IN (:roles)
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
            return readOnlyJdbcTemplate.query(SELECT_REPORT_BY_ID, extractor, reportId.toString())
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
            return readOnlyJdbcTemplate.query(SELECT_ALL_REPORTS_SQL, extractor);
        } catch (DataAccessException e) {
            String errorMessage = "Failed to fetch reports from database";
            log.error("{}: {}", errorMessage, e.getMessage(), e);
            throw new DatabaseFetchException("Failed to fetch reports from database");
        }
    }

    public Collection<Report> fetchReportsByRole() throws DatabaseFetchException {
        try {
            List<String> roles = extractRoles();
            log.info("Fetching reports from database for RBAC roles: {}", roles);
            Map<String, Object> params = Map.of("roles", roles);

            log.info("SQL: {}", SELECT_REPORTS_BY_ROLE_SQL);
            log.info("Params: {}", params);

            return namedReadOnlyJdbcTemplate.query(SELECT_REPORTS_BY_ROLE_SQL, params, extractor);
        } catch (DataAccessException e) {
            String errorMessage = "Failed to fetch reports from database";
            log.error("{}: {}", errorMessage, e.getMessage(), e);
            throw new DatabaseFetchException("Failed to fetch reports from database");
        }
    }

    private static List<String> extractRoles() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof OidcUser oidcUser)) {
            return List.of();
        }

        Object roles = oidcUser.getAttributes().get("LAA_APP_ROLES");
        return parseRoles(roles);
    }

    private static List<String> parseRoles(Object roles) {
        if (roles == null) {
            return List.of();
        }
        if (roles instanceof List<?> list) {
            return list.stream()
                    .map(Object::toString)
                    .toList();
        }

        if (roles instanceof String str) {
            return Arrays.stream(str.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        }
        return List.of();
    }


}
