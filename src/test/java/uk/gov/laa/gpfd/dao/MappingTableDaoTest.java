package uk.gov.laa.gpfd.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;
import uk.gov.laa.gpfd.model.MappingTable;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest // This uses the whole spring context, switch to @JdbcTest if you switch to a H2 DB
@ActiveProfiles("test")
class MappingTableDaoTest extends BaseDaoTest {
    public static final UUID DEFAULT_ID = UUID.fromString("0d4da9ec-b0b3-4371-af10-f375330d85d1");

    @Autowired
    private MappingTableDao mappingTableDao;

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
