package uk.gov.laa.gpfd.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.laa.gpfd.model.ReportTrackingTable;
import uk.gov.laa.gpfd.utils.FileUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest // This uses the whole spring context, switch to @JdbcTest if you switch to a H2 DB
@ActiveProfiles("test")
class ReportTrackingTableDAOTest {

    @Autowired
    private JdbcTemplate writeJdbcTemplate;

    @Autowired
    private ReportTrackingTableDao reportTrackingTableDao;

    @BeforeEach
    void setup() {

        String sqlSchema = FileUtils.readResourceToString("schema.sql");
        String sqlData = FileUtils.readResourceToString("data.sql");

        writeJdbcTemplate.execute(sqlSchema);
        writeJdbcTemplate.execute(sqlData);

    }

    @AfterEach
    void resetDatabase() {

        writeJdbcTemplate.update("TRUNCATE TABLE GPFD.REPORT_TRACKING");
        writeJdbcTemplate.update("DROP SEQUENCE GPFD_TRACKING_TABLE_SEQUENCE");

    }

    @Test
    void list_ShouldReturnAllreportTrackingTableObjects() {
        List<Map<String, Object>> reportTrackingTableList = reportTrackingTableDao.list();

        String reportName = reportTrackingTableList.get(0).get("REPORT_NAME").toString();

        assertEquals(1, reportTrackingTableList.size());
        assertEquals(1, reportTrackingTableList.get(0).get("ID"));
        assertEquals("Initial Test Report Name", reportName); // Testing the data.sql test data has populated into the DB properly
    }


    @Test
    void testUpdateTrackingTable() {
        // Arrange
        int insertedId = 0; //This will be overridden  when the DAO uses an Oracle sequence to populate the id
        String insertedReportName = "Test Report";
        String insertedReportUrl = "http://example.com/report";
        LocalDateTime creationTime = LocalDateTime.now();
        Timestamp insertedCreationTime = Timestamp.valueOf(creationTime.withNano(0));
        int insertedMappingId = 2;
        String insertedReportGeneratedBy = "TestUser";

        ReportTrackingTable reportTrackingTable = new ReportTrackingTable(insertedId, insertedReportName, insertedReportUrl, insertedCreationTime, insertedMappingId, insertedReportGeneratedBy);

        // Act
        reportTrackingTableDao.updateTrackingTable(reportTrackingTable);

        // Assert
        List<Map<String, Object>> reportTrackingTableList = reportTrackingTableDao.list();
        String reportName = reportTrackingTableList.get(1).get("REPORT_NAME").toString();
        Timestamp reportCreationTime = (Timestamp) reportTrackingTableList.get(1).get("CREATION_TIME"); //index 1 because index 0 is populated by data.sql

        assertEquals(2, reportTrackingTableList.size());
        assertEquals(2, reportTrackingTableList.get(1).get("ID")); //2 is the value of the id since the database sequence will increment up to 2 after inserting 2 rows of data
        assertEquals(insertedReportName, reportName);
        assertEquals(insertedReportUrl, reportTrackingTableList.get(1).get("REPORT_URL"));
        assertEquals(reportCreationTime, insertedCreationTime);
        assertEquals(insertedMappingId, reportTrackingTableList.get(1).get("MAPPING_ID"));
        assertEquals(insertedReportGeneratedBy, reportTrackingTableList.get(1).get("REPORT_GENERATED_BY"));

    }
}