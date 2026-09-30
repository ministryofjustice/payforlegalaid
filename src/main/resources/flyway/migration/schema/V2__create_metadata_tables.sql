-- =============================================================
-- V2__create_metadata_tables.sql
-- Versioned migration: creates GPFD report metadata tables in tracking RDS.
--
-- This replaces the previous repeatable DDL approach to avoid accidental
-- drop/recreate on checksum changes in live environments.
-- =============================================================

-- report_output_types
-- Describes the output format of a report (csv, xlsx, s3storage).
-- Referenced by reports.report_output_type.
CREATE TABLE IF NOT EXISTS report_output_types (
  id UUID NOT NULL,
  extension VARCHAR(20) NOT NULL,
  description VARCHAR(150) NOT NULL,
  CONSTRAINT pk_report_output_types PRIMARY KEY (id)
);

-- reports
-- One row per report definition. report_output_type is a FK to report_output_types.
CREATE TABLE IF NOT EXISTS reports (
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
  CONSTRAINT pk_reports PRIMARY KEY (id),
  CONSTRAINT fk_reports_output_type FOREIGN KEY (report_output_type)
    REFERENCES report_output_types (id)
);

-- report_queries
-- SQL queries associated with a report. One report can have multiple queries.
CREATE TABLE IF NOT EXISTS report_queries (
  id UUID NOT NULL,
  report_id UUID NOT NULL,
  query TEXT,
  tab_name VARCHAR(100) NOT NULL,
  "index" VARCHAR(100),
  CONSTRAINT pk_report_queries PRIMARY KEY (id),
  CONSTRAINT fk_report_queries_report FOREIGN KEY (report_id)
    REFERENCES reports (id)
);

-- field_attributes
-- Column-level metadata for a report query.
CREATE TABLE IF NOT EXISTS field_attributes (
  id UUID NOT NULL,
  report_query_id UUID NOT NULL,
  source_name VARCHAR(100) NOT NULL,
  mapped_name VARCHAR(100) NOT NULL,
  format VARCHAR(100),
  format_type VARCHAR(100),
  column_width NUMERIC(6, 2) NOT NULL,
  column_order INT,
  CONSTRAINT pk_field_attributes PRIMARY KEY (id),
  CONSTRAINT fk_field_attributes_query FOREIGN KEY (report_query_id)
    REFERENCES report_queries (id)
);

-- roles
-- Application roles (e.g. REP000, Financial, Reconciliation).
CREATE TABLE IF NOT EXISTS roles (
  role_id INT NOT NULL,
  role_name VARCHAR(200) NOT NULL,
  CONSTRAINT pk_roles PRIMARY KEY (role_id),
  CONSTRAINT uq_roles_name UNIQUE (role_name)
);

-- report_roles
-- Join table: which roles are allowed to access which reports.
CREATE TABLE IF NOT EXISTS report_roles (
  report_id UUID NOT NULL,
  role_id INT NOT NULL,
  CONSTRAINT pk_report_roles PRIMARY KEY (report_id, role_id),
  CONSTRAINT fk_report_roles_report FOREIGN KEY (report_id)
    REFERENCES reports (id),
  CONSTRAINT fk_report_roles_role FOREIGN KEY (role_id)
    REFERENCES roles (role_id)
);

-- Indexes to support the INNER JOINs used in ReportDao
CREATE INDEX IF NOT EXISTS idx_report_roles_report_id ON report_roles (report_id);
CREATE INDEX IF NOT EXISTS idx_report_roles_role_id ON report_roles (role_id);
CREATE INDEX IF NOT EXISTS idx_report_queries_report_id ON report_queries (report_id);
CREATE INDEX IF NOT EXISTS idx_field_attributes_query_id ON field_attributes (report_query_id);