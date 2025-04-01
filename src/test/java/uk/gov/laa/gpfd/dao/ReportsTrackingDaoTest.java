package uk.gov.laa.gpfd.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.laa.gpfd.model.ReportsTracking;

@SpringBootTest // This uses the whole spring context, switch to @JdbcTest if you switch to a H2 DB
@ActiveProfiles("test")
class ReportsTrackingDaoTest extends BaseDaoTest{

    public static final UUID DEFAULT_ID = UUID.fromString("0d4da9ec-b0b3-4371-af10-f375330d85d1");
    public static final int EXPECTED_ROWCOUNT_AFTER_SAVING_ONE_REPORT_TRACKING = 2;
    public static final int EXPECTED_ROWCOUNT_AFTER_NO_REPORT_TRACKING = 1;

    @Autowired
    private ReportsTrackingDao reportsTrackingDao;

    @Autowired
    private JdbcTemplate writeJdbcTemplate;

    @Test
    void givenValidReportTrackingData_whenSaveReportsTracking_thenDataIsInsertedIntoDB() {
        // Given
        ReportTrackingData testTrackingData = getTestTrackingData();

        // When
        saveReportTracking(testTrackingData);

        // Then
        List<Map<String, Object>> results = getAllReportsTrackingsFromDb();

        assertEquals(EXPECTED_ROWCOUNT_AFTER_SAVING_ONE_REPORT_TRACKING, results.size());
        Map<String, Object> lastInsertedRow = results.get(1);
        assertEquals(testTrackingData.getReportName(), lastInsertedRow.get("NAME"));
        assertEquals(testTrackingData.getReportUrl(), lastInsertedRow.get("REPORT_URL"));
        assertEquals(testTrackingData.getCreationTimestamp(), lastInsertedRow.get("CREATION_DATE"));
        assertEquals(testTrackingData.getTemplateUrl(), lastInsertedRow.get("TEMPLATE_URL"));

    }

    @Test
    void givenNullReportUrl_whenSaveReportsTracking_thenNoDataIsInserted() {
        // Given
        ReportTrackingData testTrackingData = getTestTrackingData();
        testTrackingData.setReportUrl(null);

        // When
        saveReportTracking(testTrackingData);

        // Then
        List<Map<String, Object>> results = getAllReportsTrackingsFromDb();
        assertEquals(EXPECTED_ROWCOUNT_AFTER_NO_REPORT_TRACKING, results.size());
    }

    @Test
    void givenInvalidReportId_whenSaveReportsTracking_thenNoDataIsInserted() {
        // Given
        ReportTrackingData testTrackingData = getTestTrackingData();
        testTrackingData.setReportUuid(UUID.fromString("00000000-0000-0000-0001-000000000002"));

        // When
        saveReportTracking(testTrackingData);

        // Then
        List<Map<String, Object>> results = getAllReportsTrackingsFromDb();
        assertEquals(EXPECTED_ROWCOUNT_AFTER_NO_REPORT_TRACKING, results.size());
    }

    @Test
    void givenNullReportId_whenSaveReportsTracking_thenNoDataIsInserted() {
        // Given
        ReportTrackingData testTrackingData = getTestTrackingData();
        testTrackingData.setReportUuid(null);

        // When
        saveReportTracking(testTrackingData);

        // Then
        List<Map<String, Object>> results = getAllReportsTrackingsFromDb();
        assertEquals(EXPECTED_ROWCOUNT_AFTER_NO_REPORT_TRACKING, results.size());
    }

    @Data
    @AllArgsConstructor
    static class ReportTrackingData {
        String reportName;
        UUID reportUuid;
        String reportUrl;
        LocalDateTime creationTime;
        Timestamp creationTimestamp;
        String reportDownloadedBy;
        String reportCreator;
        String reportOwner;
        String reportOutputType;
        String templateUrl;
    }

    ReportTrackingData getTestTrackingData() {
        return new ReportTrackingData(
            "Test Report 2",
            UUID.fromString("f46b4d3d-c100-429a-bf9a-6c3305dbdbf5"),
            "http://example.com/report2",
            LocalDateTime.now(),
            Timestamp.valueOf(LocalDateTime.now().withNano(0)),
            "00000000-0000-0000-0003-000000000002",
            "00000000-0000-0000-0005-000000000002",
            "00000000-0000-0000-0006-000000000002",
            "00000000-0000-0000-0007-000000000002",
            "test template URL2"
        );
    }

    private void saveReportTracking(ReportTrackingData testTrackingData) {
        ReportsTracking reportsTracking = new ReportsTracking(
            DEFAULT_ID,
            testTrackingData.getReportName(),
            testTrackingData.getReportUuid(),
            testTrackingData.getCreationTimestamp(),
            testTrackingData.getReportCreator(),
            testTrackingData.getReportOwner(),
            testTrackingData.getReportOutputType(),
            testTrackingData.getTemplateUrl(),
            testTrackingData.getReportUrl()
        );

        reportsTrackingDao.saveReportsTracking(reportsTracking);
    }

    @NotNull
    private List<Map<String, Object>> getAllReportsTrackingsFromDb() {
      String sql = "SELECT * FROM GPFD.REPORTS_TRACKING";
      return writeJdbcTemplate.queryForList(sql);
    }

}