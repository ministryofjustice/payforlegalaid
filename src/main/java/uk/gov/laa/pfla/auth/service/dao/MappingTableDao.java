package uk.gov.laa.pfla.auth.service.dao;

import lombok.extern.slf4j.Slf4j;
import oracle.jdbc.pool.OracleDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import uk.gov.laa.pfla.auth.service.builders.MappingTableModelBuilder;
import uk.gov.laa.pfla.auth.service.models.MappingTableModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class MappingTableDao  implements RowMapper<Object> {

    private final List<MappingTableModel> mappingTableObjectList = new ArrayList<>();

//    @Autowired
//    private JdbcTemplate jdbcTemplate;

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username}")
    private String databaseUsername;

    @Value("${spring.datasource.password}")
    private String databasePassword;

    public MappingTableDao() {
        //empty contructor to allow builder to do its work
    }

    public Connection setupDB(){ //Todo - create custom exception

        OracleDataSource ods = null;
        try {
                ods = new OracleDataSource();
                ods.setURL(databaseUrl); // jdbc:oracle:thin@//[hostname]:[port]/[DB service name]
                ods.setUser(databaseUsername);
                ods.setPassword(databasePassword);
            } catch(SQLException e){
            log.error("Error in Oracle Datasource JDBC setup: " + e);
        }

        Connection conn = null;
        try {
             conn = ods.getConnection();
        } catch(SQLException | NullPointerException e){
            log.error("Error in creating DB Connection: " + e);
        }


        return conn;

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
        ResultSet rslt;
        int rowNumber = 0;
        mappingTableObjectList.clear(); // Prevent response data accumulating after multiple requests
        MappingTableModel mappingTableObject;


        Connection conn = null;
        try {
            conn = setupDB();
        } catch (Exception e) {
            log.error("Database connection error:" + e );
        }


        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM GPFD.CSV_TO_SQL_MAPPING_TABLE");
            rslt = stmt.executeQuery();
            log.info("Result here: " + rslt);

            while(rslt.next()){

                mappingTableObject = mapRow(rslt, rowNumber);
                log.info("mapping Object: " + mappingTableObject.getId() + "   " + mappingTableObject.getReportName());
                mappingTableObjectList.add(mappingTableObject);
                rowNumber++;

                log.info("Row number here: " + rslt.getRow());

            }
        }catch(SQLException e){
            log.error("Error in retrieving results from DB: " + e);
        }

        return mappingTableObjectList;


    }


}
