package uk.gov.laa.pfla.auth.service.dao;

import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
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


    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final ModelMapper mapper = new ModelMapper();

    public MappingTableDao() {
        //empty contructor to allow builder to do its work
    }

    public List<MappingTableModel> fetchReportList() {
        mappingTableObjectList.clear(); // Prevent data accumulating after multiple requests

        List<Map<String, Object>> resultList;

            String query = "SELECT * FROM GPFD.CSV_TO_SQL_MAPPING_TABLE";


            resultList = jdbcTemplate.queryForList(query);
            log.debug("Result list, a list of objects each representing a row in the DB: {}", resultList);

                try {
                    resultList.forEach(obj -> {
                    MappingTableModel mappingTableObject = mapper.map(obj, MappingTableModel.class);
                    mappingTableObjectList.add(mappingTableObject);
                });
                } catch (MappingException e) {
                    log.error("Exception with model map loop: " + e);
                }


            return mappingTableObjectList;




    }
}
