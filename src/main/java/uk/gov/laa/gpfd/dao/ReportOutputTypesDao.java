package uk.gov.laa.gpfd.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import uk.gov.laa.gpfd.exception.DatabaseReadException;
import uk.gov.laa.gpfd.model.ReportGroup;
import uk.gov.laa.gpfd.model.ReportOutputType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReportOutputTypesDao {
    private static final String SELECT_SQL = "SELECT * FROM GPFD.REPORT_OUTPUT_TYPES ORDER BY ID ASC";

    private final JdbcTemplate readOnlyJdbcTemplate;
    private final ModelMapper modelMapper;

    public List<ReportOutputType> fetchReportOutputTypes() throws DatabaseReadException {
        try {
            List<ReportOutputType> typesList = new ArrayList<>();
            List<Map<String, Object>> resultList = readOnlyJdbcTemplate.queryForList(SELECT_SQL);

            resultList.forEach(obj -> {
                ReportOutputType outputTypeObject = modelMapper.map(obj, ReportOutputType.class);
                typesList.add(outputTypeObject);
            });
            return typesList;
        } catch (DataAccessException e) {
            throw new DatabaseReadException("Error reading REPORT_OUTPUT_TYPES : " + e);
        }
    }
}
