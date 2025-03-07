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

    public static void dropDatabaseTables(JdbcTemplate jdbcTemplate) {

        jdbcTemplate.update("DROP TABLE IF EXISTS GPFD.REPORTS_TRACKING");
        jdbcTemplate.update("DROP TABLE IF EXISTS GPFD.FIELD_ATTRIBUTES");
        jdbcTemplate.update("DROP TABLE IF EXISTS GPFD.REPORT_QUERIES");
        jdbcTemplate.update("DROP TABLE IF EXISTS GPFD.REPORT_GROUPS");
        jdbcTemplate.update("DROP TABLE IF EXISTS GPFD.REPORTS");
        jdbcTemplate.update("DROP TABLE IF EXISTS GPFD.REPORT_OUTPUT_TYPES");
    }

    public static void clearReportsTable(JdbcTemplate jdbcTemplate) {

        jdbcTemplate.update("ALTER TABLE GPFD.REPORTS DROP CONSTRAINT fk_report_output_types_report_id");
        jdbcTemplate.update("ALTER TABLE GPFD.REPORT_GROUPS DROP CONSTRAINT fk_report_groups_report_id");
        jdbcTemplate.update("ALTER TABLE GPFD.REPORT_QUERIES DROP CONSTRAINT fk_report_queries_report_id");
        jdbcTemplate.update("ALTER TABLE GPFD.FIELD_ATTRIBUTES DROP CONSTRAINT fk_field_attributes_report_query_id");
        jdbcTemplate.update("ALTER TABLE GPFD.REPORTS_TRACKING DROP CONSTRAINT fk_reports_tracking_reports_id");
        jdbcTemplate.update("TRUNCATE TABLE GPFD.REPORTS");
    }

}
