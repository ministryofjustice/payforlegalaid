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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReportsDao {
    private static final String SELECT_ALL_REPORTS_SQL =
            "SELECT r.*, o.EXTENSION FROM GPFD.REPORTS r, GPFD.REPORT_OUTPUT_TYPES o " +
            "WHERE r.REPORT_OUTPUT_TYPE = o.ID";
    private static final String SELECT_SINGLE_REPORT_SQL =
            "SELECT r.*, o.EXTENSION FROM GPFD.REPORTS r, GPFD.REPORT_OUTPUT_TYPES o " +
            "WHERE r.REPORT_OUTPUT_TYPE = o.ID AND r.ID = ?";

    private final JdbcTemplate readOnlyJdbcTemplate;
    private final ModelMapper modelMapper;

    private void fetchReportResults(List<Report> reportsList,
                                    String sqlCommand,
                                    Object ... args)
            throws DatabaseReadException, ReportIdNotFoundException {
        List<Map<String, Object>> resultList = null;

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
            resultList.forEach(obj -> {
                Report report = modelMapper.map(obj, Report.class);
                reportsList.add(report);
            });
        } catch (MappingException e) {
            log.error("Exception with model map loop: %s", e);
        }
    }

    public List<Report> fetchReportList() throws DatabaseReadException {
        List<Report> reportsList = new ArrayList<>();

        fetchReportResults(reportsList, SELECT_ALL_REPORTS_SQL);

        return reportsList;
    }

    public Report fetchReport(UUID reportId) throws DatabaseReadException {
        List<Report> reportList = new ArrayList<>();
        fetchReportResults(reportList, SELECT_SINGLE_REPORT_SQL, reportId.toString());
        return reportList.get(0);
    }
}
