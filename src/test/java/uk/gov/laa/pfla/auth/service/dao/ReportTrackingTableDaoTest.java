
package uk.gov.laa.pfla.auth.service.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.time.LocalDateTime;
import uk.gov.laa.pfla.auth.service.dao.ReportTrackingTableDao;
import uk.gov.laa.pfla.auth.service.models.ReportTrackingTableModel;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
//@JdbcTest //use this instead of @SpringBootTest if switching from DEV MOJFIN to a H2 database
@Import(ReportTrackingTableDao.class) // Import your DAO class into the Spring context
@SpringBootTest // This uses the whole spring context, switch to @JdbcTest if you switch to a H2 DB
@ActiveProfiles("test")
class ReportTrackingTableDAOTest {

    @Autowired
    private JdbcTemplate writeJdbcTemplate;

    @Autowired
    private ReportTrackingTableDao reportTrackingTableDAO;

    @BeforeEach
    void setup() {

    }

    @Test
    void testUpdateTrackingTable() {
        // Arrange
        JdbcTemplate localJdbcTemplate = this.writeJdbcTemplate;

        String reportName = "Test Report";
        String reportUrl = "http://example.com/report";
        LocalDateTime creationTime = LocalDateTime.now();
        int mappingId = 1;
        String reportGeneratedBy = "TestUser";

        ReportTrackingTableModel reportTrackingTableModel = new ReportTrackingTableModel(reportName, reportUrl, creationTime, mappingId, reportGeneratedBy);

        // Act
        reportTrackingTableDAO.updateTrackingTable(reportTrackingTableModel);

        // Assert
        String sql = "SELECT COUNT(*) FROM GPFD.REPORT_TRACKING WHERE ReportName = ? AND ReportUrl = ? AND MappingID = ?";
        int count = localJdbcTemplate.queryForObject(sql, new Object[]{reportName, reportUrl, mappingId}, Integer.class);

        assertEquals(1, count);

    }
}
