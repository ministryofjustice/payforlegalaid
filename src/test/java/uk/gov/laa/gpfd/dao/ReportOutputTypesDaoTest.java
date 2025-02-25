package uk.gov.laa.gpfd.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.laa.gpfd.model.ReportOutputType;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.laa.gpfd.dao.DaoUtils.initialiseDatabase;
import static uk.gov.laa.gpfd.dao.DaoUtils.clearDatabase;

@SpringBootTest // This uses the whole spring context, switch to @JdbcTest if you switch to a H2 DB
@ActiveProfiles("test")
public class ReportOutputTypesDaoTest {
    @Autowired
    private JdbcTemplate readOnlyJdbcTemplate;

    @Autowired
    private ReportOutputTypesDao reportOutputTypesDao;

    @BeforeEach
    void setup() {
        initialiseDatabase(readOnlyJdbcTemplate);
    }

    @AfterEach
    void resetDatabase() {
        clearDatabase(readOnlyJdbcTemplate);
    }

    @Test
    void shouldReturnAllReportTypesInOrder() {
        List<ReportOutputType> results = reportOutputTypesDao.fetchReportOutputTypes();

        assertEquals(2, results.size());
        assertEquals("6ebd27ac-4d83-485d-a4fd-3e45f9a53484", results.get(0).getId().toString());
        assertEquals("xlsx", results.get(1).getExtension());
    }

}
