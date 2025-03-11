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
import uk.gov.laa.gpfd.model.ReportDetails;

import java.sql.Timestamp;
import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
/**
 * Data access class responsible for interacting with the Reports table and transforming its data
 * into a format suitable for other services.
 */
public class ReportDetailsDao {
    private static final String SELECT_ALL_REPORTS_SQL =
            "SELECT r.*, o.EXTENSION FROM GPFD.REPORTS r, GPFD.REPORT_OUTPUT_TYPES o " +
            "WHERE r.REPORT_OUTPUT_TYPE = o.ID";
    private static final String SELECT_SINGLE_REPORT_SQL =
            "SELECT r.*, o.EXTENSION FROM GPFD.REPORTS r, GPFD.REPORT_OUTPUT_TYPES o " +
            "WHERE r.REPORT_OUTPUT_TYPE = o.ID AND r.ID = ?";

    private final JdbcTemplate readOnlyJdbcTemplate;
    private final ModelMapper modelMapper;

    /**
     * Fetches a list of report entries from the database, returns as {@link Report} objects, either list or single.
     * @throws DatabaseReadException if there is an error fetching data from the database
     * @throws ReportIdNotFoundException if there is no report with that Id found in the database
     */
    private List<ReportDetails> fetchReportResults(
                                    String sqlCommand,
                                    Object ... args)
            throws DatabaseReadException, ReportIdNotFoundException {
        List<Map<String, Object>> resultList = new ArrayList<>();
        List<ReportDetails> reportsList = new ArrayList<>();
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
                    .map(ReportDetailsDao::map)
                    .toList();

        } catch (MappingException e) {
            log.error("Exception with model map loop: %s", e);
        }

        return reportsList;
    }

    @SuppressWarnings("java:S3599")
    public static ReportDetails map(Map<String, Object> reportData) {

        return new ReportDetails() {{
            if (reportData.get("ID") != null) {
                setId(UUID.fromString(reportData.get("ID").toString()));
            } else {
                setId(null);
            }
            if (reportData.get("REPORT_OUTPUT_TYPE") != null) {
                setReportOutputType(UUID.fromString(reportData.get("REPORT_OUTPUT_TYPE").toString()));
            } else {
                setReportOutputType(null);
            }

            if (reportData.get("REPORT_OWNER_ID") != null) {
                setReportOwnerId(UUID.fromString(reportData.get("REPORT_OWNER_ID").toString()));
            } else {
                setReportOwnerId(null);
            }

            if (reportData.get("NAME") != null) {
                setName(reportData.get("NAME").toString());
            } else {
                setName("");
            }

            if (reportData.get("DESCRIPTION") != null) {
                setDescription(reportData.get("DESCRIPTION").toString());
            } else {
                setDescription("");
            }

            if (reportData.get("TEMPLATE_SECURE_DOCUMENT_ID") != null) {
                setTemplateSecureDocumentId(reportData.get("TEMPLATE_SECURE_DOCUMENT_ID").toString());
            } else {
                setTemplateSecureDocumentId(null);
            }

            if (reportData.get("REPORT_OWNER_NAME") != null) {
                setReportOwnerName(reportData.get("REPORT_OWNER_NAME").toString());
            } else {
                setReportOwnerName("");
            }

            if (reportData.get("REPORT_OWNER_EMAIL") != null) {
                setReportOwnerEmail(reportData.get("REPORT_OWNER_EMAIL").toString());
            } else {
                setReportOwnerEmail("");
            }

            if (reportData.get("FILE_NAME") != null) {
                setFileName(reportData.get("FILE_NAME").toString());
            } else {
                setFileName("");
            }

            if (reportData.get("EXTENSION") != null) {
                setExtension(reportData.get("EXTENSION").toString());
            } else {
                setExtension("");
            }

            if (reportData.get("ACTIVE") != null) {
                setActive(reportData.get("ACTIVE").toString().contentEquals("Y"));
            } else {
                setActive(false);
            }

            if (reportData.get("NUM_DAYS_TO_KEEP") != null) {
                setNumDaysToKeep(Integer.valueOf(reportData.get("NUM_DAYS_TO_KEEP").toString()));
            } else {
                setNumDaysToKeep(0);
            }

            if (reportData.get("REPORT_CREATION_DATE") != null) {
                setReportCreationDate(Timestamp.valueOf(reportData.get("REPORT_CREATION_DATE").toString()));
            } else {
                setReportCreationDate(null);
            }

            if (reportData.get("LAST_DATABASE_REFRESH_DATE") != null) {
                setLastDatabaseRefreshDate(Timestamp.valueOf(reportData.get("LAST_DATABASE_REFRESH_DATE").toString()));
            } else {
                setLastDatabaseRefreshDate(null);
            }
        }};
    }

    public List<ReportDetails> fetchReportList() throws DatabaseReadException, ReportIdNotFoundException {
        return fetchReportResults(SELECT_ALL_REPORTS_SQL);
    }

    public ReportDetails fetchReport(UUID reportId) throws DatabaseReadException, ReportIdNotFoundException {
        return fetchReportResults(SELECT_SINGLE_REPORT_SQL, reportId.toString()).get(0);
    }
}
