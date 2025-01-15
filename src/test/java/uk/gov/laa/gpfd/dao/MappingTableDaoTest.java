package uk.gov.laa.gpfd.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;
import uk.gov.laa.gpfd.model.MappingTable;
import uk.gov.laa.gpfd.utils.FileUtils;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest // This uses the whole spring context, switch to @JdbcTest if you switch to a H2 DB
@ActiveProfiles("test")
class MappingTableDaoTest {
    public static final UUID DEFAULT_ID = UUID.fromString("0d4da9ec-b0b3-4371-af10-f375330d85d1");

    @Autowired
    private JdbcTemplate readOnlyJdbcTemplate;

    @Autowired
    private MappingTableDao mappingTableDao;

    @BeforeEach
    void setup() {
        String sqlSchema = FileUtils.readResourceToString("gpfd_schema.sql");
        String sqlData = FileUtils.readResourceToString("gpfd_data.sql");

        readOnlyJdbcTemplate.execute(sqlSchema);
        readOnlyJdbcTemplate.execute(sqlData);
    }

    @AfterEach
    void resetDatabase() {

        readOnlyJdbcTemplate.update("TRUNCATE TABLE GPFD.REPORT_TRACKING");
        readOnlyJdbcTemplate.update("DROP SEQUENCE GPFD_TRACKING_TABLE_SEQUENCE");
        readOnlyJdbcTemplate.update("TRUNCATE TABLE GPFD.CSV_TO_SQL_MAPPING_TABLE");
    }

    @Test
    void shouldReturnAllReportsInOrder() {
        List<MappingTable> results = mappingTableDao.fetchReportList();

        assertEquals(3, results.size());
        assertEquals("0d4da9ec-b0b3-4371-af10-f375330d85d1", results.get(0).getId().toString());
        assertEquals("0d4da9ec-b0b3-4371-af10-f375330d85d2", results.get(1).getId().toString());
        assertEquals("0d4da9ec-b0b3-4371-af10-f375330d85d3", results.get(2).getId().toString());
    }

    @Test
    void shouldReturnSingleReport() {
        MappingTable result = mappingTableDao.fetchReport(DEFAULT_ID);

        assertEquals("0d4da9ec-b0b3-4371-af10-f375330d85d1", result.getId().toString());
    }

    @Test
    void shouldThrowExceptionIfReportNotFound() {
        var requestId = UUID.fromString("1d4da9ec-b0b3-4371-af10-f375330d85d1");
        assertThrows(ReportIdNotFoundException.class, () -> mappingTableDao.fetchReport(requestId));
    }
}
