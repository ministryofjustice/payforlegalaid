package uk.gov.laa.pfla.auth.service.dao;

import lombok.extern.slf4j.Slf4j;
import oracle.jdbc.pool.OracleDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import uk.gov.laa.pfla.auth.service.builders.MappingTableModelBuilder;
import uk.gov.laa.pfla.auth.service.models.MappingTableModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    public void setup() throws Exception{
        OracleDataSource ods = new OracleDataSource();
        ods.setURL(databaseUrl); // jdbc:oracle:thin@//[hostname]:[port]/[DB service name]
        ods.setUser(databaseUsername);
        ods.setPassword(databasePassword);
        Connection conn = ods.getConnection();

        PreparedStatement stmt = conn.prepareStatement("SELECT 'Hello World!' FROM dual");
        ResultSet rslt = stmt.executeQuery();
        while (rslt.next()) {
            log.info(rslt.getString(1));
        }
    }

    public List<MappingTableModel> fetchReportList() {

//        mappingTableObjectList.clear(); // Prevent response data accumulating after multiple requests
//
//        //fetch data from database
//        String sql = "SELECT * FROM ";
//
//        mappingTableObjectList = JdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(MappingTableModel.class));
//        log.info("mappingTableObjectList: " + mappingTableObjectList.toString());
//

    log.info("Url: " + databaseUrl + "username: " + databaseUsername + "Passowrd: " + databasePassword);



        //create a list of MappingTableModel objects
        MappingTableModel mappingTableObject1 = new MappingTableModelBuilder()
                .withId(1).withReportName("Excel_Report_Name-CSV-NAME-sheetnumber")
                .withReportPeriod("01/08/2023 - 01/09/2023")
                .withReportOwner("Chancey Mctavish")
                .withReportCreator("Barry Gibb")
                .withReportDescription("List all unpaid AP invoices and all outstanding AR debts at the end of the previous month. Detailed data, one row per invoice")
                .withBaseUrl("www.sharepoint.com/the-folder-we're-using")
                .withSql("SELECT * FROM SOMETHING")
                .createMappingTableModel();



        MappingTableModel mappingTableObject2 = new MappingTableModelBuilder()
                .withId(2).withReportName("AP_and_AR_Combined-DEBT-AGING-SUMMARY-4")
                .withReportPeriod("01/07/2023 - 01/09/2023")
                .withReportOwner("Chancey Mctavish")
                .withReportCreator("Sophia Patel")
                .withReportDescription("List all unpaid AP invoices and all outstanding AR debts at the end of the previous month. Summary data, one row per provider")
                .withBaseUrl("www.sharepoint.com/a-different-folder-we're-using")
                .withSql("SELECT * FROM SOMETHING")
                .createMappingTableModel();

//
//
        mappingTableObjectList.add(0, mappingTableObject1);
        mappingTableObjectList.add(1, mappingTableObject2);
        return mappingTableObjectList;


    }


}
