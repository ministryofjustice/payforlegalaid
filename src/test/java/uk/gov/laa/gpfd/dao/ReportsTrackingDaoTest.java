package uk.gov.laa.gpfd.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.laa.gpfd.model.ReportsTracking;

@SpringBootTest // This uses the whole spring context, switch to @JdbcTest if you switch to a H2 DB
@ActiveProfiles("test")
class ReportsTrackingDaoTest extends BaseDaoTest{

    public static final UUID DEFAULT_ID = UUID.fromString("0d4da9ec-b0b3-4371-af10-f375330d85d1");

    @Autowired
    private ReportsTrackingDao reportsTrackingDao;

    @Test
    void list_ShouldReturnAllReportsTrackingObjects() {
        List<Map<String, Object>> reportsTrackingList = reportsTrackingDao.list();

        assertEquals(1, reportsTrackingList.size());
        assertEquals("00000000-0000-0000-0001-000000000001", reportsTrackingList.get(0).get("ID").toString());
        assertEquals("Test Report Name", reportsTrackingList.get(0).get("NAME").toString()); // Testing the gpfd_data.sql test data has populated into the DB properly
    }


    @Test
    void testSaveReportsTracking() {
        // Arrange
        String insertedReportName = "Test Report 2";
        UUID insertedReportUuid = UUID.fromString("f46b4d3d-c100-429a-bf9a-6c3305dbdbf5");
        String insertedReportUrl = "http://example.com/report2";
        LocalDateTime creationTime = LocalDateTime.now();
        Timestamp insertedCreationTime = Timestamp.valueOf(creationTime.withNano(0));
        String insertedReportDownloadedBy = "00000000-0000-0000-0003-000000000002";
        String insertedReportCreator = "00000000-0000-0000-0005-000000000002";
        String insertedReportOwner = "00000000-0000-0000-0006-000000000002";
        String insertedReportOutputType = "00000000-0000-0000-0007-000000000002";
        String insertedTemplateUrl = "test template URL2";

        ReportsTracking reportsTracking = new ReportsTracking(DEFAULT_ID, insertedReportName, insertedReportUuid, insertedCreationTime, insertedReportDownloadedBy,
            insertedReportCreator, insertedReportOwner, insertedReportOutputType, insertedTemplateUrl, insertedReportUrl);

        reportsTrackingDao.saveReportsTracking(reportsTracking);

        // Assert
        List<Map<String, Object>> reportsTrackingList = reportsTrackingDao.list();
        String reportName = reportsTrackingList.get(1).get("NAME").toString();
        Timestamp reportCreationTime = (Timestamp) reportsTrackingList.get(1).get("CREATION_DATE"); //index 1 because index 0 is populated by gpfd_data.sql

        assertEquals(2, reportsTrackingList.size());
        Assertions.assertNotNull(reportsTrackingList.get(1).get("ID"));
        assertEquals(insertedReportName, reportName);
        assertEquals(insertedReportUrl, reportsTrackingList.get(1).get("REPORT_URL"));
        assertEquals(insertedCreationTime, reportCreationTime);
        assertEquals(insertedTemplateUrl, reportsTrackingList.get(1).get("TEMPLATE_URL"));
    }
}