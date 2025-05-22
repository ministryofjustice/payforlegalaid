package uk.gov.laa.gpfd.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.laa.gpfd.exception.DatabaseReadException.DatabaseFetchException;

@SpringBootTest
@ActiveProfiles("test")
class ReportViewsDaoTest extends BaseDaoTest {

    @Autowired
    private ReportViewsDao reportViewsDao;

    @Test
    void shouldReturnDataFromDatabase() {
        var resultList = reportViewsDao.callDataBase("SELECT ID FROM GPFD.CSV_TO_SQL_MAPPING_TABLE");
        assertEquals(3, resultList.size() );
    }

    @Test
    void shouldThrowExceptionWhereNoData() {
        assertThrows(DatabaseFetchException.class,
                () -> reportViewsDao.callDataBase("SELECT ID FROM GPFD.CSV_TO_SQL_MAPPING_TABLE WHERE ID = 0"));
    }
}
