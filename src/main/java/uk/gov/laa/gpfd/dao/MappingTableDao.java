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
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MappingTableDao {
    private static final String SELECT_SQL = "SELECT * FROM GPFD.CSV_TO_SQL_MAPPING_TABLE ORDER BY ID ASC";
    private static final String SELECT_SINGLE_ITEM_SQL = "SELECT * FROM GPFD.CSV_TO_SQL_MAPPING_TABLE WHERE ID = ?";

    private final JdbcTemplate readOnlyJdbcTemplate;
    private final ModelMapper mapper;

    private void fetchReportResults(List<MappingTable> mappingTableObjectList,
                                   String sqlCommand,
                                   Object ... args)
            throws DatabaseReadException, ReportIdNotFoundException {
        List<Map<String, Object>> resultList = null;

        try {
            log.info("Reading mapping data");
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
                MappingTable mappingTableObject = mapper.map(obj, MappingTable.class);
                mappingTableObjectList.add(mappingTableObject);
            });
        } catch (MappingException e) {
            log.error("Exception with model map loop: %s", e);
        }
    }

    public MappingTable fetchReport(UUID requestId) throws DatabaseReadException {
        List<MappingTable> mappingTableObjectList = new ArrayList<>();
        fetchReportResults(mappingTableObjectList, SELECT_SINGLE_ITEM_SQL, requestId.toString());
        return mappingTableObjectList.get(0);
    }

    public List<MappingTable> fetchReportList() throws DatabaseReadException {
        List<MappingTable> mappingTableObjectList = new ArrayList<>();

        fetchReportResults(mappingTableObjectList, SELECT_SQL);

        return mappingTableObjectList;
    }
}
