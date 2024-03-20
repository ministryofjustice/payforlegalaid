package uk.gov.laa.pfla.auth.service.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.laa.pfla.auth.service.utils.FileUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

//@ExtendWith(SpringExtension.class)
//@JdbcTest //use this instead of @SpringBootTest if switching from DEV MOJFIN to a H2 database
//@DataJdbcTest(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
//        classes = AbstractJdbcConfiguration.class))
//This is a slimmed down version of @jdbctest, using an in-memory DB
//@Import(ReportTrackingTableDao.class) // Import your DAO class into the Spring context
@SpringBootTest // This uses the whole spring context, switch to @JdbcTest if you switch to a H2 DB
@ActiveProfiles("test")
//@Sql({"/schema.sql", "/data.sql"})
@DataJdbcTest
class ReportTrackingTableDAOTest {

    @Autowired
    private JdbcTemplate writeJdbcTemplate;

    @Autowired
    private ReportTrackingTableDao reportTrackingTableDao;

//    @BeforeEach
//    void setup() {
//
//    }

//    @Autowired
//    public ReportTrackingTableDAOTest(JdbcTemplate writeJdbcTemplate) {
//        this.writeJdbcTemplate = writeJdbcTemplate;
//        reportTrackingTableDAO = new ReportTrackingTableDao(writeJdbcTemplate);
//    }

    @BeforeEach
    void setup() {

        String sqlSchema = FileUtils.readResourceToString("schema.sql");
        String sqlData = FileUtils.readResourceToString("data.sql");

        writeJdbcTemplate.execute(sqlSchema);
        writeJdbcTemplate.execute(sqlData);

    }

    @Test
    void list_ShouldReturnAllreportTrackingTableObjects() {
        List<Map<String, Object>> reportTrackingTableList = reportTrackingTableDao.list();
        assertEquals(1, reportTrackingTableList.size());
    }
//    @Test
//    void testUpdateTrackingTable() {
//        // Arrange
//        JdbcTemplate localJdbcTemplate = this.writeJdbcTemplate;
//
//        String reportName = "Test Report";
//        String reportUrl = "http://example.com/report";
//        LocalDateTime creationTime = LocalDateTime.now();
//        int mappingId = 1;
//        String reportGeneratedBy = "TestUser";
//
//        ReportTrackingTableModel reportTrackingTableModel = new ReportTrackingTableModel(reportName, reportUrl, creationTime, mappingId, reportGeneratedBy);
//
//        // Act
//        reportTrackingTableDAO.updateTrackingTable(reportTrackingTableModel);
//
//        // Assert
//        String sql = "SELECT COUNT(*) FROM GPFD.REPORT_TRACKING WHERE ReportName = ? AND ReportUrl = ? AND MappingID = ?";
//        int count = localJdbcTemplate.queryForObject(sql, new Object[]{reportName, reportUrl, mappingId}, Integer.class);
//
//        assertEquals(1, count);
//
//    }
}