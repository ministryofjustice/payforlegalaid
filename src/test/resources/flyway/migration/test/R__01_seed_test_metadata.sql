-- Repeatable test metadata seed generated from src/main/resources/db/changelog.
-- Do not use prod Flyway R__01 or V2 migrations in tests.

-- REPORT_OUTPUT_TYPES
INSERT INTO glad.report_output_types (id, extension, description) VALUES ('6ebd27ac-4d83-485d-a4fd-3e45f9a53484'::uuid, 'csv', 'Comma Separated Text') ON CONFLICT DO NOTHING;
INSERT INTO glad.report_output_types (id, extension, description) VALUES ('bd098666-94e4-4b0e-822c-8e5dfb04c908'::uuid, 'xlsx', 'Excel Document') ON CONFLICT DO NOTHING;
INSERT INTO glad.report_output_types (id, extension, description) VALUES ('523ed024-74f9-4288-9624-bbfeb04f45d0'::uuid, 's3storage', 'Download from s3 Bucket') ON CONFLICT DO NOTHING;

-- ROLES
INSERT INTO glad.roles (role_id, role_name) VALUES ('1', 'Get legal aid data - REP000') ON CONFLICT DO NOTHING;
INSERT INTO glad.roles (role_id, role_name) VALUES ('2', 'Get legal aid data - Reconciliation') ON CONFLICT DO NOTHING;

-- REPORTS
INSERT INTO glad.reports (id, name, template_secure_document_id, report_creation_date, description, num_days_to_keep, file_name, active, report_output_type, report_owner_id, report_owner_name, report_owner_email) VALUES ('0fbec75b-2d72-44f5-a0e3-2dcb29d92f79'::uuid, 'acceptance_test_table', '00000000-0000-0000-0000-000000000000'::uuid, DATE '2025-02-15', 'acceptance_test_table', '30', 'acceptance_test_table', 'Y', '6ebd27ac-4d83-485d-a4fd-3e45f9a53484'::uuid, '00000000-0000-0000-0000-000000000003'::uuid, 'Teresa Green', 'teresagreen@example.org') ON CONFLICT DO NOTHING;
INSERT INTO glad.reports (id, name, template_secure_document_id, report_creation_date, description, num_days_to_keep, file_name, active, report_output_type, report_owner_id, report_owner_name, report_owner_email) VALUES ('abbec75b-2d72-44f5-a0e3-2dcb29d92f79'::uuid, 'acceptance_test_table', '00000000-0000-0000-0000-000000000000'::uuid, DATE '2025-02-15', 'acceptance_test_table', '30', 'acceptance_test_table', 'N', '6ebd27ac-4d83-485d-a4fd-3e45f9a53484'::uuid, '00000000-0000-0000-0000-000000000003'::uuid, 'Teresa Green', 'teresagreen@example.org') ON CONFLICT DO NOTHING;
INSERT INTO glad.reports (id, name, template_secure_document_id, report_creation_date, description, num_days_to_keep, file_name, active, report_output_type, report_owner_id, report_owner_name, report_owner_email) VALUES ('cc55e276-97b0-4dd8-a919-26d4aa373266'::uuid, 'REP012 - Original Submissions Value Report', '00000000-0000-0000-0000-000000000000'::uuid, CURRENT_DATE, 'Original Submissions Value Report', '30', 'Original Submissions Value Report', 'Y', '523ed024-74f9-4288-9624-bbfeb04f45d0'::uuid, '00000000-0000-0000-0000-000000000003'::uuid, 'Blah blah', 'email@email.com') ON CONFLICT DO NOTHING;
INSERT INTO glad.reports (id, name, template_secure_document_id, report_creation_date, description, num_days_to_keep, file_name, active, report_output_type, report_owner_id, report_owner_name, report_owner_email) VALUES ('523f38f0-2179-4824-b885-3a38c5e149e8'::uuid, 'REP000 - Combined Data Extract for Submit a Bulk Claim Data', '00000000-0000-0000-0000-000000000000'::uuid, CURRENT_DATE, 'Combined Data Extract for Submit a Bulk Claim Data', '30', 'Bulk Claim Data', 'Y', '523ed024-74f9-4288-9624-bbfeb04f45d0'::uuid, '00000000-0000-0000-0000-000000000003'::uuid, 'Stevie Steve', 'email@email.com') ON CONFLICT DO NOTHING;
INSERT INTO glad.reports (id, name, template_secure_document_id, report_creation_date, description, num_days_to_keep, file_name, active, report_output_type, report_owner_id, report_owner_name, report_owner_email) VALUES ('c4ba2e89-c106-48a7-8e1d-7c19dbd7710d'::uuid, 'REP002 - New Matter Starts Data Extract for Submit a Bulk Claim Data', '00000000-0000-0000-0000-000000000000'::uuid, CURRENT_DATE, 'New Matter Starts data extract for Submit a Bulk Claim Data', '30', 'New Matter Starts', 'Y', '6ebd27ac-4d83-485d-a4fd-3e45f9a53484'::uuid, '00000000-0000-0000-0000-000000000003'::uuid, 'Stevie Steve', 'email@email.com') ON CONFLICT DO NOTHING;

-- REPORT_QUERIES
INSERT INTO glad.report_queries (id, report_id, query, tab_name) VALUES ('069bd36f-b3e5-474d-9408-75b8de56de03'::uuid, '0fbec75b-2d72-44f5-a0e3-2dcb29d92f79'::uuid, 'SELECT * FROM ANY_REPORT.MARSHMALLOW_DENSITY_SUMMARY_VIEW', 'MAIN') ON CONFLICT DO NOTHING;
INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index") VALUES ('1dc32729-f50d-418e-a2af-ad83d9248cc1'::uuid, 'abbec75b-2d72-44f5-a0e3-2dcb29d92f79'::uuid, 'SELECT * FROM ANY_REPORT.V_VIEW_THAT_DOESNT_EXIST', 'RESULTS', '0') ON CONFLICT DO NOTHING;

-- FIELD_ATTRIBUTES

-- REPORT_ROLES
INSERT INTO glad.report_roles (report_id, role_id) VALUES ('cc55e276-97b0-4dd8-a919-26d4aa373266'::uuid, '2') ON CONFLICT DO NOTHING;
INSERT INTO glad.report_roles (report_id, role_id) VALUES ('523f38f0-2179-4824-b885-3a38c5e149e8'::uuid, '1') ON CONFLICT DO NOTHING;
INSERT INTO glad.report_roles (report_id, role_id) VALUES ('c4ba2e89-c106-48a7-8e1d-7c19dbd7710d'::uuid, '1') ON CONFLICT DO NOTHING;
