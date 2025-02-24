package uk.gov.laa.gpfd.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import uk.gov.laa.gpfd.exception.DatabaseReadException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import uk.gov.laa.gpfd.model.ReportGroup;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReportGroupsDao {
    private static final String SELECT_SQL = "SELECT * FROM GPFD.REPORT_GROUPS WHERE REPORT_ID = ?";

    private final JdbcTemplate readOnlyJdbcTemplate;
    private final ModelMapper modelMapper;

    public List<ReportGroup> fetchReportGroupsForSpecificReport(UUID reportId) throws DatabaseReadException {
        try {
            List<ReportGroup> reportGroupList = new ArrayList<>();
            List<Map<String, Object>> resultList = readOnlyJdbcTemplate.queryForList(SELECT_SQL, reportId.toString());

            resultList.forEach(obj -> {
                ReportGroup reportGroupObject = modelMapper.map(obj, ReportGroup.class);
                reportGroupList.add(reportGroupObject);
            });
            return reportGroupList;
        } catch (DataAccessException e) {
            throw new DatabaseReadException("Error reading REPORT_GROUPS : " + e);
        }
    }
}