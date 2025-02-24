package uk.gov.laa.gpfd.dao;

import org.modelmapper.ModelMapper;
import org.springframework.jdbc.core.JdbcTemplate;

public class ReportsDao {
    private static final String SELECT_SINGLE_REPORT_SQL = "SELECT * FROM GPFD.REPORTS WHERE ID = ?";
    private static final String SELECT_REPORTS_BY_TYPE_SQL = "SELECT * FROM GPFD.V_REPORTS_BY_TYPE WHERE EXTENSION = ?";

    private final JdbcTemplate readOnlyJdbcTemplate;
    private final ModelMapper modelMapper;

}
