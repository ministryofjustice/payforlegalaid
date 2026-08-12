package uk.gov.laa.gpfd.integration;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.gov.laa.gpfd.integration.verifier.DatabaseVerifier;
import uk.gov.laa.gpfd.integration.verifier.DatabaseVerifier.Table;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

final class GetReportsIT extends BaseIT {

    @Autowired
    @Qualifier("metadataJdbcTemplate")
    private JdbcTemplate metadataJdbcTemplate;

    @Test
    @SneakyThrows
    void shouldSuccessfullyReturnAllAvailableReports() {
        // Setup metadata tables if they don't exist
        setupMetadataTables();
        
        var reportsLen = metadataJdbcTemplate.queryForObject("SELECT COUNT(*) FROM glad.reports", Integer.class);

        performGetRequestWithRoles("/reports", List.of("REP000", "Financial", "Reconciliation"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.reportList").isArray())
                .andExpect(jsonPath("$.reportList.length()").value(reportsLen));
    }

    @Test
    @SneakyThrows
    void shouldSuccessfullyReturn200WhenNoReportsFound() {
        // Setup metadata tables if they don't exist
        setupMetadataTables();
        
        performGetRequestWithRoles("/reports", List.of("not-a-report-role"))
                .andExpect(status().isOk())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.reportList").isEmpty());
    }

    private void setupMetadataTables() {
        try {
            // Create glad schema
            metadataJdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS glad");
            
            // Create metadata tables in glad schema
            metadataJdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS glad.report_output_types (
                  id UUID NOT NULL,
                  extension VARCHAR(20) NOT NULL,
                  description VARCHAR(150) NOT NULL,
                  CONSTRAINT pk_report_output_types PRIMARY KEY (id)
                )
            """);
            
            metadataJdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS glad.roles (
                  role_id INT NOT NULL,
                  role_name VARCHAR(200) NOT NULL,
                  CONSTRAINT pk_roles PRIMARY KEY (role_id),
                  CONSTRAINT uq_roles_name UNIQUE (role_name)
                )
            """);
            
            metadataJdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS glad.reports (
                  id UUID NOT NULL,
                  name VARCHAR(150) NOT NULL,
                  file_name VARCHAR(150),
                  template_secure_document_id VARCHAR(300),
                  report_creation_date DATE,
                  last_database_refresh_datetime TIMESTAMP,
                  description VARCHAR(4000),
                  num_days_to_keep INT,
                  report_output_type UUID NOT NULL,
                  report_owner_id UUID NOT NULL,
                  report_owner_name VARCHAR(150) NOT NULL,
                  report_owner_email VARCHAR(150) NOT NULL,
                  active VARCHAR(1),
                  CONSTRAINT pk_reports PRIMARY KEY (id)
                )
            """);
            
            metadataJdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS glad.report_queries (
                  id UUID NOT NULL,
                  report_id UUID NOT NULL,
                  query TEXT,
                  tab_name VARCHAR(100) NOT NULL,
                  "index" VARCHAR(100),
                  CONSTRAINT pk_report_queries PRIMARY KEY (id)
                )
            """);
            
            metadataJdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS glad.field_attributes (
                  id UUID NOT NULL,
                  report_query_id UUID NOT NULL,
                  source_name VARCHAR(100) NOT NULL,
                  mapped_name VARCHAR(100) NOT NULL,
                  format VARCHAR(100),
                  format_type VARCHAR(100),
                  column_width NUMERIC(6, 2) NOT NULL,
                  column_order INT,
                  CONSTRAINT pk_field_attributes PRIMARY KEY (id)
                )
            """);
            
            metadataJdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS glad.report_roles (
                  report_id UUID NOT NULL,
                  role_id INT NOT NULL,
                  CONSTRAINT pk_report_roles PRIMARY KEY (report_id, role_id)
                )
            """);
            
            // Insert test data
            metadataJdbcTemplate.execute("""
                INSERT INTO glad.report_output_types (id, extension, description)
                VALUES 
                    ('6ebd27ac-4d83-485d-a4fd-3e45f9a53484', 'csv', 'Comma Separated Text'),
                    ('bd098666-94e4-4b0e-822c-8e5dfb04c908', 'xlsx', 'Excel Document')
                ON CONFLICT (id) DO NOTHING
            """);
            
            metadataJdbcTemplate.execute("""
                INSERT INTO glad.roles (role_id, role_name)
                VALUES 
                    (1, 'REP000'),
                    (2, 'Financial'),
                    (3, 'Reconciliation')
                ON CONFLICT (role_id) DO NOTHING
            """);
            
            metadataJdbcTemplate.execute("""
                INSERT INTO glad.reports (id, name, file_name, template_secure_document_id, 
                    report_creation_date, num_days_to_keep, report_output_type, 
                    report_owner_id, report_owner_name, report_owner_email, active)
                VALUES 
                    ('b36f9bbb-1178-432c-8f99-8090e285f2d3', 'CCMS Invoice Analysis (CIS to CCMS)', 
                    'CCMS_invoice_analysis', '7c2b9f4e-3a6d-4b8a-9f12-6e5d0c8a1b34', 
                    CURRENT_DATE, 30, 'bd098666-94e4-4b0e-822c-8e5dfb04c908', 
                    '00000000-0000-0000-0000-000000000001', 'Test Owner', 'test@example.com', 'Y')
                ON CONFLICT (id) DO NOTHING
            """);
            
            metadataJdbcTemplate.execute("""
                INSERT INTO glad.report_roles (report_id, role_id)
                VALUES ('b36f9bbb-1178-432c-8f99-8090e285f2d3', 2)
                ON CONFLICT (report_id, role_id) DO NOTHING
            """);
            
        } catch (Exception e) {
            System.err.println("Error setting up metadata tables: " + e.getMessage());
        }
    }

}
