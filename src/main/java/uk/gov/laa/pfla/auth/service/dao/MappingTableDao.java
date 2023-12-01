package uk.gov.laa.pfla.auth.service.dao;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import uk.gov.laa.pfla.auth.service.builders.MappingTableModelBuilder;
import uk.gov.laa.pfla.auth.service.models.MappingTableModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
@Slf4j
public class MappingTableDao  implements RowMapper<Object> {

    private final List<MappingTableModel> mappingTableObjectList = new ArrayList<>();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final ModelMapper mapper = new ModelMapper();

    public MappingTableDao() {
        //empty contructor to allow builder to do its work
    }

    public MappingTableModel mapRow(ResultSet resultSet, int rowNum) throws SQLException {

        return new MappingTableModelBuilder()
                .withId(resultSet.getInt(1))
                .withReportName(resultSet.getString(2))
                .withSqlString(resultSet.getString(3))
                .withBaseUrl(resultSet.getString(4))
                .withReportPeriod(resultSet.getString(5))
                .withReportOwner(resultSet.getString(6))
                .withReportCreator(resultSet.getString(7))
                .withReportDescription(resultSet.getString(8))
                .withExcelSheetNumber(resultSet.getInt(12))
                .withCsvName(resultSet.getString(13))
                .build();

    }

    public List<MappingTableModel> fetchReportList() {
        mappingTableObjectList.clear(); // Prevent data accumulating after multiple requests

        List<Map<String, Object>> resultList;

            String query = "SELECT * FROM GPFD.CSV_TO_SQL_MAPPING_TABLE";


            resultList = jdbcTemplate.queryForList(query);
            log.info("Result list obj here: " + resultList);

            AtomicInteger i = new AtomicInteger();

                i.getAndIncrement();

                try {
                    resultList.forEach(obj -> {
                    MappingTableModel mappingTableObject = mapper.map(obj, MappingTableModel.class);
                    mappingTableObjectList.add(mappingTableObject);
                });
                } catch (org.modelmapper.MappingException e) {
                    log.error("Exception with model map loop: " + e);
                }


            return mappingTableObjectList;




    }
}
