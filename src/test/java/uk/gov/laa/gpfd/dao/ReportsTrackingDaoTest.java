package uk.gov.laa.gpfd.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest // This uses the whole spring context, switch to @JdbcTest if you switch to a H2 DB
@ActiveProfiles("test")
public class ReportsTrackingDaoTest {
    @Autowired
    private JdbcTemplate writeJdbcTemplate;

    @Autowired
    private ReportsTrackingDao reportsTrackingDao;
}
