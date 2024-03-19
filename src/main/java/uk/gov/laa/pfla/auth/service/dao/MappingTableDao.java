package uk.gov.laa.pfla.auth.service.dao;

import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import uk.gov.laa.pfla.auth.service.exceptions.DatabaseReadException;
import uk.gov.laa.pfla.auth.service.models.MappingTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class MappingTableDao {

    private final List<MappingTableModel> mappingTableObjectList = new ArrayList<>();

    public static final Logger log = LoggerFactory.getLogger(MappingTableDao.class);


    private final JdbcTemplate readOnlyJdbcTemplate;
    private final ModelMapper mapper = new ModelMapper();

    // Using the JDBCTemplate bean defined in  PflaApplication (the @SpringBootApplication / run class) which uses the
    //read only DB datasource/credentials
    @Autowired
    public MappingTableDao(JdbcTemplate readOnlyJdbcTemplate) {
        this.readOnlyJdbcTemplate = readOnlyJdbcTemplate;
    }


    public List<MappingTableModel> fetchReportList() throws DatabaseReadException {
        mappingTableObjectList.clear(); // Prevent data accumulating after multiple requests

        List<Map<String, Object>> resultList;

            String query = "SELECT * FROM GPFD.CSV_TO_SQL_MAPPING_TABLE";


        try {
            log.info("Reading from mapping table");
            resultList = readOnlyJdbcTemplate.queryForList(query);
        } catch (DataAccessException e) {
            throw new DatabaseReadException("Error reading from DB: " + e);
        }

        if (resultList.isEmpty()) {
            throw new DatabaseReadException("No results returned from mapping table");
        }


                try {
                    resultList.forEach(obj -> {
                    MappingTableModel mappingTableObject = mapper.map(obj, MappingTableModel.class);
                    mappingTableObjectList.add(mappingTableObject);
                });
                } catch (MappingException e) {
                    log.error("Exception with model map loop: %s", e);
                }


            return mappingTableObjectList;




    }
}
