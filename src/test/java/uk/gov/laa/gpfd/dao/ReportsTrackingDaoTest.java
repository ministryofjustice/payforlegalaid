package uk.gov.laa.gpfd.dao;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.laa.gpfd.data.ReportsTrackingTestDataFactory;
import uk.gov.laa.gpfd.utils.DatabaseUtils;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.laa.gpfd.data.ReportsTestDataFactory.createTestReport;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReportsTrackingDaoTest {

    @Autowired
    private ReportsTrackingDao reportsTrackingDao;

    @Autowired
    private JdbcTemplate writeJdbcTemplate;

    @Autowired
    private DatabaseUtils databaseUtils;

    @BeforeAll
    void setup() {
        databaseUtils.setUpDatabase();
    }

    @Test
    void givenValidReportTrackingData_whenSaveReportsTracking_thenDataIsInsertedIntoDB() {
        UUID id = UUID.fromString("0d4da9ec-b0b3-4371-af10-f375330d85d3");
        // Given
        var testTrackingData = ReportsTrackingTestDataFactory.createBasicReportTracking( createTestReport(id));

        // When
        reportsTrackingDao.saveReportsTracking(testTrackingData);

        // Then
        var lastInsertedRow = findTrackingByReportId(id).get();
        assertEquals(testTrackingData.getReportName(), lastInsertedRow.get("NAME"));
        assertEquals(testTrackingData.getReportUrl(), lastInsertedRow.get("REPORT_URL"));
        assertNotNull(lastInsertedRow.get("CREATION_DATE"));
        assertEquals(testTrackingData.getTemplateUrl(), lastInsertedRow.get("TEMPLATE_URL"));

    }

    @Test
    void givenNullReportUrl_whenSaveReportsTracking_thenNoDataIsInserted() {
        // Given
        UUID nonExistentReportId = UUID.randomUUID();
        var existingTrackingBefore = findTrackingByReportId(nonExistentReportId);

        // When
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class,
                () -> reportsTrackingDao.saveReportsTracking(ReportsTrackingTestDataFactory.createBasicReportTracking(
                        createTestReport(nonExistentReportId)
                )));

        // Then
        var existingTrackingAfter = findTrackingByReportId(nonExistentReportId);
        assertTrue(existingTrackingBefore.isEmpty(), "Should not have tracking record before insertion attempt");
        assertTrue(existingTrackingAfter.isEmpty(), "Should not have tracking record after failed insertion");
    }

    @Test
    void givenInvalidReportId_whenSaveReportsTracking_thenNoDataIsInserted() {
        // Given
        var nonExistentReportId = UUID.fromString("00000000-0000-0000-0001-000000000002");
        var testTrackingData = ReportsTrackingTestDataFactory.createBasicReportTracking(
                createTestReport(nonExistentReportId)
        );
        var existingTrackingBefore = findTrackingByReportId(nonExistentReportId);

        // When
        assertThrows(DataIntegrityViolationException.class,
                () -> reportsTrackingDao.saveReportsTracking(testTrackingData));

        // Then
        var existingTrackingAfter = findTrackingByReportId(nonExistentReportId);
        assertTrue(existingTrackingBefore.isEmpty(), "Should not have tracking record before insertion attempt");
        assertTrue(existingTrackingAfter.isEmpty(), "Should not have tracking record after failed insertion");
    }

    private Optional<Map<String, Object>> findTrackingByReportId(UUID reportId) {
        var results = writeJdbcTemplate.queryForList(
                "SELECT * FROM GPFD.REPORTS_TRACKING WHERE REPORT_ID = ?",
                reportId
        );
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

}
