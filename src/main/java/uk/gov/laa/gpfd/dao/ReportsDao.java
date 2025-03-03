package uk.gov.laa.gpfd.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import uk.gov.laa.gpfd.exception.DatabaseReadException;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;
import uk.gov.laa.gpfd.model.Report;
import uk.gov.laa.gpfd.model.ReportsGet200ResponseReportListInner;

import java.sql.Timestamp;
import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
/**
 * Fetches a list of report entries from the database, returns as {@link Report} objects, either list or single.
 * @throws DatabaseReadException if there is an error fetching data from the database
 * @throws ReportIdNotFoundException if there is no report with that Id found in the database
 */
public class ReportsDao {
    private static final String SELECT_ALL_REPORTS_SQL =
            "SELECT r.*, o.EXTENSION FROM GPFD.REPORTS r, GPFD.REPORT_OUTPUT_TYPES o " +
            "WHERE r.REPORT_OUTPUT_TYPE = o.ID";
    private static final String SELECT_SINGLE_REPORT_SQL =
            "SELECT r.*, o.EXTENSION FROM GPFD.REPORTS r, GPFD.REPORT_OUTPUT_TYPES o " +
            "WHERE r.REPORT_OUTPUT_TYPE = o.ID AND r.ID = ?";

    private final JdbcTemplate readOnlyJdbcTemplate;
    private final ModelMapper modelMapper;

    private List<Report> fetchReportResults(
                                    String sqlCommand,
                                    Object ... args)
            throws DatabaseReadException, ReportIdNotFoundException {
        List<Map<String, Object>> resultList = new ArrayList<>();
        List<Report> reportsList = new ArrayList<>();
        try {
            log.info("Reading reports");
            if (args != null && args.length > 0) {
                resultList = readOnlyJdbcTemplate.queryForList(sqlCommand, args);
            }
            else {
                resultList = readOnlyJdbcTemplate.queryForList(sqlCommand);
            }
        } catch (DataAccessException e) {
            throw new DatabaseReadException("Error reading from DB: " + e);
        }

        if (resultList.isEmpty()) {
            throw new ReportIdNotFoundException("Report with unrecognised ID");
        }

        try {
            reportsList = resultList.stream()
                    .map(ReportsDao::map)
                    .toList();

        } catch (MappingException e) {
            log.error("Exception with model map loop: %s", e);
        }

        return reportsList;
    }

    @SuppressWarnings("java:S3599")
    public static Report map(Map<String, Object> reportData) {

        return new Report() {{
            setId(UUID.fromString(reportData.get("ID").toString()));
            setReportOutputType(UUID.fromString(reportData.get("REPORT_OUTPUT_TYPE").toString()));
            setReportOwnerId(UUID.fromString(reportData.get("REPORT_OWNER_ID").toString()));
            setName(reportData.get("NAME").toString());
            setDescription(reportData.get("DESCRIPTION").toString());
            setTemplateSecureDocumentId(reportData.get("TEMPLATE_SECURE_DOCUMENT_ID").toString());
            setReportOwnerName(reportData.get("REPORT_OWNER_NAME").toString());
            setReportOwnerEmail(reportData.get("REPORT_OWNER_EMAIL").toString());
            setFileName(reportData.get("FILE_NAME").toString());
            setExtension(reportData.get("EXTENSION").toString());
            setActive(reportData.get("ACTIVE").toString().contentEquals("Y"));
            setReportCreationDate(Timestamp.valueOf(reportData.get("REPORT_CREATION_DATE").toString()));
            setLastDatabaseRefreshDate(Timestamp.valueOf(reportData.get("LAST_DATABASE_REFRESH_DATETIME").toString()));
            setNumDaysToKeep(Integer.valueOf(reportData.get("NUM_DAYS_TO_KEEP").toString()));

        }};
    }

    public List<Report> fetchReportList() throws DatabaseReadException, ReportIdNotFoundException {
        return fetchReportResults(SELECT_ALL_REPORTS_SQL);
    }

    public Report fetchReport(UUID reportId) throws DatabaseReadException, ReportIdNotFoundException {
        return fetchReportResults(SELECT_SINGLE_REPORT_SQL, reportId.toString()).get(0);
    }
}
