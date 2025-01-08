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
    private static final String SELECT_SQL = "SELECT * FROM GPFD.CSV_TO_SQL_MAPPING_TABLE ORDER BY ID ASC";
    private static final String SELECT_SINGLE_ITEM_SQL = "SELECT * FROM GPFD.CSV_TO_SQL_MAPPING_TABLE WHERE ID = ";

    private final JdbcTemplate readOnlyJdbcTemplate;
    private final ModelMapper mapper;

    public void fetchReportResults(String sqlCommand, List<MappingTable> mappingTableObjectList)
            throws DatabaseReadException, ReportIdNotFoundException {
        List<Map<String, Object>> resultList = null;

        try {
            log.info("Reading mapping data");
            resultList = readOnlyJdbcTemplate.queryForList(sqlCommand);
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
    }

    public MappingTable fetchReport(int requestId) throws DatabaseReadException {
        List<MappingTable> mappingTableObjectList = new ArrayList<>();
        String sqlCommand = SELECT_SINGLE_ITEM_SQL + requestId;
        fetchReportResults(sqlCommand, mappingTableObjectList);
        return mappingTableObjectList.get(0);
    }

    public List<MappingTable> fetchReportList() throws DatabaseReadException {
        List<MappingTable> mappingTableObjectList = new ArrayList<>();

        fetchReportResults(SELECT_SQL, mappingTableObjectList);

        return mappingTableObjectList;
    }
}
