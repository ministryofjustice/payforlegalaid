-- Repeatable Flyway migration for report metadata retained after MOJFIN retirement.

INSERT INTO glad.report_output_types (id, extension, description)
VALUES
    ('6ebd27ac-4d83-485d-a4fd-3e45f9a53484', 'csv', 'Comma Separated Text'),
    ('bd098666-94e4-4b0e-822c-8e5dfb04c908', 'xlsx', 'Excel Document'),
    ('523ed024-74f9-4288-9624-bbfeb04f45d0', 's3storage', 'Download from s3 Bucket')
ON CONFLICT (id) DO UPDATE SET
    extension = EXCLUDED.extension,
    description = EXCLUDED.description;

INSERT INTO glad.roles (role_id, role_name)
VALUES
    (1, 'Get legal aid data - REP000'),
    (2, 'Get legal aid data - Reconciliation'),
    (3, 'Get legal aid data - Financial')
ON CONFLICT (role_id) DO UPDATE SET
    role_name = EXCLUDED.role_name;

INSERT INTO glad.reports (id, description, name, file_name, template_secure_document_id, report_creation_date, num_days_to_keep, report_output_type, report_owner_id, report_owner_name, report_owner_email, active)
VALUES
    ('523f38f0-2179-4824-b885-3a38c5e149e8', 'Combined Data Extract for Submit a Bulk Claim Data', 'REP000 - Combined Data Extract for Submit a Bulk Claim Data', 'Bulk Claim Data', '00000000-0000-0000-0000-000000000000', CURRENT_DATE, 30, '523ed024-74f9-4288-9624-bbfeb04f45d0', '00000000-0000-0000-0000-000000000003', 'William Moran', 'William.Moran@Justice.gov.uk', 'Y'),
    ('cc55e276-97b0-4dd8-a919-26d4aa373266', 'Original Submissions Value Report', 'REP012 - Original Submissions Value Report', 'Original Submissions Value Report', '00000000-0000-0000-0000-000000000000', CURRENT_DATE, 30, '523ed024-74f9-4288-9624-bbfeb04f45d0', '00000000-0000-0000-0000-000000000003', 'Nigel Howell', 'Nigel.Howell@Justice.gov.uk', 'Y'),
    ('aca2120c-8f82-45a8-a682-8dedfb7997a7', 'Current Submissions Value Report', 'REP013 - Current Submissions Value Report', 'Current Submissions Value Report', '00000000-0000-0000-0000-000000000000', CURRENT_DATE, 30, '523ed024-74f9-4288-9624-bbfeb04f45d0', '00000000-0000-0000-0000-000000000003', 'Nigel Howell', 'Nigel.Howell@Justice.gov.uk', 'Y'),
    ('55daf3c1-28f0-4260-9396-2ee6d537abab', 'Claim Amendments Information Report', 'REP014 - Claim Amendments Information Report', 'REP014', '00000000-0000-0000-0000-000000000000', CURRENT_DATE, 30, '523ed024-74f9-4288-9624-bbfeb04f45d0', '00000000-0000-0000-0000-000000000003', 'Nigel Howell', 'Nigel.Howell@Justice.gov.uk', 'Y'),
    ('c4ba2e89-c106-48a7-8e1d-7c19dbd7710d', 'New Matter Starts data extract for Submit a Bulk Claim Data', 'REP002 - New Matter Starts Data Extract for Submit a Bulk Claim Data', 'New Matter Starts', '00000000-0000-0000-0000-000000000000', CURRENT_DATE, 30, '523ed024-74f9-4288-9624-bbfeb04f45d0', '00000000-0000-0000-0000-000000000003', 'William Moran', 'William.Moran@Justice.gov.uk', 'Y')
ON CONFLICT (id) DO UPDATE SET
    description = EXCLUDED.description,
    name = EXCLUDED.name,
    file_name = EXCLUDED.file_name,
    template_secure_document_id = EXCLUDED.template_secure_document_id,
    num_days_to_keep = EXCLUDED.num_days_to_keep,
    report_output_type = EXCLUDED.report_output_type,
    report_owner_id = EXCLUDED.report_owner_id,
    report_owner_name = EXCLUDED.report_owner_name,
    report_owner_email = EXCLUDED.report_owner_email,
    active = EXCLUDED.active;

INSERT INTO glad.report_roles (report_id, role_id)
VALUES
    ('523f38f0-2179-4824-b885-3a38c5e149e8', 1),
    ('cc55e276-97b0-4dd8-a919-26d4aa373266', 2),
    ('aca2120c-8f82-45a8-a682-8dedfb7997a7', 2),
    ('55daf3c1-28f0-4260-9396-2ee6d537abab', 2),
    ('c4ba2e89-c106-48a7-8e1d-7c19dbd7710d', 2)
ON CONFLICT (report_id, role_id) DO NOTHING;
