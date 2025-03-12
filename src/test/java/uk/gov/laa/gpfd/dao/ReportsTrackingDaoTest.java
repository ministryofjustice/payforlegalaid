package uk.gov.laa.gpfd.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.laa.gpfd.model.ReportsTracking;
import uk.gov.laa.gpfd.utils.FileUtils;

@SpringBootTest // This uses the whole spring context, switch to @JdbcTest if you switch to a H2 DB
@ActiveProfiles("test")
class ReportsTrackingDaoTest {

    public static final UUID DEFAULT_ID = UUID.fromString("0d4da9ec-b0b3-4371-af10-f375330d85d1");

    @Autowired
    private JdbcTemplate writeJdbcTemplate;

    @Autowired
    private ReportsTrackingDao reportsTrackingDao;

    @BeforeEach
    void setup() {
        String sqlSchema = FileUtils.readResourceToString("gpfd_schema.sql");
        String sqlData = FileUtils.readResourceToString("gpfd_data.sql");

        writeJdbcTemplate.execute(sqlSchema);
        writeJdbcTemplate.execute(sqlData);
    }

    @AfterEach
    void resetDatabase() {
        writeJdbcTemplate.update("TRUNCATE TABLE GPFD.REPORTS_TRACKING");
        writeJdbcTemplate.update("TRUNCATE TABLE GPFD.CSV_TO_SQL_MAPPING_TABLE");
    }

    @Test
    void list_ShouldReturnAllReportsTrackingObjects() {
        List<Map<String, Object>> reportsTrackingList = reportsTrackingDao.list();

        String reportName = reportsTrackingList.get(0).get("REPORT_NAME").toString();

        assertEquals(1, reportsTrackingList.size());
        assertEquals("0d4da9ec-b0b3-4371-af10-f375330d85d3", reportsTrackingList.get(0).get("ID").toString());
        assertEquals("Initial Test Report Name", reportName); // Testing the gpfd_data.sql test data has populated into the DB properly
    }


    @Test
    void testSaveReportsTracking() {
        // Arrange
        String insertedReportName = "Test Report";
        String insertedReportUrl = "http://example.com/report";
        LocalDateTime creationTime = LocalDateTime.now();
        Timestamp insertedCreationTime = Timestamp.valueOf(creationTime.withNano(0));
        String insertedReportDownloadedBy = "ReportDownloader";
        String insertedReportGeneratedBy = "ReportGenerator";
        String insertedReportCreator = "ReportCreator";
        String insertedReportOwner = "ReportOwner";
        String insertedReportOutputType = "ReportOutputType";
        String insertedTemplateUrl = "TemplateUrl";

        ReportsTracking reportsTracking = new ReportsTracking(DEFAULT_ID, insertedReportName, DEFAULT_ID, insertedCreationTime, insertedReportDownloadedBy,
            insertedReportCreator, insertedReportOwner, insertedReportOutputType, insertedTemplateUrl, insertedReportUrl);

        reportsTrackingDao.saveReportsTracking(reportsTracking);

        // Assert
        List<Map<String, Object>> reportsTrackingList = reportsTrackingDao.list();
        String reportName = reportsTrackingList.get(1).get("REPORT_NAME").toString();
        Timestamp reportCreationTime = (Timestamp) reportsTrackingList.get(1).get("CREATION_TIME"); //index 1 because index 0 is populated by gpfd_data.sql

        assertEquals(2, reportsTrackingList.size());
        assertNotNull(reportsTrackingList.get(1).get("ID"));
        assertEquals(insertedReportName, reportName);
        assertEquals(insertedReportUrl, reportsTrackingList.get(1).get("REPORT_URL"));
        assertEquals(reportCreationTime, insertedCreationTime);
        assertEquals("0d4da9ec-b0b3-4371-af10-f375330d85d1", reportsTrackingList.get(1).get("MAPPING_ID").toString());
        assertEquals(insertedReportGeneratedBy, reportsTrackingList.get(1).get("REPORT_GENERATED_BY"));
    }
}