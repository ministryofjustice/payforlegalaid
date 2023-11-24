package uk.gov.laa.pfla.auth.service.dao;

import lombok.extern.slf4j.Slf4j;
import oracle.jdbc.pool.OracleDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import uk.gov.laa.pfla.auth.service.builders.MappingTableModelBuilder;
import uk.gov.laa.pfla.auth.service.models.MappingTableModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class MappingTableDao {

    private List<MappingTableModel> mappingTableObjectList = new ArrayList<>();

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
        } catch(SQLException e){
            log.error("Error in creating DB Connection: " + e);
        }

//        ResultSet rslt = null;
//        try {
//            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM GPFD.CSV_TO_SQL_MAPPING_TABLE");
//            rslt = stmt.executeQuery();
//            ResultSetMetaData metadata = rslt.getMetaData();
//            log.info("Metadata: " + metadata.toString());
//
//            while (rslt.next()) {
//                log.info("Result column: " + rslt.getString(1));
//                log.info("Result column: " + rslt.getString(2));
//                log.info("Result column: " + rslt.getString(3));
//                log.info("Result column: " + rslt.getString(4));
//                log.info("Result column: " + rslt.getString(5));
//                log.info("Result column: " + rslt.getString(6));
//
//            }
//        }catch(SQLException e){
//            log.error("Error in retrieving results from DB: " + e);
//        }

        // Todo - remove duplicate block

//            String sqlQuery = "SELECT * FROM GPFD.CSV_TO_SQL_MAPPING_TABLE";
//            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sqlQuery);
//
//            for (Map<String, Object> row : rows){
//                Object column1value = row.get("column1");
//                Object column2value = row.get("column2");
//                Object column3value = row.get("column3");
//                Object column4value = row.get("column4");
//                Object column5value = row.get("column5");
//
//                log.info("column1value: " + column1value);
//                log.info("column2value: " + column2value);
//                log.info("column3value: " + column3value);
//                log.info("column4value: " + column4value);
//                log.info("column5value: " + column5value);
//
//
//            }

        return conn;

    }

    public List<MappingTableModel> fetchReportList() {

        mappingTableObjectList.clear(); // Prevent response data accumulating after multiple requests
//
//        //fetch data from database
//        String sql = "SELECT * FROM ";
//
//        mappingTableObjectList = JdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(MappingTableModel.class));
//        log.info("mappingTableObjectList: " + mappingTableObjectList.toString());
//


        Connection conn = setupDB();


//        //create a list of MappingTableModel objects
//        MappingTableModel mappingTableObject1 = new MappingTableModelBuilder()
//                .withId(1).withReportName("Excel_Report_Name-CSV-NAME-sheetnumber")
//                .withReportPeriod("01/08/2023 - 01/09/2023")
//                .withReportOwner("Chancey Mctavish")
//                .withReportCreator("Barry Gibb")
//                .withReportDescription("List all unpaid AP invoices and all outstanding AR debts at the end of the previous month. Detailed data, one row per invoice")
//                .withBaseUrl("www.sharepoint.com/the-folder-we're-using")
//                .withSql("SELECT * FROM SOMETHING")
//                .createMappingTableModel();



        MappingTableModel mappingTableObject2 = new MappingTableModelBuilder()
                .withId(2)
                .withReportName("AP_and_AR_Combined-DEBT-AGING-SUMMARY-4")
                .withSqlString("SELECT * FROM SOMETHING")
                .withBaseUrl("www.sharepoint.com/a-different-folder-we're-using")
                .withReportPeriod("01/07/2023 - 01/09/2023")
                .withReportOwner("Chancey Mctavish")
                .withReportCreator("Sophia Patel")
                .withReportDescription("List all unpaid AP invoices and all outstanding AR debts at the end of the previous month. Summary data, one row per provider")
                .withExcelSheetNumber(11)
                .withCsvName("CSV Name")
                .build();

//
//
        mappingTableObjectList.add(0, queryDB(conn));
        mappingTableObjectList.add(1, mappingTableObject2);
        return mappingTableObjectList;


    }

    private static MappingTableModel queryDB(Connection conn) {
        ResultSet rslt = null;

        int id = 0;
        String reportName = null;
        String sqlString = null;
        String baseUrl = null;
        String reportPeriodString = null;
        String reportOwner = null;
        String reportCreator = null;
        String reportDescription = null;
        Date reportPeriodFrom = null;
        Date reportPeriodTo = null;
        String excelReport = null;
        int excelSheetNum = 0;
        String csvName = null;



        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM GPFD.CSV_TO_SQL_MAPPING_TABLE");
            rslt = stmt.executeQuery();

            while (rslt.next()) {


                id = rslt.getInt(1);
                reportName = rslt.getString(2);
                sqlString = rslt.getString(3);
                baseUrl = rslt.getString(4);
                reportPeriodString = rslt.getString(5);
                reportOwner = rslt.getString(6);
                reportCreator = rslt.getString(7);
                reportDescription = rslt.getString(8);
                reportPeriodFrom = rslt.getDate(9);
                reportPeriodTo = rslt.getDate(10);
                excelReport = rslt.getString(11);
                excelSheetNum = rslt.getInt(12);
                csvName = rslt.getString(13);


                log.info("Result column: " + id);
                log.info("Result column: " + reportName);
                log.info("Result column: " + sqlString);
                log.info("Result column: " + baseUrl);
                log.info("Result column: " + reportPeriodString);
                log.info("Result column: " + reportOwner);
                log.info("Result column: " + reportCreator);
                log.info("Result column: " + reportDescription);
                log.info("Result column: " + reportPeriodFrom);
                log.info("Result column: " + reportPeriodTo);
                log.info("Result column: " + excelReport);
                log.info("Result column: " + excelSheetNum);
                log.info("Result column: " + csvName);



            }
        }catch(SQLException e){
            log.error("Error in retrieving results from DB: " + e);
        }


        return new MappingTableModelBuilder()
                .withId(id)
                .withReportName(reportName)
                .withSqlString(sqlString)
                .withBaseUrl(baseUrl)
                .withReportPeriod(reportPeriodString)
                .withReportOwner(reportOwner)
                .withReportCreator(reportCreator)
                .withReportDescription(reportDescription)
                .withExcelSheetNumber(excelSheetNum)
                .withCsvName(csvName)
                .build();

    }



}
