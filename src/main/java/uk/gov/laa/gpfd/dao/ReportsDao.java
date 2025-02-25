package uk.gov.laa.gpfd.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import uk.gov.laa.gpfd.exception.DatabaseReadException;
import uk.gov.laa.gpfd.model.Report;
import uk.gov.laa.gpfd.model.ReportGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReportsDao {
    private static final String SELECT_ALL_REPORTS = "SELECT * FROM GPFD.V_REPORTS";

    private final JdbcTemplate readOnlyJdbcTemplate;
    private final ModelMapper modelMapper;

    public List<Report> fetchAllReports() throws DatabaseReadException {
        try {
            List<Report> reportList = new ArrayList<>();
            List<Map<String, Object>> resultList = readOnlyJdbcTemplate.queryForList(SELECT_ALL_REPORTS);

            resultList.forEach(obj -> {
                Report reportObject = modelMapper.map(obj, Report.class);
                reportList.add(reportObject);
            });
            return reportList;
        } catch (DataAccessException e) {
            throw new DatabaseReadException("Error reading REPORTS : " + e);
        }
    }
}
