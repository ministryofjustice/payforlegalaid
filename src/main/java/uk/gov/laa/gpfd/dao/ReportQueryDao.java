package uk.gov.laa.gpfd.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import uk.gov.laa.gpfd.exception.DatabaseReadException;
import uk.gov.laa.gpfd.model.FieldAttributes;
import uk.gov.laa.gpfd.model.ReportQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReportQueryDao {
    private static final String SELECT_SQL = "SELECT * FROM GPFD.REPORT_QUERIES WHERE ID = ?";

    private final JdbcTemplate readOnlyJdbcTemplate;
    private final ModelMapper modelMapper;

    public List<ReportQuery> fetchReportQueriesForReport(UUID reportId) throws DatabaseReadException {
        try {
            List<ReportQuery> reportQueries = new ArrayList<>();
            List<Map<String, Object>> resultList = readOnlyJdbcTemplate.queryForList(SELECT_SQL, reportId.toString());

            resultList.forEach(obj -> {
                ReportQuery reportQuery = modelMapper.map(obj, ReportQuery.class);
                reportQueries.add(reportQuery);
            });
            return reportQueries;
        } catch (DataAccessException e) {
            throw new DatabaseReadException("Error reading REPORT_QUERIES : " + e);
        }
    }
}
