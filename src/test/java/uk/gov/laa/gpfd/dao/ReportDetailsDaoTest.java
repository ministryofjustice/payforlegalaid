package uk.gov.laa.gpfd.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.laa.gpfd.exception.DatabaseReadException;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;
import uk.gov.laa.gpfd.model.ReportDetails;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest // This uses the whole spring context, switch to @JdbcTest if you switch to an H2 DB
@ActiveProfiles("test")
public class ReportDetailsDaoTest extends BaseDaoTest{

    public static final int NUMBER_OF_REPORTS_IN_TEST_DATA = 5;
    @Autowired
    private ReportDetailsDao reportDetailsDao;

    UUID DEFAULT_REPORT_ID = UUID.fromString("b36f9bbb-1178-432c-8f99-8090e285f2d3");
    UUID NOTFOUND_REPORT_ID = UUID.fromString("b36f9bbb-0178-430c-8f09-8090e205f2d3");

    @Test
    void shouldReturnAllReportsInOrder() {
        List<ReportDetails> results = reportDetailsDao.fetchReportList();

        assertEquals(NUMBER_OF_REPORTS_IN_TEST_DATA, results.size());
        assertEquals("b36f9bbb-1178-432c-8f99-8090e285f2d3", results.get(0).getId().toString());
        assertEquals(30, results.get(0).getNumDaysToKeep());
        assertEquals("xlsx", results.get(0).getExtension());
        assertEquals("csv", results.get(2).getExtension());
    }

    @Test
    void shouldReturnSingleReportById() {
        ReportDetails report = reportDetailsDao.fetchReport(DEFAULT_REPORT_ID);
        assertEquals("b36f9bbb-1178-432c-8f99-8090e285f2d3", report.getId().toString());

    }

    @Test
    void shouldThrowDataAccessExceptionWhenQueryForListFailsForAllReports() {
        resetDatabase();

        assertThrows(DatabaseReadException.class,
                () -> reportDetailsDao.fetchReportList());
    }

    @Test
    void shouldThrowDataAccessExceptionWhenQueryForListFailsForOneReport() {
        resetDatabase();

        assertThrows(DatabaseReadException.class,
                () -> reportDetailsDao.fetchReport(DEFAULT_REPORT_ID));
    }

    @Test
    void shouldThrowReportIdNotFoundExceptionWhenQueryForListReturnsEmptyListForAllReports() {

        clearReportsTable();

        assertThrows(ReportIdNotFoundException.class,
                () -> reportDetailsDao.fetchReportList());
    }

    @Test
    void shouldThrowReportIdNotFoundExceptionWhenQueryForListReturnsEmptyListForOneReport() {

        assertThrows(ReportIdNotFoundException.class,
                () -> reportDetailsDao.fetchReport(NOTFOUND_REPORT_ID));
    }

    public void clearReportsTable() {
        writeJdbcTemplate.update("DELETE FROM GPFD.REPORTS_TRACKING");
        writeJdbcTemplate.update("DELETE FROM GPFD.REPORTS");
    }


}
