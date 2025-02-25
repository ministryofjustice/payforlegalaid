package uk.gov.laa.gpfd.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.laa.gpfd.utils.FileUtils;

@SpringBootTest // This uses the whole spring context, switch to @JdbcTest if you switch to a H2 DB
@ActiveProfiles("test")
public class ReportOutputTypesDaoTest {
    @Autowired
    private JdbcTemplate readOnlyJdbcTemplate;

    @Autowired
    private ReportOutputTypesDao reportOutputTypesDao;

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

}
