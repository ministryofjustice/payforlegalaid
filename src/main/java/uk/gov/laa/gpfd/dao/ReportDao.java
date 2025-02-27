package uk.gov.laa.gpfd.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.dao.DataAccessException;
import lombok.RequiredArgsConstructor;
import uk.gov.laa.gpfd.dao.support.ReportWithQueriesAndFieldAttributesExtractor;
import uk.gov.laa.gpfd.model.Report;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReportDao {

    private static final String SELECT_REPORT_BY_ID = """
        SELECT 
            r.ID, 
            r.NAME, 
            r.FILE_NAME,
            r.TEMPLATE_SECURE_DOCUMENT_ID, 
            r.REPORT_CREATION_DATE, 
            r.LAST_DATABASE_REFRESH_DATETIME, 
            r.DESCRIPTION, 
            r.NUM_DAYS_TO_KEEP, 
            r.REPORT_OUTPUT_TYPE, 
            r.REPORT_CREATOR_ID, 
            r.REPORT_CREATOR_NAME, 
            r.REPORT_CREATOR_EMAIL, 
            r.REPORT_OWNER_ID, 
            r.REPORT_OWNER_NAME,
            r.ACTIVE,
            r.REPORT_OWNER_EMAIL,
            q.ID AS QUERY_ID,
            q.QUERY,
            q.TAB_NAME,
            fa.ID AS FIELD_ATTRIBUTE_ID,
            fa.SOURCE_NAME,
            fa.MAPPED_NAME,
            fa.FORMAT,
            fa.FORMAT_TYPE,
            fa.COLUMN_WIDTH,
            rot.EXTENSION,
            rot.DESCRIPTION
        FROM GPFD.REPORTS r
        LEFT JOIN GPFD.REPORT_QUERIES q ON r.ID = q.REPORT_ID
        LEFT JOIN GPFD.FIELD_ATTRIBUTES fa ON q.ID = fa.REPORT_QUERY_ID
        LEFT JOIN GPFD.REPORT_OUTPUT_TYPES rot ON r.REPORT_OUTPUT_TYPE = rot.ID
        WHERE r.ID = ?
    """;

    private final ReportWithQueriesAndFieldAttributesExtractor extractor;
    private final JdbcTemplate readOnlyJdbcTemplate;

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
            throw new RuntimeException("Error fetching report by ID: " + reportId, e);
        }
    }

}
