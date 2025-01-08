package uk.gov.laa.gpfd.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.laa.gpfd.exception.DatabaseReadException;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;
import uk.gov.laa.gpfd.utils.FileUtils;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest // This uses the whole spring context, switch to @JdbcTest if you switch to a H2 DB
@ActiveProfiles("test")
public class ReportViewsDaoTest {
    @Autowired
    private JdbcTemplate writeJdbcTemplate;

    @Autowired
    private ReportViewsDao reportViewsDao;

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
        writeJdbcTemplate.update("DROP SEQUENCE GPFD_TRACKING_TABLE_SEQUENCE");
        writeJdbcTemplate.update("TRUNCATE TABLE GPFD.CSV_TO_SQL_MAPPING_TABLE");
    }

    @Test
    void shouldReturnDataFromDatabase() {
        List<Map<String, Object>> resultList =
            reportViewsDao.callDataBase("SELECT ID FROM GPFD.CSV_TO_SQL_MAPPING_TABLE");
        assertEquals(resultList.size(), 3);
    }

    @Test
    void shouldThrowExceptionWhereNoData() {
        assertThrows(DatabaseReadException.class,
                () -> reportViewsDao.callDataBase("SELECT ID FROM GPFD.CSV_TO_SQL_MAPPING_TABLE WHERE ID = 0"));
    }
}
