package uk.gov.laa.gpfd.dao;

import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.gov.laa.gpfd.exception.DatabaseReadException;
import uk.gov.laa.gpfd.model.Report;
import uk.gov.laa.gpfd.model.ReportGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ReportsDao {
    private static final String SELECT_SINGLE_REPORT_SQL = "SELECT * FROM GPFD.REPORTS WHERE ID = ?";
    //private static final String SELECT_REPORTS_BY_TYPE_SQL = "SELECT * FROM GPFD.V_REPORTS_BY_TYPE WHERE EXTENSION = ?";

    private final JdbcTemplate readOnlyJdbcTemplate;
    private final ModelMapper modelMapper;

    public List<Report> fetchAllReports() throws DatabaseReadException {
        try {
            List<Report> reportList = new ArrayList<>();
            List<Map<String, Object>> resultList = readOnlyJdbcTemplate.queryForList(SELECT_SQL);

            resultList.forEach(obj -> {
                ReportGroup reportObject = modelMapper.map(obj, ReportGroup.class);
                reportList.add(reportObject);
            });
            return reportList;
        } catch (DataAccessException e) {
            throw new DatabaseReadException("Error reading REPORTS : " + e);
        }
    }
}
