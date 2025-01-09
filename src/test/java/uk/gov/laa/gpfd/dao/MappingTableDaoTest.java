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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest // This uses the whole spring context, switch to @JdbcTest if you switch to a H2 DB
@ActiveProfiles("test")
class MappingTableDaoTest {
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
        assertEquals(1, results.get(0).getId());
        assertEquals(2, results.get(1).getId());
        assertEquals(4, results.get(2).getId());
    }

    @Test
    void shouldReturnSingleReport() {
        MappingTable result = mappingTableDao.fetchReport(4);

        assertEquals(4, result.getId());
    }

    @Test
    void shouldThrowExceptionIfReportNotFound() {
        assertThrows(ReportIdNotFoundException.class,
                () -> mappingTableDao.fetchReport(3));

    }
}
