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
        jdbcTemplate.update("TRUNCATE TABLE GPFD.REPORTS");
        //jdbcTemplate.update("TRUNCATE TABLE GPFD.REPORT_OUTPUT_TYPES");
    }
}
