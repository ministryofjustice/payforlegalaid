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
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest // This uses the whole spring context, switch to @JdbcTest if you switch to a H2 DB
@ActiveProfiles("test")
class ReportTrackingTableDAOTest {

    public static final UUID DEFAULT_ID = UUID.fromString("0d4da9ec-b0b3-4371-af10-f375330d85d1");

    @Autowired
    private JdbcTemplate writeJdbcTemplate;

    @Autowired
    private ReportTrackingTableDao reportTrackingTableDao;

    @BeforeEach
    void setup() {
        String sqlSchema = FileUtils.readResourceToString("gpfd_schema.sql");
        String sqlData = FileUtils.readResourceToString("gpfd_data.sql");

        writeJdbcTemplate.execute(sqlSchema);
        writeJdbcTemplate.execute(sqlData);
    }

    @AfterEach
    void resetDatabase() {
        writeJdbcTemplate.update("TRUNCATE TABLE GPFD.REPORT_TRACKING");
        writeJdbcTemplate.update("TRUNCATE TABLE GPFD.CSV_TO_SQL_MAPPING_TABLE");
    }

    @Test
    void list_ShouldReturnAllreportTrackingTableObjects() {
        List<Map<String, Object>> reportTrackingTableList = reportTrackingTableDao.list();

        String reportName = reportTrackingTableList.get(0).get("REPORT_NAME").toString();

        assertEquals(1, reportTrackingTableList.size());
        assertEquals("0d4da9ec-b0b3-4371-af10-f375330d85d3", reportTrackingTableList.get(0).get("ID").toString());
        assertEquals("Initial Test Report Name", reportName); // Testing the gpfd_data.sql test data has populated into the DB properly
    }


    @Test
    void testUpdateTrackingTable() {
        // Arrange
        String insertedReportName = "Test Report";
        String insertedReportUrl = "http://example.com/report";
        LocalDateTime creationTime = LocalDateTime.now();
        Timestamp insertedCreationTime = Timestamp.valueOf(creationTime.withNano(0));
        String insertedReportGeneratedBy = "TestUser";

        ReportTrackingTable reportTrackingTable = new ReportTrackingTable(DEFAULT_ID, insertedReportName, insertedReportUrl, insertedCreationTime, DEFAULT_ID, insertedReportGeneratedBy);

        // Act
        reportTrackingTableDao.updateTrackingTable(reportTrackingTable);

        // Assert
        List<Map<String, Object>> reportTrackingTableList = reportTrackingTableDao.list();
        String reportName = reportTrackingTableList.get(1).get("REPORT_NAME").toString();
        Timestamp reportCreationTime = (Timestamp) reportTrackingTableList.get(1).get("CREATION_TIME"); //index 1 because index 0 is populated by gpfd_data.sql

        assertEquals(2, reportTrackingTableList.size());
        assertNotNull(reportTrackingTableList.get(1).get("ID"));
        assertEquals(insertedReportName, reportName);
        assertEquals(insertedReportUrl, reportTrackingTableList.get(1).get("REPORT_URL"));
        assertEquals(reportCreationTime, insertedCreationTime);
        assertEquals("0d4da9ec-b0b3-4371-af10-f375330d85d1", reportTrackingTableList.get(1).get("MAPPING_ID").toString());
        assertEquals(insertedReportGeneratedBy, reportTrackingTableList.get(1).get("REPORT_GENERATED_BY"));
    }
}