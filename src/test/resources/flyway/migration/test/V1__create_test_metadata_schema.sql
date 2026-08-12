-- Test metadata schema derived from db/changelog Liquibase definitions.
CREATE SCHEMA IF NOT EXISTS glad;

CREATE TABLE IF NOT EXISTS glad.report_output_types (
  id UUID NOT NULL,
  extension VARCHAR(20) NOT NULL,
  description VARCHAR(150) NOT NULL,
  CONSTRAINT pk_report_output_types PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS glad.roles (
  role_id INT NOT NULL,
  role_name VARCHAR(200) NOT NULL,
  CONSTRAINT pk_roles PRIMARY KEY (role_id),
  CONSTRAINT uq_roles_name UNIQUE (role_name)
);

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
  CONSTRAINT pk_reports PRIMARY KEY (id),
  CONSTRAINT fk_reports_output_type FOREIGN KEY (report_output_type)
    REFERENCES glad.report_output_types (id)
);

CREATE TABLE IF NOT EXISTS glad.report_queries (
  id UUID NOT NULL,
  report_id UUID NOT NULL,
  query TEXT,
  tab_name VARCHAR(100) NOT NULL,
  "index" VARCHAR(100),
  CONSTRAINT pk_report_queries PRIMARY KEY (id),
  CONSTRAINT fk_report_queries_report FOREIGN KEY (report_id)
    REFERENCES glad.reports (id)
);

CREATE TABLE IF NOT EXISTS glad.field_attributes (
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
    REFERENCES glad.report_queries (id)
);

CREATE TABLE IF NOT EXISTS glad.report_roles (
  report_id UUID NOT NULL,
  role_id INT NOT NULL,
  CONSTRAINT pk_report_roles PRIMARY KEY (report_id, role_id),
  CONSTRAINT fk_report_roles_report FOREIGN KEY (report_id)
    REFERENCES glad.reports (id),
  CONSTRAINT fk_report_roles_role FOREIGN KEY (role_id)
    REFERENCES glad.roles (role_id)
);

CREATE INDEX IF NOT EXISTS idx_report_roles_report_id ON glad.report_roles (report_id);
CREATE INDEX IF NOT EXISTS idx_report_roles_role_id ON glad.report_roles (role_id);
CREATE INDEX IF NOT EXISTS idx_report_queries_report_id ON glad.report_queries (report_id);
CREATE INDEX IF NOT EXISTS idx_field_attributes_query_id ON glad.field_attributes (report_query_id);
