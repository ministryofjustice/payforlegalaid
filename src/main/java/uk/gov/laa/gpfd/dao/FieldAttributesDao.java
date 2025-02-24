package uk.gov.laa.gpfd.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import uk.gov.laa.gpfd.exception.DatabaseReadException;
import uk.gov.laa.gpfd.model.FieldAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FieldAttributesDao {
    private static final String SELECT_SQL = "SELECT * FROM GPFD.FIELD_ATTRIBUTES WHERE REPORT_QUERY_ID = ?";

    private final JdbcTemplate readOnlyJdbcTemplate;
    private final ModelMapper modelMapper;

    public List<FieldAttributes> fetchFieldAttributesForReportQuery(UUID reportQueryId) throws DatabaseReadException {
        try {
            List<FieldAttributes> fieldAttributesList = new ArrayList<>();
            List<Map<String, Object>> resultList = readOnlyJdbcTemplate.queryForList(SELECT_SQL, reportQueryId.toString());

            resultList.forEach(obj -> {
                FieldAttributes fieldAttributesObject = modelMapper.map(obj, FieldAttributes.class);
                fieldAttributesList.add(fieldAttributesObject);
            });
            return fieldAttributesList;
        } catch (DataAccessException e) {
            throw new DatabaseReadException("Error reading FIELD_ATTRIBUTES : " + e);
        }
    }
}
