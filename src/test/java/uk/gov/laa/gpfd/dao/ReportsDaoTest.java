package uk.gov.laa.gpfd.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.laa.gpfd.model.Report;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.laa.gpfd.dao.DaoUtils.clearDatabase;
import static uk.gov.laa.gpfd.dao.DaoUtils.initialiseDatabase;

@SpringBootTest // This uses the whole spring context, switch to @JdbcTest if you switch to a H2 DB
@ActiveProfiles("test")
public class ReportsDaoTest {
    @Autowired
    private JdbcTemplate readOnlyJdbcTemplate;

    @Autowired
    private ReportsDao reportsDao;

    UUID DEFAULT_REPORT_ID = UUID.fromString("b36f9bbb-1178-432c-8f99-8090e285f2d3");

    @BeforeEach
    void setup() {
        initialiseDatabase(readOnlyJdbcTemplate);
    }

    @AfterEach
    void resetDatabase() {
        clearDatabase(readOnlyJdbcTemplate);
    }

    @Test
    void shouldReturnAllReportsInOrder() {
        List<Report> results = reportsDao.fetchReportList();

        assertEquals(3, results.size());
        assertEquals("b36f9bbb-1178-432c-8f99-8090e285f2d3", results.get(0).getId().toString());
        assertEquals(30, results.get(0).getNumDaysToKeep());
        assertEquals("xlsx", results.get(0).getExtension());
        assertEquals("csv", results.get(2).getExtension());
    }

    @Test
    void shouldReturnSingleReport() {
        Report report = reportsDao.fetchReport(DEFAULT_REPORT_ID);
        assertEquals("b36f9bbb-1178-432c-8f99-8090e285f2d3", report.getId().toString());

    }
}
