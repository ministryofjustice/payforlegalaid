package uk.gov.laa.gpfd.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import uk.gov.laa.gpfd.utils.FileUtils;

public class DaoUtils {
    public static void initialiseDatabase(JdbcTemplate jdbcTemplate) {
        String sqlSchema = FileUtils.readResourceToString("gpfd_reports_schema.sql");
        String sqlData = FileUtils.readResourceToString("gpfd_reports_data.sql");

        jdbcTemplate.execute(sqlSchema);
        jdbcTemplate.execute(sqlData);

    }

    public static void clearDatabase(JdbcTemplate jdbcTemplate) {

        jdbcTemplate.update("DROP TABLE GPFD.REPORTS_TRACKING");
        jdbcTemplate.update("DROP TABLE GPFD.FIELD_ATTRIBUTES");
        jdbcTemplate.update("DROP TABLE GPFD.REPORT_QUERIES");
        jdbcTemplate.update("DROP TABLE GPFD.REPORT_GROUPS");
        jdbcTemplate.update("DROP TABLE GPFD.REPORTS");
        jdbcTemplate.update("DROP TABLE GPFD.REPORT_OUTPUT_TYPES");
    }
}
