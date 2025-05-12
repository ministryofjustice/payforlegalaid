package uk.gov.laa.gpfd.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.laa.gpfd.exception.DatabaseReadException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class ReportViewsDaoTest extends BaseDaoTest{

    @Autowired
    private ReportViewsDao reportViewsDao;

    @Test
    void shouldReturnDataFromDatabase() {
        List<Map<String, Object>> resultList =
            reportViewsDao.callDataBase("SELECT ID FROM GPFD.CSV_TO_SQL_MAPPING_TABLE");
        assertEquals(3, resultList.size());
    }

    @Test
    void shouldThrowExceptionWhereNoData() {
        assertThrows(DatabaseReadException.class,
                () -> reportViewsDao.callDataBase("SELECT ID FROM GPFD.CSV_TO_SQL_MAPPING_TABLE WHERE ID = 0"));
    }
}
