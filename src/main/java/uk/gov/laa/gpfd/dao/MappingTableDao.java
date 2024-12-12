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
import uk.gov.laa.gpfd.model.MappingTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MappingTableDao {
    private static final String SELECT_SQL = "SELECT * FROM GPFD.CSV_TO_SQL_MAPPING_TABLE";

    private final JdbcTemplate readOnlyJdbcTemplate;
    private final ModelMapper mapper;

    public List<MappingTable> fetchReportList() throws DatabaseReadException {
        List<MappingTable> mappingTableObjectList = new ArrayList<>();
        List<Map<String, Object>> resultList;

        try {
            log.info("Reading mapping data");
            resultList = readOnlyJdbcTemplate.queryForList(SELECT_SQL);
        } catch (DataAccessException e) {
            throw new DatabaseReadException("Error reading from DB: " + e);
        }

        if (resultList.isEmpty()) {
            throw new ReportIdNotFoundException("No results returned from mapping table");
        }

        try {
            resultList.forEach(obj -> {
                MappingTable mappingTableObject = mapper.map(obj, MappingTable.class);
                mappingTableObjectList.add(mappingTableObject);
            });
        } catch (MappingException e) {
            log.error("Exception with model map loop: %s", e);
        }

        return mappingTableObjectList;
    }
}
