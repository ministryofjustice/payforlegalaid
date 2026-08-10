-- =============================================================
-- R__01_seed_metadata.sql
-- Repeatable migration: full reload of GPFD metadata seed data.
-- Re-runs whenever the checksum changes (e.g. new report added).
-- Source: payforlegalaid-data repo.
-- =============================================================

-- -----------------------------------------------------------
-- REPORT OUTPUT TYPES (3 rows)
-- -----------------------------------------------------------
INSERT INTO glad.report_output_types (id, extension, description)
VALUES ('6ebd27ac-4d83-485d-a4fd-3e45f9a53484', 'csv', 'Comma Separated Text')
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_output_types (id, extension, description)
VALUES ('bd098666-94e4-4b0e-822c-8e5dfb04c908', 'xlsx', 'Excel Document')
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_output_types (id, extension, description)
VALUES ('523ed024-74f9-4288-9624-bbfeb04f45d0', 's3storage', 'Download from s3 Bucket')
ON CONFLICT (id) DO NOTHING;

-- -----------------------------------------------------------
-- ROLES (3 rows)
-- -----------------------------------------------------------
INSERT INTO glad.roles (role_id, role_name)
VALUES (1, 'Get legal aid data - REP000')
ON CONFLICT (role_id) DO NOTHING;

INSERT INTO glad.roles (role_id, role_name)
VALUES (2, 'Get legal aid data - Reconciliation')
ON CONFLICT (role_id) DO NOTHING;

INSERT INTO glad.roles (role_id, role_name)
VALUES (3, 'Get legal aid data - Financial')
ON CONFLICT (role_id) DO NOTHING;

-- -----------------------------------------------------------
-- REPORTS (27 rows)
-- -----------------------------------------------------------
INSERT INTO glad.reports (id, description, name, file_name, template_secure_document_id, report_creation_date, num_days_to_keep, report_output_type, report_owner_id, report_owner_name, report_owner_email, active)
VALUES ('b36f9bbb-1178-432c-8f99-8090e285f2d3', 'Summary of invoices in CIS and CCMS by original source IT system', 'CCMS Invoice Analysis (CIS to CCMS)', 'CCMS_invoice_analysis', '7c2b9f4e-3a6d-4b8a-9f12-6e5d0c8a1b34', CURRENT_DATE, 30, 'bd098666-94e4-4b0e-822c-8e5dfb04c908', '00000000-0000-0000-0000-000000000001', 'Chancey Mctavish', 'owneremail@email.com', 'Y')
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.reports (id, description, name, file_name, template_secure_document_id, report_creation_date, num_days_to_keep, report_output_type, report_owner_id, report_owner_name, report_owner_email, active)
VALUES ('f46b4d3d-c100-429a-bf9a-6c3305dbdbf1', 'Summary of invoices in CIS and CCMS by original source IT system', 'CCMS Invoice Analysis (CIS to CCMS)', 'CCMS_invoice_analysis', '00000000-0000-0000-0000-000000000000', CURRENT_DATE, 30, '6ebd27ac-4d83-485d-a4fd-3e45f9a53484', '00000000-0000-0000-0000-000000000001', 'Chancey Mctavish', 'owneremail@email.com', 'Y')
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.reports (id, description, name, file_name, template_secure_document_id, report_creation_date, num_days_to_keep, report_output_type, report_owner_id, report_owner_name, report_owner_email, active)
VALUES ('f46b4d3d-c100-429a-bf9a-6c3305dbdbf2', 'Summary of all payments made by CCMS/CIS and all cash receipts applied to debt in the previous month', 'CCMS_and_CIS_Bank_Account_Report_w_Category_Code_(MNTH)-MAIN-12', 'CCMS_and_CIS_Bank_Account_Report_w_Category_Code_(MNTH)', '00000000-0000-0000-0000-000000000000', CURRENT_DATE, 30, '6ebd27ac-4d83-485d-a4fd-3e45f9a53484', '00000000-0000-0000-0000-000000000002', 'Daniel Mctavish', 'owneremail@email.com', 'Y')
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.reports (id, description, name, file_name, template_secure_document_id, report_creation_date, num_days_to_keep, report_output_type, report_owner_id, report_owner_name, report_owner_email, active)
VALUES ('f46b4d3d-c100-429a-bf9a-6c3305dbdbf3', 'Details of invoices transferred from CIS to CCMS by Legal Aid Scheme', 'CIS to CCMS import exceptions', 'CIS_TO_CCMS_IMPORT_EXCEPTIONS', '00000000-0000-0000-0000-000000000000', CURRENT_DATE, 30, '6ebd27ac-4d83-485d-a4fd-3e45f9a53484', '00000000-0000-0000-0000-000000000003', 'William Moran', 'William.Moran@Justice.gov.uk', 'Y')
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.reports (id, description, name, file_name, template_secure_document_id, report_creation_date, num_days_to_keep, report_output_type, report_owner_id, report_owner_name, report_owner_email, active)
VALUES ('f46b4d3d-c100-429a-bf9a-6c3305dbdbf4', 'Details of invoices transferred from CIS to CCMS by Legal Aid Scheme', 'CIS to CCMS payment value Defined', 'CIS_TO_CCMS_PAYMENT_VALUE_DEFINED', '00000000-0000-0000-0000-000000000000', CURRENT_DATE, 30, '6ebd27ac-4d83-485d-a4fd-3e45f9a53484', '00000000-0000-0000-0000-000000000003', 'William Moran', 'William.Moran@Justice.gov.uk', 'Y')
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.reports (id, description, name, file_name, template_secure_document_id, report_creation_date, num_days_to_keep, report_output_type, report_owner_id, report_owner_name, report_owner_email, active)
VALUES ('f46b4d3d-c100-429a-bf9a-6c3305dbdbf5', 'Details of invoices transferred from CIS to CCMS by Legal Aid Scheme', 'CIS to CCMS payment value Not Defined', 'CIS_TO_CCMS_PAYMENT_VALUE_NOT_DEFINED', '00000000-0000-0000-0000-000000000000', CURRENT_DATE, 30, '6ebd27ac-4d83-485d-a4fd-3e45f9a53484', '00000000-0000-0000-0000-000000000003', 'William Moran', 'William.Moran@Justice.gov.uk', 'Y')
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.reports (id, description, name, file_name, template_secure_document_id, report_creation_date, num_days_to_keep, report_output_type, report_owner_id, report_owner_name, report_owner_email, active)
VALUES ('f46b4d3d-c100-429a-bf9a-6c3305dbdbf6', 'Details of invoices transferred from CIS to CCMS by Legal Aid Scheme', 'CCMS Held Payments', 'CCMS_HELD_PAYMENTS', '00000000-0000-0000-0000-000000000000', CURRENT_DATE, 30, '6ebd27ac-4d83-485d-a4fd-3e45f9a53484', '00000000-0000-0000-0000-000000000003', 'William Moran', 'William.Moran@Justice.gov.uk', 'Y')
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.reports (id, description, name, file_name, template_secure_document_id, report_creation_date, num_days_to_keep, report_output_type, report_owner_id, report_owner_name, report_owner_email, active)
VALUES ('f46b4d3d-c100-429a-bf9a-6c3305dbdbf7', 'Details of invoices transferred from CIS to CCMS by Legal Aid Scheme', 'CCMS AP Debtors', 'CCMS_AP_DEBTORS', '00000000-0000-0000-0000-000000000000', CURRENT_DATE, 30, '6ebd27ac-4d83-485d-a4fd-3e45f9a53484', '00000000-0000-0000-0000-000000000003', 'William Moran', 'William.Moran@Justice.gov.uk', 'Y')
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.reports (id, description, name, file_name, template_secure_document_id, report_creation_date, num_days_to_keep, report_output_type, report_owner_id, report_owner_name, report_owner_email, active)
VALUES ('f46b4d3d-c100-429a-bf9a-6c3305dbdbf8', 'Summary of all payments made by CCMS/CIS and all cash receipts applied to debt YTD', 'CCMS_and_CIS_Bank_Account_Report_w_Category_Code_YTD', 'CCMS_and_CIS_Bank_Account_Report_w_Category_Code_YTD', '00000000-0000-0000-0000-000000000000', CURRENT_DATE, 30, '6ebd27ac-4d83-485d-a4fd-3e45f9a53484', '00000000-0000-0000-0000-000000000003', 'William Moran', 'William.Moran@Justice.gov.uk', 'Y')
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.reports (id, description, name, file_name, template_secure_document_id, report_creation_date, num_days_to_keep, report_output_type, report_owner_id, report_owner_name, report_owner_email, active)
VALUES ('f46b4d3d-c100-429a-bf9a-6c3305dbdbf9', 'CCMS General ledger extractor (tiny columns multiple periods)', 'CCMS General Ledger (tiny columns multiple periods)', 'CCMS_GEN_LEDGER_TINYCOL_MULPERIODS', '00000000-0000-0000-0000-000000000000', CURRENT_DATE, 30, '6ebd27ac-4d83-485d-a4fd-3e45f9a53484', '00000000-0000-0000-0000-000000000003', 'William Moran', 'William.Moran@Justice.gov.uk', 'Y')
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.reports (id, description, name, file_name, template_secure_document_id, report_creation_date, num_days_to_keep, report_output_type, report_owner_id, report_owner_name, report_owner_email, active)
VALUES ('f46b4d3d-c100-429a-bf9a-6c3305dbdbfa', 'CCMS General ledger extractor (tiny columns multiple periods manual)', 'CCMS General Ledger (tiny columns multiple periods manual)', 'CCMS_GEN_LEDGER_TINYCOL_MULPERIODS_MANUAL', '00000000-0000-0000-0000-000000000000', CURRENT_DATE, 30, '6ebd27ac-4d83-485d-a4fd-3e45f9a53484', '00000000-0000-0000-0000-000000000003', 'William Moran', 'William.Moran@Justice.gov.uk', 'Y')
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.reports (id, description, name, file_name, template_secure_document_id, report_creation_date, num_days_to_keep, report_output_type, report_owner_id, report_owner_name, report_owner_email, active)
VALUES ('f46b4d3d-c100-429a-bf9a-6c3305dbdbfb', 'CCMS Authorised expenditure (Contracting live system)', 'CCMS Authorised expenditure (Contracting live system)', 'CCMS_AUTH_EXPEND_CONTRACTING', '00000000-0000-0000-0000-000000000000', CURRENT_DATE, 30, '6ebd27ac-4d83-485d-a4fd-3e45f9a53484', '00000000-0000-0000-0000-000000000003', 'William Moran', 'William.Moran@Justice.gov.uk', 'Y')
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.reports (id, description, name, file_name, template_secure_document_id, report_creation_date, num_days_to_keep, report_output_type, report_owner_id, report_owner_name, report_owner_email, active)
VALUES ('f46b4d3d-c100-429a-bf9a-223305dbdbfb', 'CCMS General ledger extractor (small manual batches)', 'CCMS General ledger extractor (small manual batches)', 'CCMS_GENERAL_LEDGER_EXTRACTOR_SMALL_MANUAL_BATCHES', 'f46b4d3d-c100-429a-bf9a-223305dbdbfb', CURRENT_DATE, 30, 'bd098666-94e4-4b0e-822c-8e5dfb04c908', '00000000-0000-0000-0000-000000000003', 'William Moran', 'William.Moran@Justice.gov.uk', 'Y')
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.reports (id, description, name, file_name, template_secure_document_id, report_creation_date, num_days_to_keep, report_output_type, report_owner_id, report_owner_name, report_owner_email, active)
VALUES ('eee30b23-2c8d-4b4b-bb11-8cd67d07915c', 'CCMS and CIS Bank Account Report w Category Code (YTD)', 'CCMS and CIS Bank Account Report w Category Code (YTD)', 'CCMS_AND_CIS_BANK_ACCOUNT_REPORT_W_CATEGORY_CODE_YTD', 'eee30b23-2c8d-4b4b-bb11-8cd67d07915c', CURRENT_DATE, 30, 'bd098666-94e4-4b0e-822c-8e5dfb04c908', '00000000-0000-0000-0000-000000000003', 'William Moran', 'William.Moran@Justice.gov.uk', 'Y')
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.reports (id, description, name, file_name, template_secure_document_id, report_creation_date, num_days_to_keep, report_output_type, report_owner_id, report_owner_name, report_owner_email, active)
VALUES ('a017241a-359f-4fdb-a0cd-7f28f1946ef1', 'CCMS and CIS Bank Account Report w Category Code (MNTH)', 'CCMS and CIS Bank Account Report w Category Code (MNTH)', 'CCMS_AND_CIS_BANK_ACCOUNT_REPORT_W_CATEGORY_CODE_YTD', 'a017241a-359f-4fdb-a0cd-7f28f1946ef1', CURRENT_DATE, 30, 'bd098666-94e4-4b0e-822c-8e5dfb04c908', '00000000-0000-0000-0000-000000000003', 'William Moran', 'William.Moran@Justice.gov.uk', 'Y')
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.reports (id, description, name, file_name, template_secure_document_id, report_creation_date, num_days_to_keep, report_output_type, report_owner_id, report_owner_name, report_owner_email, active)
VALUES ('7073dd13-e325-4863-a05c-a049a815d1f7', 'Legal Help contract balances', 'Legal Help contract balances', 'LEGAL_HELP_CONTRACT_BALANCES', '7073dd13-e325-4863-a05c-a049a815d1f7', CURRENT_DATE, 30, 'bd098666-94e4-4b0e-822c-8e5dfb04c908', '00000000-0000-0000-0000-000000000003', 'William Moran', 'William.Moran@Justice.gov.uk', 'Y')
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.reports (id, description, name, file_name, template_secure_document_id, report_creation_date, num_days_to_keep, report_output_type, report_owner_id, report_owner_name, report_owner_email, active)
VALUES ('e6823193-f5b0-451b-8965-e4d4914980da', 'Lower Crime contract balances', 'Lower Crime contract balances', 'LEGAL_HELP_CONTRACT_BALANCES', '7073dd13-e325-4863-a05c-a049a815d1f7', CURRENT_DATE, 30, 'bd098666-94e4-4b0e-822c-8e5dfb04c908', '00000000-0000-0000-0000-000000000003', 'William Moran', 'William.Moran@Justice.gov.uk', 'Y')
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.reports (id, description, name, file_name, template_secure_document_id, report_creation_date, num_days_to_keep, report_output_type, report_owner_id, report_owner_name, report_owner_email, active)
VALUES ('56328b13-254d-435d-813a-5863f94b996d', 'Mediation contract balances', 'Mediation contract balances', 'MEDIATION_CONTRACT_BALANCES_TEMPLATE', 'b380e788-2096-46dc-b58a-21bf771669dc', CURRENT_DATE, 30, 'bd098666-94e4-4b0e-822c-8e5dfb04c908', '00000000-0000-0000-0000-000000000003', 'William Moran', 'William.Moran@Justice.gov.uk', 'Y')
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.reports (id, description, name, file_name, template_secure_document_id, report_creation_date, num_days_to_keep, report_output_type, report_owner_id, report_owner_name, report_owner_email, active)
VALUES ('7bda9aa4-6129-4c71-bd12-7d4e46fdd882', 'AGFS late processed bills', 'AGFS late processed bills', 'LATE_PROCESSED_BILLS', '7bda9aa4-6129-4c71-bd12-7d4e46fdd882', CURRENT_DATE, 30, 'bd098666-94e4-4b0e-822c-8e5dfb04c908', '00000000-0000-0000-0000-000000000003', 'William Moran', 'William.Moran@Justice.gov.uk', 'N')
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.reports (id, description, name, file_name, template_secure_document_id, report_creation_date, num_days_to_keep, report_output_type, report_owner_id, report_owner_name, report_owner_email, active)
VALUES ('516cdbff-5fa8-4050-b5e6-7edf71daf679', 'CCLF late processed bills', 'CCLF late processed bills', 'LATE_PROCESSED_BILLS', '7bda9aa4-6129-4c71-bd12-7d4e46fdd882', CURRENT_DATE, 30, 'bd098666-94e4-4b0e-822c-8e5dfb04c908', '00000000-0000-0000-0000-000000000003', 'William Moran', 'William.Moran@Justice.gov.uk', 'N')
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.reports (id, description, name, file_name, template_secure_document_id, report_creation_date, num_days_to_keep, report_output_type, report_owner_id, report_owner_name, report_owner_email, active)
VALUES ('8b9f0484-819f-4e0f-b60a-0b3f9d30d9ba', 'CCMS Third party report', 'CCMS Third party report', 'CCMS_THIRD_PARTY_REPORT', '8b9f0484-819f-4e0f-b60a-0b3f9d30d9ba', CURRENT_DATE, 30, 'bd098666-94e4-4b0e-822c-8e5dfb04c908', '00000000-0000-0000-0000-000000000003', 'William Moran', 'William.Moran@Justice.gov.uk', 'N')
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.reports (id, description, name, file_name, template_secure_document_id, report_creation_date, num_days_to_keep, report_output_type, report_owner_id, report_owner_name, report_owner_email, active)
VALUES ('90af8289-2c07-4b65-8f37-6b4659920207', 'C12 late processed bills - CIS (80%)', 'C12 late processed bills - CIS (80%)', 'C12_LATE_PROCESSED_BILLS_CIS', '22fe2b17-eea8-4c74-929d-9c69503f25d3', CURRENT_DATE, 30, 'bd098666-94e4-4b0e-822c-8e5dfb04c908', '00000000-0000-0000-0000-000000000003', 'William Moran', 'William.Moran@Justice.gov.uk', 'N')
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.reports (id, description, name, file_name, template_secure_document_id, report_creation_date, num_days_to_keep, report_output_type, report_owner_id, report_owner_name, report_owner_email, active)
VALUES ('523f38f0-2179-4824-b885-3a38c5e149e8', 'Combined Data Extract for Submit a Bulk Claim Data', 'REP000 - Combined Data Extract for Submit a Bulk Claim Data', 'Bulk Claim Data', '00000000-0000-0000-0000-000000000000', CURRENT_DATE, 30, '523ed024-74f9-4288-9624-bbfeb04f45d0', '00000000-0000-0000-0000-000000000003', 'William Moran', 'William.Moran@Justice.gov.uk', 'Y')
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.reports (id, description, name, file_name, template_secure_document_id, report_creation_date, num_days_to_keep, report_output_type, report_owner_id, report_owner_name, report_owner_email, active)
VALUES ('cc55e276-97b0-4dd8-a919-26d4aa373266', 'Original Submissions Value Report', 'REP012 - Original Submissions Value Report', 'Original Submissions Value Report', '00000000-0000-0000-0000-000000000000', CURRENT_DATE, 30, '523ed024-74f9-4288-9624-bbfeb04f45d0', '00000000-0000-0000-0000-000000000003', 'Nigel Howell', 'Nigel.Howell@Justice.gov.uk', 'Y')
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.reports (id, description, name, file_name, template_secure_document_id, report_creation_date, num_days_to_keep, report_output_type, report_owner_id, report_owner_name, report_owner_email, active)
VALUES ('aca2120c-8f82-45a8-a682-8dedfb7997a7', 'Current Submissions Value Report', 'REP013 - Current Submissions Value Report', 'Current Submissions Value Report', '00000000-0000-0000-0000-000000000000', CURRENT_DATE, 30, '523ed024-74f9-4288-9624-bbfeb04f45d0', '00000000-0000-0000-0000-000000000003', 'Nigel Howell', 'Nigel.Howell@Justice.gov.uk', 'Y')
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.reports (id, description, name, file_name, template_secure_document_id, report_creation_date, num_days_to_keep, report_output_type, report_owner_id, report_owner_name, report_owner_email, active)
VALUES ('55daf3c1-28f0-4260-9396-2ee6d537abab', 'Claim Amendments Information Report', 'REP014 - Claim Amendments Information Report', 'REP014', '00000000-0000-0000-0000-000000000000', CURRENT_DATE, 30, '523ed024-74f9-4288-9624-bbfeb04f45d0', '00000000-0000-0000-0000-000000000003', 'Nigel Howell', 'Nigel.Howell@Justice.gov.uk', 'Y')
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.reports (id, description, name, file_name, template_secure_document_id, report_creation_date, num_days_to_keep, report_output_type, report_owner_id, report_owner_name, report_owner_email, active)
VALUES ('c4ba2e89-c106-48a7-8e1d-7c19dbd7710d', 'New Matter Starts data extract for Submit a Bulk Claim Data', 'REP002 - New Matter Starts Data Extract for Submit a Bulk Claim Data', 'New Matter Starts', '00000000-0000-0000-0000-000000000000', CURRENT_DATE, 30, '523ed024-74f9-4288-9624-bbfeb04f45d0', '00000000-0000-0000-0000-000000000003', 'William Moran', 'William.Moran@Justice.gov.uk', 'Y')
ON CONFLICT (id) DO NOTHING;

-- -----------------------------------------------------------
-- REPORT QUERIES (59 rows)
-- -----------------------------------------------------------
INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index")
VALUES ('1dc32729-f50d-418e-a2af-ad83d9248bb0', 'b36f9bbb-1178-432c-8f99-8090e285f2d3', 'SELECT * FROM ANY_REPORT.V_CIS_TO_CCMS_INVOICE_SUMMARY', 'CIS to CCMS import analysis', 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index")
VALUES ('1dc32729-f50d-418e-a2af-ad83d9248bb1', 'b36f9bbb-1178-432c-8f99-8090e285f2d3', 'SELECT * FROM ANY_REPORT.V_CIS_TO_CCMS_INVOICE_EXCEPTIONS', 'CIS to CCMS import exceptions', 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index")
VALUES ('1dc32729-f50d-418e-a2af-ad83d9248bb2', 'b36f9bbb-1178-432c-8f99-8090e285f2d3', 'SELECT * FROM ANY_REPORT.V_CIS_TO_CCMS_INVOICE_DEFINED_PAYMENT_GROUPS', 'CCMS Payment value (user def)', 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index")
VALUES ('1dc32729-f50d-418e-a2af-ad83d9248bb3', 'b36f9bbb-1178-432c-8f99-8090e285f2d3', 'SELECT * FROM ANY_REPORT.V_CIS_TO_CCMS_INVOICE_NOT_DEFINED_PAYMENT_GROUPS', 'CCMS Payment value (not def)', 4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index")
VALUES ('1dc32729-f50d-418e-a2af-ad83d9248bb4', 'b36f9bbb-1178-432c-8f99-8090e285f2d3', 'SELECT * FROM ANY_REPORT.V_CIS_TO_CCMS_INVOICE_HELD_PAYMENTS', 'CCMS Held payments', 5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index")
VALUES ('1dc32729-f50d-418e-a2af-ad83d9248bb5', 'b36f9bbb-1178-432c-8f99-8090e285f2d3', 'SELECT * FROM ANY_REPORT.V_CIS_TO_CCMS_INVOICE_CCMS_AP_DEBTS', 'CCMS AP Debtors', 6)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index")
VALUES ('f46b4d3d-c100-429a-bf9a-6c3305dbdbe1', 'f46b4d3d-c100-429a-bf9a-6c3305dbdbf1', 'SELECT * FROM ANY_REPORT.V_CIS_TO_CCMS_INVOICE_SUMMARY', 'MAIN', 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index")
VALUES ('f46b4d3d-c100-429a-bf9a-6c3305dbdbe2', 'f46b4d3d-c100-429a-bf9a-6c3305dbdbf2', 'SELECT * FROM ANY_REPORT.V_BANK_MONTH', 'MAIN', 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index")
VALUES ('f46b4d3d-c100-429a-bf9a-6c3305dbdbe3', 'f46b4d3d-c100-429a-bf9a-6c3305dbdbf3', 'SELECT * FROM ANY_REPORT.V_CIS_TO_CCMS_INVOICE_EXCEPTIONS', 'MAIN', 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index")
VALUES ('f46b4d3d-c100-429a-bf9a-6c3305dbdbe4', 'f46b4d3d-c100-429a-bf9a-6c3305dbdbf4', 'SELECT * FROM ANY_REPORT.V_CIS_TO_CCMS_INVOICE_DEFINED_PAYMENT_GROUPS', 'MAIN', 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index")
VALUES ('f46b4d3d-c100-429a-bf9a-6c3305dbdbe5', 'f46b4d3d-c100-429a-bf9a-6c3305dbdbf5', 'SELECT * FROM ANY_REPORT.V_CIS_TO_CCMS_INVOICE_NOT_DEFINED_PAYMENT_GROUPS', 'MAIN', 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index")
VALUES ('f46b4d3d-c100-429a-bf9a-6c3305dbdbe6', 'f46b4d3d-c100-429a-bf9a-6c3305dbdbf6', 'SELECT * FROM ANY_REPORT.V_CIS_TO_CCMS_INVOICE_HELD_PAYMENTS', 'MAIN', 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index")
VALUES ('f46b4d3d-c100-429a-bf9a-6c3305dbdbe7', 'f46b4d3d-c100-429a-bf9a-6c3305dbdbf7', 'SELECT * FROM ANY_REPORT.V_CIS_TO_CCMS_INVOICE_CCMS_AP_DEBTS', 'MAIN', 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index")
VALUES ('f46b4d3d-c100-429a-bf9a-6c3305dbdbe8', 'f46b4d3d-c100-429a-bf9a-6c3305dbdbf8', 'SELECT * FROM ANY_REPORT.V_BANK_YTD', 'MAIN', 4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index")
VALUES ('f46b4d3d-c100-429a-bf9a-6c3305dbdbe9', 'f46b4d3d-c100-429a-bf9a-6c3305dbdbf9', 'SELECT * FROM ANY_REPORT.V_CCMS_GL_EXTRACTOR_YTD', 'MAIN', 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index")
VALUES ('f46b4d3d-c100-429a-bf9a-6c3305dbdbea', 'f46b4d3d-c100-429a-bf9a-6c3305dbdbfa', 'SELECT * FROM ANY_REPORT.V_CCMS_GL_EXTRACTOR_MANUAL_YTD', 'MAIN', 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index")
VALUES ('f46b4d3d-c100-429a-bf9a-6c3305dbdbeb', 'f46b4d3d-c100-429a-bf9a-6c3305dbdbfb', 'SELECT * FROM ANY_REPORT.V_CCMS_AUTHORISED_INVS_CONTRACTING', 'MAIN', 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index")
VALUES ('f46b4d3d-c100-429a-bf9a-223305dbdbfb', 'f46b4d3d-c100-429a-bf9a-223305dbdbfb', 'SELECT * FROM ANY_REPORT.V_CCMS_GL_EXTRACTOR_MANUAL_YTD', 'DATA', 0)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index")
VALUES ('eee30b23-2c8d-4b4b-bb11-8cd67d07915c', 'eee30b23-2c8d-4b4b-bb11-8cd67d07915c', 'SELECT * FROM ANY_REPORT.V_BANK_YTD', 'MAIN', 4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index")
VALUES ('dd3d11dd-b7da-4494-a01a-c064ea8d82ff', 'eee30b23-2c8d-4b4b-bb11-8cd67d07915c', 'SELECT * FROM ANY_REPORT.V_BANK_REPORT_PROVIDER_CONTIGENCY', 'Provider Contigency', 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index")
VALUES ('a017241a-359f-4fdb-a0cd-7f28f1946ef1', 'a017241a-359f-4fdb-a0cd-7f28f1946ef1', 'SELECT * FROM ANY_REPORT.V_BANK_MONTH', 'MAIN', 4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index")
VALUES ('e7350dc8-458b-4929-8cf0-85b47f9251b3', 'a017241a-359f-4fdb-a0cd-7f28f1946ef1', 'SELECT * FROM ANY_REPORT.V_BANK_REPORT_PROVIDER_CONTINGENCY', 'Provider Contigency', 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index")
VALUES ('7073dd13-e325-4863-a05c-a049a815d1f7', '7073dd13-e325-4863-a05c-a049a815d1f7', 'SELECT * FROM ANY_REPORT.V_CURRENT_ACCOUNT_LEGAL_HELP', 'DATA', 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index")
VALUES ('1c8f8d55-aaf3-4ab1-a07b-63f85ef2f2ea', '7073dd13-e325-4863-a05c-a049a815d1f7', 'SELECT * FROM ANY_REPORT.V_CURRENT_ACCOUNT_FIN_ADJUST', 'Adjusted Expenditure', 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index")
VALUES ('e6823193-f5b0-451b-8965-e4d4914980da', 'e6823193-f5b0-451b-8965-e4d4914980da', 'SELECT * FROM ANY_REPORT.V_CURRENT_ACCOUNT_LOWER_CRIME', 'DATA', 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index")
VALUES ('4a978b58-f7f7-43db-854b-86bbab8abdc7', 'e6823193-f5b0-451b-8965-e4d4914980da', 'SELECT * FROM ANY_REPORT.V_CURRENT_ACCOUNT_FIN_ADJUST', 'Adjusted Expenditure', 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index")
VALUES ('56328b13-254d-435d-813a-5863f94b996d', '56328b13-254d-435d-813a-5863f94b996d', 'SELECT * FROM ANY_REPORT.V_CURRENT_ACCOUNT_MEDIATION', 'DATA', 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index")
VALUES ('70847839-5103-4971-b163-f71832893f70', '56328b13-254d-435d-813a-5863f94b996d', 'SELECT * FROM ANY_REPORT.V_CURRENT_ACCOUNT_FIN_ADJUST', 'Adjusted Expenditure', 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index")
VALUES ('f68f7f0d-e379-4423-876c-6fef92cb1b1e', '7bda9aa4-6129-4c71-bd12-7d4e46fdd882', 'SELECT * FROM ANY_REPORT.V_LATE_PROCESSED_CCR_DATA', 'DATA', 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index")
VALUES ('a73dd25d-c9b9-4a60-80fc-cbc3b79f1cd6', '516cdbff-5fa8-4050-b5e6-7edf71daf679', 'SELECT * FROM ANY_REPORT.V_LATE_PROCESSED_CCLF_DATA', 'DATA', 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index")
VALUES ('c07aae9d-ab80-4059-8ce3-2c959cbc5fda', '8b9f0484-819f-4e0f-b60a-0b3f9d30d9ba', 'SELECT *  FROM ANY_REPORT.V_CIVIL_CASE_DEBT_CCMS_CLASS_SUMMARY', 'CCMS_DEBT_SUMMARY_B_EXP', 14)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index")
VALUES ('679c389f-33b2-4b26-8876-099e9d7d741d', '8b9f0484-819f-4e0f-b60a-0b3f9d30d9ba', 'SELECT *  FROM ANY_REPORT.V_CIVIL_CASE_DEBT_CCMS', 'CCMS_CASE_TOTALS_TRUE', 10)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index")
VALUES ('b7ec8f81-f912-4c00-af39-073dad745284', '8b9f0484-819f-4e0f-b60a-0b3f9d30d9ba', 'SELECT *  FROM ANY_REPORT.V_CIVIL_CASE_DEBT_CCMS_MIGRATED_IN_ERROR_EXCP', 'FIXED_CASES_EXP', 11)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index")
VALUES ('b37c38b2-5b74-45f0-9822-1d855041943a', '8b9f0484-819f-4e0f-b60a-0b3f9d30d9ba', 'SELECT *  FROM ANY_REPORT.V_CIVIL_CASE_DEBT_CCMS_MOVEMENT_SUMMARY', 'CCMS_DEBT_SUMMARY_C_EXP', 13)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index")
VALUES ('7c2d4236-81a9-4e19-a4d9-1d81458ae745', '8b9f0484-819f-4e0f-b60a-0b3f9d30d9ba', 'SELECT *  FROM ANY_REPORT.V_CIVIL_CASE_DEBT_CCMS_NOT_LINKED_EXCP', 'CCMS_CIVIL_EXCEPTIONS_EXP', 12)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, query, tab_name, "index")
VALUES ('dac688af-d0da-42cc-af8a-a581c123d9e4', '90af8289-2c07-4b65-8f37-6b4659920207', 'SELECT * FROM ANY_REPORT.V_LATE_PROCESSED_CIS_CIVIL_REP_DATA', 'DATA', 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, tab_name, "index")
VALUES ('829fc6df-82ca-40bb-bf16-67b83675e258', 'b36f9bbb-1178-432c-8f99-8090e285f2d3', 'MAIN', 0)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, tab_name, "index")
VALUES ('a3a80d35-7182-4b0c-a9f2-7bfd3994ef19', '8b9f0484-819f-4e0f-b60a-0b3f9d30d9ba', 'MAIN', 0)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, tab_name, "index")
VALUES ('bb871c0c-171f-438d-b740-38872089e116', '8b9f0484-819f-4e0f-b60a-0b3f9d30d9ba', 'MAIN WOFF MIG ERROR', 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, tab_name, "index")
VALUES ('f70184b4-c8d7-4afc-8b68-b201be371e16', '8b9f0484-819f-4e0f-b60a-0b3f9d30d9ba', 'WOFF Mig Error', 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, tab_name, "index")
VALUES ('a4b64247-b3de-475d-8191-081a3a4400a6', '8b9f0484-819f-4e0f-b60a-0b3f9d30d9ba', 'Reconciliation', 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, tab_name, "index")
VALUES ('e3f499ef-8e59-4e25-a38f-6a4b0c0a3cf3', '8b9f0484-819f-4e0f-b60a-0b3f9d30d9ba', 'Age Profile', 4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, tab_name, "index")
VALUES ('5d81a198-7880-4e48-a899-03b56368c410', '8b9f0484-819f-4e0f-b60a-0b3f9d30d9ba', 'Civil Movement Report', 5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, tab_name, "index")
VALUES ('9868bb9c-9d87-4933-9c48-3b36f6e3b2a5', '8b9f0484-819f-4e0f-b60a-0b3f9d30d9ba', 'All Debts by Type Summary', 6)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, tab_name, "index")
VALUES ('ae248695-bf73-49fa-ad31-0c63f5be7245', '8b9f0484-819f-4e0f-b60a-0b3f9d30d9ba', 'Correctly linked Civil Cases', 7)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, tab_name, "index")
VALUES ('57f268fc-8233-4202-9c38-265dd61019b6', '8b9f0484-819f-4e0f-b60a-0b3f9d30d9ba', 'Civil Debt Exceptions', 8)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, tab_name, "index")
VALUES ('3409e8ba-5c05-4862-a809-9106d6fe241c', '8b9f0484-819f-4e0f-b60a-0b3f9d30d9ba', 'Third Party Cash Receipt Income', 9)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, tab_name, "index")
VALUES ('09aab7ff-416b-4fdc-a26b-a5118a0bb418', '56328b13-254d-435d-813a-5863f94b996d', 'MAIN', 0)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, tab_name, "index")
VALUES ('db804fe4-3ef9-45fc-ac8c-566ca9b42683', 'e6823193-f5b0-451b-8965-e4d4914980da', 'MAIN', 0)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, tab_name, "index")
VALUES ('b8be5f8c-2f53-4f50-aa22-a7fb322a122b', '7073dd13-e325-4863-a05c-a049a815d1f7', 'MAIN', 0)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, tab_name, "index")
VALUES ('5b3d1d57-d768-456d-af31-a838ac8b8773', 'eee30b23-2c8d-4b4b-bb11-8cd67d07915c', 'Summary', 0)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, tab_name, "index")
VALUES ('19ef9af1-9620-4e3d-8a9d-93173ce4940c', 'eee30b23-2c8d-4b4b-bb11-8cd67d07915c', 'By Source and Expenditure type', 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, tab_name, "index")
VALUES ('d9add97d-3ac8-40ed-84ca-8f568644ddbe', 'eee30b23-2c8d-4b4b-bb11-8cd67d07915c', 'Transparency Rec', 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, tab_name, "index")
VALUES ('7ee0bff5-084e-4672-a5c7-3e45cafbc4d5', 'f46b4d3d-c100-429a-bf9a-6c3305dbdbf2', 'Summary', 0)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, tab_name, "index")
VALUES ('8869e089-c5b6-4263-9421-88fff99dae18', 'f46b4d3d-c100-429a-bf9a-6c3305dbdbf2', 'By Source and Expenditure type', 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, tab_name, "index")
VALUES ('8e6cde19-ca31-4119-9c64-d2fa36029988', 'f46b4d3d-c100-429a-bf9a-6c3305dbdbf2', 'Transparency Rec', 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, tab_name, "index")
VALUES ('55376b05-3c4a-40dd-bc92-ff6d198784b7', 'a017241a-359f-4fdb-a0cd-7f28f1946ef1', 'Summary', 0)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, tab_name, "index")
VALUES ('3d6b7116-79c6-4f5a-8043-af7e4e7aaa66', 'a017241a-359f-4fdb-a0cd-7f28f1946ef1', 'By Source and Expenditure type', 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.report_queries (id, report_id, tab_name, "index")
VALUES ('59481287-45b5-4dfc-ab09-164633302543', 'a017241a-359f-4fdb-a0cd-7f28f1946ef1', 'Transparency Rec', 3)
ON CONFLICT (id) DO NOTHING;

-- -----------------------------------------------------------
-- FIELD ATTRIBUTES (622 rows)
-- -----------------------------------------------------------
INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('b9dc706c-b05d-4339-9521-1fad144b7361', 'DATE_AUTHORISED_CIS', 'DATE_AUTHORISED_CIS', 'dd-MMM-yy', 'date', 20.67, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('b9dc706c-b05d-4339-9521-1fad144b7362', 'THE_SYSTEM', 'THE_SYSTEM', '', 'string', 22.83, 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('b9dc706c-b05d-4339-9521-1fad144b7363', 'CIS_VALUE', 'CIS_VALUE', '#.00', 'double', 10.17, 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('b9dc706c-b05d-4339-9521-1fad144b7364', 'CCMS_VALUE', 'CCMS_VALUE', '#.00', 'double', 11.83, 4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('8f0fb820-640a-41a9-b5a1-e74c336a19a1', 'ACC_CODE', 'ACC_CODE', '', 'string', 9.33, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('8f0fb820-640a-41a9-b5a1-e74c336a19a2', 'ACCO_HELD_BY_TYPE', 'ACCO_HELD_BY_TYPE', '', 'string', 18.67, 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('8f0fb820-640a-41a9-b5a1-e74c336a19a3', 'DTYP_DOC_TYPE_ID', 'DTYP_DOC_TYPE_ID', '', 'string', 17.33, 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('8f0fb820-640a-41a9-b5a1-e74c336a19a4', 'THE_SYSTEM', 'THE_SYSTEM', '', 'string', 11.17, 4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('8f0fb820-640a-41a9-b5a1-e74c336a19a5', 'DATE_CREATED_CIS', 'DATE_CREATED_CIS', 'dd-MMM-yy', 'date', 17.17, 5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('8f0fb820-640a-41a9-b5a1-e74c336a19a6', 'DATE_AUTHORISED_CIS', 'DATE_AUTHORISED_CIS', 'dd-MMM-yy', 'date', 20.67, 6)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('8f0fb820-640a-41a9-b5a1-e74c336a19a7', 'TRANS_INT_ID', 'TRANS_INT_ID', '', 'long', 12.67, 7)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('8f0fb820-640a-41a9-b5a1-e74c336a19a8', 'VOLUME', 'VOLUME', '', 'long', 7.67, 8)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('8f0fb820-640a-41a9-b5a1-e74c336a19a9', 'VALUE', 'VALUE', '#.00', 'double', 9.17, 9)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('8f0fb820-640a-41a9-b5a1-e74c336a19b1', 'INVOICE_ID', 'INVOICE_ID', '', 'string', 10, 10)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('8f0fb820-640a-41a9-b5a1-e74c336a19b2', 'GL_DATE', 'GL_DATE', 'dd-MMM-yy', 'date', 7.67, 11)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('8f0fb820-640a-41a9-b5a1-e74c336a19b3', 'INVOICE_AMOUNT', 'INVOICE_AMOUNT', '', 'long', 16.5, 12)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('251ab6d4-0e29-4c9a-aae2-dfa3abf91c41', 'VENDOR_SITE_CODE', 'VENDOR_SITE_CODE', '', 'string', 17.83, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('251ab6d4-0e29-4c9a-aae2-dfa3abf91c42', 'VENDOR_SITE_NAME', 'VENDOR_SITE_NAME', '', 'string', 63.67, 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('251ab6d4-0e29-4c9a-aae2-dfa3abf91c43', 'FIRST_GL_DATE', 'FIRST_GL_DATE', 'dd-MMM-yy', 'date', 13.33, 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('251ab6d4-0e29-4c9a-aae2-dfa3abf91c44', 'LATEST_GL_DATE', 'LATEST_GL_DATE', 'dd-MMM-yy', 'date', 15, 4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('251ab6d4-0e29-4c9a-aae2-dfa3abf91c45', 'TOTAL', 'TOTAL', '#.00', 'double', 9.17, 5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('ba373b4e-e735-49dd-9087-d7f1fb40e1b1', 'VENDOR_SITE_CODE', 'VENDOR_SITE_CODE', '', 'string', 17.83, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('ba373b4e-e735-49dd-9087-d7f1fb40e1b2', 'VENDOR_SITE_NAME', 'VENDOR_SITE_NAME', '', 'string', 35, 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('ba373b4e-e735-49dd-9087-d7f1fb40e1b3', 'PAY_GROUP_LOOKUP_CODE', 'PAY_GROUP_LOOKUP_CODE', '', 'string', 24.83, 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('ba373b4e-e735-49dd-9087-d7f1fb40e1b4', 'FIRST_GL_DATE', 'FIRST_GL_DATE', 'dd-MMM-yy', 'date', 13.33, 4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('ba373b4e-e735-49dd-9087-d7f1fb40e1b5', 'LATEST_GL_DATE', 'LATEST_GL_DATE', 'dd-MMM-yy', 'date', 15, 5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('ba373b4e-e735-49dd-9087-d7f1fb40e1b6', 'TOTAL', 'TOTAL', '#.00', 'double', 8.17, 6)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('de98ef9c-400f-4260-9e3c-927cc503db21', 'VENDOR_SITE_CODE', 'VENDOR_SITE_CODE', '', 'string', 17.83, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('de98ef9c-400f-4260-9e3c-927cc503db22', 'VENDOR_SITE_NAME', 'VENDOR_SITE_NAME', '', 'string', 68.67, 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('de98ef9c-400f-4260-9e3c-927cc503db23', 'FIRST_GL_DATE', 'FIRST_GL_DATE', 'dd-MMM-yy', 'date', 13.33, 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('de98ef9c-400f-4260-9e3c-927cc503db24', 'LATEST_GL_DATE', 'LATEST_GL_DATE', 'dd-MMM-yy', 'date', 15, 4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('de98ef9c-400f-4260-9e3c-927cc503db25', 'TOTAL_INC_MEDIATION', 'TOTAL_INC_MEDIATION', '#.00', 'double', 21, 5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('de98ef9c-400f-4260-9e3c-927cc503dd21', 'VENDOR_SITE_CODE', 'VENDOR_SITE_CODE', '', 'string', 17.83, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('de98ef9c-400f-4260-9e3c-927cc503dd22', 'VENDOR_SITE_NAME', 'VENDOR_SITE_NAME', '', 'string', 68.67, 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('de98ef9c-400f-4260-9e3c-927cc503dd23', 'FIRST_GL_DATE', 'FIRST_GL_DATE', 'dd-MMM-yy', 'date', 13.33, 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('de98ef9c-400f-4260-9e3c-927cc503dd24', 'LATEST_GL_DATE', 'LATEST_GL_DATE', 'dd-MMM-yy', 'date', 15, 4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('de98ef9c-400f-4260-9e3c-927cc503dd25', 'TOTAL_INC_MEDIATION', 'TOTAL_INC_MEDIATION', '#.00', 'double', 21, 5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('34574fe0-5213-4d84-887c-7d46dc3a357b', 'GL_BATCH_ID', 'GL_BATCH_ID', '', 'string', 13.16, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('3f885eb8-c1e1-4b86-91f9-f88f592d8ecc', 'GL_BATCH_STATUS', 'GL_BATCH_STATUS', '', 'string', 18.16, 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('74e26cb7-77d8-48b9-9b00-f4960fe07c03', 'GL_BATCH_NAME', 'GL_BATCH_NAME', '', 'string', 96.16, 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('9851fdb1-f1d9-494a-9dac-7df2468a088e', 'GL_BATCH_DESC', 'GL_BATCH_DESC', '', 'string', 143, 4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('0d365ef7-5ba4-456e-a13a-8f3dd221f279', 'GL_HEADER_PERIOD', 'GL_HEADER_PERIOD', '', 'string', 10.83, 5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('96fe70e7-6e90-4e43-a3ef-0b923e5b2ae7', 'JE_SOURCE', 'JE_SOURCE', '', 'string', 10.83, 6)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('c7a19495-c7b6-416d-81c2-93047eaa50f7', 'JE_CATEGORY', 'JE_CATEGORY', '', 'string', 13.5, 7)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('55c8f2bc-3a9d-41dd-906f-b68e825a2220', 'GL_CODE', 'GL_CODE', '', 'string', 21.16, 8)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('816724b4-f310-4184-b64f-206b6077fa07', 'SEG5', 'SEG5', '', 'string', 5.5, 9)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('2e378fc4-6d24-4acd-b6ed-f4ecf0df24b3', 'ACCOUNTED_CR', 'ACCOUNTED_CR', '#.00', 'double', 15.5, 10)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('f79dfec9-45d7-4a87-8d1c-a18afde9701d', 'ACCOUNTED_DR', 'ACCOUNTED_DR', '#.00', 'double', 15.66, 11)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('9c3bb76f-eea7-422f-8d9c-e54fd50a7c89', 'TOTAL', 'TOTAL', '#.00', 'double', 12.66, 12)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('ca98e379-ee0e-41d3-bc2e-3bd5808ebdfe', 'SOURCE', 'SOURCE', '', 'string', 8.16, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('62a9ba72-07c7-4ed1-8d19-35212869b93a', 'INV_SOURCE', 'INV_SOURCE', '', 'string', 12.5, 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('6ff6a6d3-2621-472c-bd43-9ca57eacbf5b', 'SUB_SOURCE', 'SUB_SOURCE', '', 'string', 16.16, 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('36d590f9-959b-4ae1-9c73-cbbfa25ec7b1', 'PAYMENT_DATE', 'PAYMENT_DATE', 'dd-MMM-yy', 'date', 15.5, 4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('34b694ec-a54c-4e44-b1ac-35f4a7834129', 'PAYMENT_MONTH', 'PAYMENT_MONTH', 'dd-MMM-yy', 'date', 18.16, 5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('f44e2540-0ff1-4e81-a236-426b6e9c3a68', 'SETTLEMENT_TYPE', 'SETTLEMENT_TYPE', '', 'string', 17.66, 6)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('7772b325-d612-48b9-8f8c-037a9a99a6a5', 'SCHEME', 'SCHEME', '', 'string', 8.33, 7)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('b8e03e3d-8597-492b-91c8-5fb0d735c99d', 'SUB_SCHEME', 'SUB_SCHEME', '', 'string', 19.33, 8)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('a98add1e-3e83-45cb-b30f-66cda623b1c0', 'DETAIL_DESC', 'DETAIL_DESC', '', 'string', 36.5, 9)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('83d659f8-90c0-4e1e-b299-aa35aa8fa5b9', 'CAT_CODE', 'CAT_CODE', '', 'string', 10.33, 10)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('bd3d438c-10ea-409e-85e3-d8380c24aa4f', 'AP_AR_MOVEMENT', 'AP_AR_MOVEMENT', '', 'string', 19, 11)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('f0dd86ad-7b53-4f8b-92d7-094268069f67', 'TOTAL', 'TOTAL', '#.00', 'double', 12.66, 12)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('030eeff8-9d2e-4d97-b244-7e5b6f1af205', 'OFFICE_NUMBER', 'OFFICE_NUMBER', '', 'string', 16.33, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('b62176a3-ab80-4c8c-90e5-c0ebd5826266', 'OFFICE_NAME', 'OFFICE_NAME', '', 'string', 68.33, 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('194f4357-0bb9-4fd5-a67c-0918312be852', 'BANK_NUM', 'BANK_NUM', '', 'string', 11.66, 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('7e10640e-7692-4115-99f8-42fe4c07f38f', 'BANK_ACCOUNT_NUM', 'BANK_ACCOUNT_NUM', '', 'string', 22, 4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('014afd7f-6465-429d-9af1-3227ab9dcc0d', 'BANK_ACCOUNT_NAME', 'BANK_ACCOUNT_NAME', '', 'string', 67.5, 5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('b27871da-1cf6-43ac-a7b9-9ba257dbb35f', 'VENDOR_SITE_ID', 'VENDOR_SITE_ID', '', 'number', 16.16, 6)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('458f9360-bcf9-49e0-97a7-e355d5b76728', 'HOLD_ALL_PAYMENTS_FLAG', 'HOLD_ALL_PAYMENTS_FLAG', '', 'string', 26.83, 7)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('44bef17d-fa51-4470-969c-154182f62f87', 'PAYMENTS_CHECKSUM', 'PAYMENTS_CHECKSUM', '', 'number', 22.33, 8)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('0b70bd4e-493a-4633-af14-6a65283761ae', 'PAYMENTS_LAST_SIX', 'PAYMENTS_LAST_SIX', '', 'number', 18, 9)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('015809ca-9c1b-4a3c-a9c8-f8bb10b3eb37', 'PAYMENTS_LAST_TWELVE', 'PAYMENTS_LAST_TWELVE', '', 'number', 24.5, 10)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('2d950cde-f416-4541-b8a2-8744d782454e', 'AVG_SIX', 'AVG_SIX', '', 'number', 9, 11)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('7a79d9d0-585a-437d-ba6d-22cb3b517010', 'AVG_TWELVE', 'AVG_TWELVE', '', 'number', 13.33, 12)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d35b6b7e-fc98-4517-84d8-fa938043db18', 'AR_DEBT', 'AR_DEBT', '', 'number', 10.66, 13)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('fdd51bfd-3c70-4469-a67e-b478d582562e', 'SOURCE', 'SOURCE', '', 'string', 8.16, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('4b794dff-d077-4980-b8d0-ac2109e90c25', 'INV_SOURCE', 'INV_SOURCE', '', 'string', 12.5, 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('1e6275dd-cb29-478c-97e0-3d577d541855', 'SUB_SOURCE', 'SUB_SOURCE', '', 'string', 16.16, 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('e149a44c-0410-440d-8104-f84c5cb1fc14', 'PAYMENT_DATE', 'PAYMENT_DATE', 'dd-MMM-yy', 'date', 15.5, 4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('b92a44b6-2bcf-4622-bea2-7da731854d63', 'PAYMENT_MONTH', 'PAYMENT_MONTH', 'dd-MMM-yy', 'date', 18.16, 5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('de7ad520-0492-4b83-a02f-71d817dfea2d', 'SETTLEMENT_TYPE', 'SETTLEMENT_TYPE', '', 'string', 17.66, 6)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('5b67e8e9-0c3b-43b1-8ec7-af94981ad9d8', 'SCHEME', 'SCHEME', '', 'string', 8.33, 7)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('00d3f087-e0c9-4ea8-8bda-0fab217249a5', 'SUB_SCHEME', 'SUB_SCHEME', '', 'string', 19.33, 8)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('a04245fc-3a1c-4cbf-a2e9-d6154a40cb4e', 'DETAIL_DESC', 'DETAIL_DESC', '', 'string', 36.5, 9)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('199ea26f-a789-4f13-b7ef-b190725ebd60', 'CAT_CODE', 'CAT_CODE', '', 'string', 10.33, 10)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('854a766a-010a-4db8-b2ee-2be3385eafff', 'AP_AR_MOVEMENT', 'AP_AR_MOVEMENT', '', 'string', 19, 11)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('a72372f5-f079-4c70-b044-c84c65917058', 'TOTAL', 'TOTAL', '#.00', 'double', 12.66, 12)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('850419e6-cdcf-4500-b26f-e6c0fff03292', 'OFFICE_NUMBER', 'OFFICE_NUMBER', '', 'string', 16.33, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('e759d33f-a3be-4891-b104-12e1157ea924', 'OFFICE_NAME', 'OFFICE_NAME', '', 'string', 68.33, 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('dadc6659-3272-488c-abe8-ea4d709b0afd', 'BANK_NUM', 'BANK_NUM', '', 'number', 11.66, 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('9da9dc00-0f2f-4109-be84-5c379d59fc8c', 'BANK_ACCOUNT_NUM', 'BANK_ACCOUNT_NUM', '', 'number', 22, 4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('bfa602eb-160d-4e46-b435-02dafe54a38b', 'BANK_ACCOUNT_NAME', 'BANK_ACCOUNT_NAME', '', 'string', 67.5, 5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('ac899872-6407-4c64-8f02-4ec8bd2292e5', 'VENDOR_SITE_ID', 'VENDOR_SITE_ID', '', 'number', 16.16, 6)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('f122ec90-6e3f-424d-b9c2-0b79c4dbfa4b', 'HOLD_ALL_PAYMENTS_FLAG', 'HOLD_ALL_PAYMENTS_FLAG', '', 'string', 26.83, 7)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('7ecccd21-fd11-4d94-96ae-9aa3e78a7b32', 'PAYMENTS_CHECKSUM', 'PAYMENTS_CHECKSUM', '', 'number', 22.33, 8)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('a0b45dce-107f-4d96-9efe-5a1f4dd98e28', 'PAYMENTS_LAST_SIX', 'PAYMENTS_LAST_SIX', '', 'number', 18, 9)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('45e7cdf3-c546-4814-b62e-e934343d3128', 'PAYMENTS_LAST_TWELVE', 'PAYMENTS_LAST_TWELVE', '', 'number', 24.5, 10)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('fc6966e1-9905-4779-96fd-8fff800db7f0', 'AVG_SIX', 'AVG_SIX', '', 'number', 9, 11)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('e9dc2b87-ffd9-4034-942f-e68adbc9abdf', 'AVG_TWELVE', 'AVG_TWELVE', '', 'number', 13.33, 12)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('2382a42a-855d-4044-9ed6-ab77d47891fc', 'AR_DEBT', 'AR_DEBT', '', 'number', 10.66, 13)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('1d9cf44d-d02b-437e-bf12-95148a7e812b', 'OFFICE_CODE', 'OFFICE_CODE', '', 'string', 13.16, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('267ac92b-5e34-45bb-9509-49aaf689f1c4', 'OLD_CLAIMS', 'OLD_CLAIMS', '', 'string', 12.33, 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('ff392ffe-d821-403e-b470-8ddf9627f019', 'NEW_CLAIMS', 'NEW_CLAIMS', '', 'string', 13.33, 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('10f1e842-995c-4c18-9fe7-e94276120b95', 'TOTAL_CLAIMS', 'TOTAL_CLAIMS', 'dd-MMM-yy', 'date', 14.5, 4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d0c20eb7-5366-45de-b5b6-28ebbddd5097', 'OLD_EXPEND', 'OLD_EXPEND', 'dd-MMM-yy', 'date', 12.5, 5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('94916877-aa91-4cd4-aa60-a18960ef7530', 'NEW_EXPEND', 'NEW_EXPEND', '', 'string', 13.5, 6)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('0c726311-5298-429b-ad27-3e18650f8d16', 'TOTAL_EXPEND', 'TOTAL_EXPEND', '', 'string', 14.66, 7)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('edfb487e-f0bc-4627-a7b3-4fbd7647e997', 'BAL_OF_CLAIMS_N_PAYS', 'BAL_OF_CLAIMS_N_PAYS', '', 'string', 23.83, 8)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('2ddad314-0d86-4541-b3ba-01a9333715ce', 'CONTRACT_ADJUSTMENTS', 'CONTRACT_ADJUSTMENTS', '', 'string', 25.12, 9)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('6889b363-f28f-4260-877d-105da58ea090', 'CONTRACT_BALANCE', 'CONTRACT_BALANCE', '', 'string', 20, 10)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('4a6ec3f1-9f90-4f8b-9b7c-f3c2dd61a7e0', 'IN_YEAR_CLAIMS', 'IN_YEAR_CLAIMS', '', 'string', 16.5, 11)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d5252b76-4930-4a97-86a3-eee59441e549', 'IN_YEAR_PAYMENTS', 'IN_YEAR_PAYMENTS', '', 'string', 19.66, 12)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('6dc9f61f-da16-49d6-b5e9-699d7544fad6', 'PAYMENT_RUN_ADJUSTMENT', 'PAYMENT_RUN_ADJUSTMENT', '', 'string', 28.5, 13)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('dd8f28f0-0e14-45be-9f00-6b4348cda2e5', 'FINAL_CONTRACT_BALANCE', 'FINAL_CONTRACT_BALANCE', '#.00', 'double', 26.5, 14)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('9c4f18cd-2260-4035-91f7-91ae5368b921', 'PARTY_SITE_NAME', 'PARTY_SITE_NAME', '', 'string', 18.16, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('bcc94bbf-4481-42d3-a2e8-fe8e49337fff', 'PARTY_SITE_ID', 'PARTY_SITE_ID', '', 'string', 14.33, 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('7b029e62-ae42-4718-b363-f4b4b46dbf48', 'INVOICE_NUM', 'INVOICE_NUM', '', 'string', 42.66, 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('839fc96e-8b6b-4638-9059-5108d3bd01ff', 'PAY_DATE', 'PAY_DATE', 'dd-MMM-yy', 'date', 10.16, 4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('00dd5d17-6efa-40b9-845b-1cade1704b00', 'SUB_SCHEME', 'SUB_SCHEME', '', 'string', 11.83, 5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('afe06dac-91f5-46e1-8a69-1c26de6e6cea', 'PAID_TOTAL', 'PAID_TOTAL', '', 'number', 16.16, 6)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('43d9c190-86af-4a22-8967-5191f8dfc3aa', 'OFFICE_CODE', 'OFFICE_CODE', '', 'string', 13.16, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('57ab8148-5fa6-4e0a-93dd-e86e07d5413c', 'OLD_CLAIMS', 'OLD_CLAIMS', '', 'string', 12.33, 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('cf36e742-dc03-4dff-a0d8-866b3e64b652', 'NEW_CLAIMS', 'NEW_CLAIMS', '', 'string', 13.33, 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('54e3e223-254f-4f82-90d7-1cc21124788d', 'TOTAL_CLAIMS', 'TOTAL_CLAIMS', 'dd-MMM-yy', 'date', 14.5, 4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('1d06ab67-e5d8-4332-81ab-fb731ecc32de', 'OLD_EXPEND', 'OLD_EXPEND', 'dd-MMM-yy', 'date', 12.5, 5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('71db1a56-5b09-48ae-b6bd-a59a4f4f71d7', 'NEW_EXPEND', 'NEW_EXPEND', '', 'string', 13.5, 6)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('a38b5c8b-dcbc-42ea-b1f2-d23d9b623f1f', 'TOTAL_EXPEND', 'TOTAL_EXPEND', '', 'string', 14.66, 7)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('78c79089-2472-4502-8e50-6e92aa83c295', 'BAL_OF_CLAIMS_N_PAYS', 'BAL_OF_CLAIMS_N_PAYS', '', 'string', 23.83, 8)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('f5e01749-110d-49a4-beec-8d76daa29e43', 'CONTRACT_ADJUSTMENTS', 'CONTRACT_ADJUSTMENTS', '', 'string', 25.12, 9)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('332683a2-d423-4dc9-b2a9-370f20e02853', 'CONTRACT_BALANCE', 'CONTRACT_BALANCE', '', 'string', 20, 10)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('b434e202-6942-4b22-af36-7e43b81005cf', 'IN_YEAR_CLAIMS', 'IN_YEAR_CLAIMS', '', 'string', 16.5, 11)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('7c77f33f-c9b2-4afb-ae4f-1b879fba7c79', 'IN_YEAR_PAYMENTS', 'IN_YEAR_PAYMENTS', '', 'string', 19.66, 12)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('c54f25c7-d574-4540-a8a0-df72e043e21a', 'PAYMENT_RUN_ADJUSTMENT', 'PAYMENT_RUN_ADJUSTMENT', '', 'string', 28.5, 13)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('47438c0b-99fa-41a5-8897-abcafc2f6dec', 'FINAL_CONTRACT_BALANCE', 'FINAL_CONTRACT_BALANCE', '#.00', 'double', 26.5, 14)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('14251627-ec1b-4d7b-9664-9bc07cd3a916', 'PARTY_SITE_NAME', 'PARTY_SITE_NAME', '', 'string', 18.16, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('a88e76b2-3b4d-47a5-81b5-6da17ba70fb3', 'PARTY_SITE_ID', 'PARTY_SITE_ID', '', 'string', 14.33, 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('a407c244-2905-4a2f-88f8-d1a5b2ed536e', 'INVOICE_NUM', 'INVOICE_NUM', '', 'string', 42.66, 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('3bf035e3-5304-4d76-9d5b-d6f56684f9df', 'PAY_DATE', 'PAY_DATE', 'dd-MMM-yy', 'date', 10.16, 4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('b22afba3-1724-401d-b34b-5ef771341b0a', 'SUB_SCHEME', 'SUB_SCHEME', '', 'string', 11.83, 5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('1453e24b-58ed-4c67-b5a3-11cd97afffa1', 'PAID_TOTAL', 'PAID_TOTAL', '', 'number', 16.16, 6)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('2c03a423-885d-431b-9539-6ba35ce57b3d', 'OFFICE_CODE', 'OFFICE_CODE', '', 'string', 13.16, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('4418c554-1e1e-4c56-aab1-252c5e7d695a', 'OLD_CLAIMS', 'OLD_CLAIMS', '', 'string', 12.33, 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('4e37d5df-0a43-4d16-a0ff-2eaefb921378', 'NEW_CLAIMS', 'NEW_CLAIMS', '', 'string', 13.33, 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('2ef5910e-ec95-4900-89f9-d09a293f8e27', 'TOTAL_CLAIMS', 'TOTAL_CLAIMS', 'null', 'string', 14.5, 4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('2087bc97-7c5a-431d-9d6c-bda729c8f206', 'OLD_EXPEND', 'OLD_EXPEND', 'null', 'string', 12.5, 5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('fced6f77-cf57-4c57-bf1b-76eef0e7a5a9', 'NEW_EXPEND', 'NEW_EXPEND', '', 'string', 13.5, 6)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('769c1b58-ddc5-40b3-b241-8c196b8a8fb1', 'TOTAL_EXPEND', 'TOTAL_EXPEND', '', 'string', 14.66, 7)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('325bade9-6283-4866-98a3-9c89ba1dea7e', 'BAL_OF_CLAIMS_N_PAYS', 'BAL_OF_CLAIMS_N_PAYS', '', 'string', 23.83, 8)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('eadad560-85f4-4ad4-b72a-dd0ee2149b09', 'CONTRACT_ADJUSTMENTS', 'CONTRACT_ADJUSTMENTS', '', 'string', 25.12, 9)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('c7749447-d47c-492b-8db4-2cbc277be1b9', 'CONTRACT_BALANCE', 'CONTRACT_BALANCE', '', 'string', 20, 10)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('9522d40f-6d39-4b19-a20b-5fa7efb81404', 'IN_YEAR_CLAIMS', 'IN_YEAR_CLAIMS', '', 'string', 16.5, 11)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d8b382c7-4d53-40f7-960c-3adcfa1fda6c', 'IN_YEAR_PAYMENTS', 'IN_YEAR_PAYMENTS', '', 'string', 19.66, 12)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('7d988ab6-e800-4964-a8f4-4306f117f80b', 'PAYMENT_RUN_ADJUSTMENT', 'PAYMENT_RUN_ADJUSTMENT', '', 'string', 28.5, 13)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('ea3cb880-9e49-451f-b823-eebfc771c162', 'FINAL_CONTRACT_BALANCE', 'FINAL_CONTRACT_BALANCE', '#.00', 'double', 26.5, 14)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('544aab18-bd73-44cf-952a-309dca366521', 'PARTY_SITE_NAME', 'PARTY_SITE_NAME', '', 'string', 18.16, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('2c596952-1996-4434-a618-e2cce5326af2', 'PARTY_SITE_ID', 'PARTY_SITE_ID', '', 'string', 14.33, 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('9e76ec6c-f07c-4572-aa85-9086d2471491', 'INVOICE_NUM', 'INVOICE_NUM', '', 'string', 42.66, 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('0252fe3c-1438-4234-910c-94364af1d006', 'PAY_DATE', 'PAY_DATE', 'dd-MMM-yy', 'date', 10.16, 4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('20fbb13d-0350-4100-8441-b509e4f7effe', 'SUB_SCHEME', 'SUB_SCHEME', '', 'string', 11.83, 5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('9cc6dbec-02cf-4736-8535-28c9282d7225', 'PAID_TOTAL', 'PAID_TOTAL', '', 'number', 16.16, 6)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('88c14880-d197-47da-8a15-8135f7fdc579', 'CASE_ID', 'CASE_ID', '', 'number', 8.33, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('0b0e7bbd-5e7b-4129-8222-9487dd6cf24e', 'CASE_NO', 'CASE_NO', '', 'string', 14.83, 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('73a69999-0207-4f27-97f0-d206721d7cf8', 'CASE_COURT_CODE', 'CASE_COURT_CODE', '', 'string', 30.83, 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('4de71712-3180-4513-a1cf-156879f69af0', 'CASE_REP_ORD_DATE', 'CASE_REP_ORD_DATE', '', 'date', 20.5, 4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('525be9fe-9714-4568-9f1c-6321e22a93b0', 'CASE_SUP_ACCOUNT_CODE', 'CASE_SUP_ACCOUNT_CODE', '', 'string', 31.16, 5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('5cfc77c2-3531-4869-8e9c-df9914ead029', 'BILL_ID', 'BILL_ID', '', 'number', 8, 6)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('085b52a3-4486-4cba-8221-9398f89d4734', 'BILL_TYPE', 'BILL_TYPE', '', 'string', 16.16, 7)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('3dd06092-b313-40a5-bb06-ece77e17fb38', 'PAST_PAYMENT_STATUS', 'PAST_PAYMENT_STATUS', '', 'string', 28, 8)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('b15f0fd8-ed95-44e4-8d4d-4965c1509025', 'BILL_CALCULATION_DATE', 'BILL_CALCULATION_DATE', '', 'date', 23.83, 9)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('c875db23-2531-4101-9662-482d95815dab', 'BILL_DATE_CREATED', 'BILL_DATE_CREATED', '', 'date', 19.16, 10)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('77383c7d-a43b-41b5-8d59-657003c421c7', 'DECISION_DATE', 'DECISION_DATE', '', 'date', 19.66, 11)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('ae91d631-265b-4d57-bf6b-12839cb2af8a', 'BILL_REQUESTED_DATE', 'BILL_REQUESTED_DATE', '', 'date', 26.16, 12)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('b0faa158-411c-4115-8fe7-ce1226726486', 'BILL_ACTUAL_FEE_INC_VAT', 'BILL_ACTUAL_FEE_INC_VAT', '', 'number', 25.66, 13)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('e10f760e-7602-41a9-bc44-72f75cf39e99', 'PIVOT_MONTH', 'PIVOT_MONTH', '', 'string', 18.5, 14)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('18c14880-d197-47da-8a15-8135f7fdc579', 'CASE_ID', 'CASE_ID', '', 'number', 8.33, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('1b0e7bbd-5e7b-4129-8222-9487dd6cf24e', 'CASE_NO', 'CASE_NO', '', 'string', 14.83, 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('13a69999-0207-4f27-97f0-d206721d7cf8', 'CASE_COURT_CODE', 'CASE_COURT_CODE', '', 'string', 30.83, 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('1de71712-3180-4513-a1cf-156879f69af0', 'CASE_REP_ORD_DATE', 'CASE_REP_ORD_DATE', '', 'date', 20.5, 4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('125be9fe-9714-4568-9f1c-6321e22a93b0', 'CASE_SUP_ACCOUNT_CODE', 'CASE_SUP_ACCOUNT_CODE', '', 'string', 31.16, 5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('1cfc77c2-3531-4869-8e9c-df9914ead029', 'BILL_ID', 'BILL_ID', '', 'number', 8, 6)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('185b52a3-4486-4cba-8221-9398f89d4734', 'BILL_TYPE', 'BILL_TYPE', '', 'string', 16.16, 7)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('1dd06092-b313-40a5-bb06-ece77e17fb38', 'PAST_PAYMENT_STATUS', 'PAST_PAYMENT_STATUS', '', 'string', 28, 8)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('115f0fd8-ed95-44e4-8d4d-4965c1509025', 'BILL_CALCULATION_DATE', 'BILL_CALCULATION_DATE', '', 'date', 23.83, 9)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('1875db23-2531-4101-9662-482d95815dab', 'BILL_DATE_CREATED', 'BILL_DATE_CREATED', '', 'date', 19.16, 10)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('17383c7d-a43b-41b5-8d59-657003c421c7', 'DECISION_DATE', 'DECISION_DATE', '', 'date', 19.66, 11)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('1e91d631-265b-4d57-bf6b-12839cb2af8a', 'BILL_REQUESTED_DATE', 'BILL_REQUESTED_DATE', '', 'date', 26.16, 12)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('10faa158-411c-4115-8fe7-ce1226726486', 'BILL_ACTUAL_FEE_INC_VAT', 'BILL_ACTUAL_FEE_INC_VAT', '', 'number', 25.66, 13)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('110f760e-7602-41a9-bc44-72f75cf39e99', 'PIVOT_MONTH', 'PIVOT_MONTH', '', 'string', 18.5, 14)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('8fd82afe-2af0-4c4d-8ccf-bd119bba9226', 'DEBT_TYPE', 'DEBT_TYPE', '', 'string', 70.5, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('de8a6135-9c3f-4633-8af3-34173ff14d83', 'CLASS', 'CLASS', '', 'string', 6.33, 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('f2383107-0058-45c8-af2a-69cf79a9cf6f', 'DEBT', 'DEBT', '', 'string', 12.66, 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width)
VALUES ('8696b19f-9484-4232-ab37-a0a0a4cf8d98', 'CM_DEBT CM_CHARGE', 'CM_DEBT CM_CHARGE', '', 'string', 11.17)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('4ec1e181-c23b-45e6-89c7-4f77005b9559', 'CM_UNAPPLIED', 'CM_UNAPPLIED', '', 'string', 15.33, 6)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('ddcbac3b-a0e4-49fd-a9d9-aa28c58a27c6', 'ADJUST_DEBT', 'ADJUST_DEBT', '', 'string', 13.33, 7)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d91b148b-c5a4-443e-b4c0-aa07100b4c2d', 'ADJUST_DEBT_GRE', 'ADJUST_DEBT_GRE', '', 'string', 18, 8)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('722ee512-4d61-49fa-9e44-aeadc517c8df', 'ADJUST_DEBT_LOW', 'ADJUST_DEBT_LOW', '', 'string', 18.66, 9)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('055a6bd2-439a-44df-803c-b4913e5d0e65', 'ADJUST_CHARGE', 'ADJUST_CHARGE', '', 'string', 16.16, 10)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width)
VALUES ('1df525b1-11a5-4806-b1db-ddc51bcf07cb', 'WO_DEBT WO_CHARGE', 'WO_DEBT WO_CHARGE', '', 'string', 11.83)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width)
VALUES ('9c135e25-2366-44db-b837-b4d6eb430a54', 'WD_DEBT WD_CHARGE', 'WD_DEBT WD_CHARGE', '', 'string', 11.67)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('5d53e4ff-84a9-46ba-8d62-d8e9f8895baf', 'INCOME_DEBT', 'INCOME_DEBT', '', 'string', 14, 15)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('ff44a530-6455-4ce6-8e3d-241193a0993e', 'INCOME_CHARGE', 'INCOME_CHARGE', '', 'string', 16.83, 16)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('372f8a23-1ede-414c-b0db-96e3cb4bdaf3', 'INCOME_UNAPPLIED', 'INCOME_UNAPPLIED', '', 'string', 19.83, 17)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width)
VALUES ('768e8b68-2e43-4762-a1aa-61c314dbc2a3', 'DATE_FIELD', 'PROCESSING_DATE', 'dd-mmm-yy', 'date', 15.0)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('bed52e96-2bfa-4e70-b2ca-54f6107d9133', 'MIGRATED_CASE', 'MIGRATED_CASE', '', 'string', 16.16, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('8464d96e-550a-4e9a-b495-21193bdd7a65', 'MIGRATED_FLAG', 'MIGRATED_FLAG', '', 'string', 16.33, 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('1ea166a2-d49b-4f1e-9040-437ded694cfc', 'DATE_MIGRATED', 'DATE_MIGRATED', 'dd-mmm-yy', 'date', 16.33, 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('db11ec21-1c88-4653-b00f-bdc2011e1b65', 'CASE_REFERENCE', 'CASE_REFERENCE', '', 'string', 23.5, 4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('3b91824d-9a24-4878-a519-1bdb2983e6fb', 'CCMS_CASE_ID', 'CCMS_CASE_ID', '', 'string', 14.5, 5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('f97bbbea-d1b3-445a-a6a6-76028554e7c7', 'FULL_CASE', 'FULL_CASE', '', 'string', 10.5, 6)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d84741fc-ed99-4d74-b46a-4ad87b6aa967', 'CIS_CASE_NUMBER', 'CIS_CASE_NUMBER', '', 'string', 24.16, 7)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('838fdce9-1075-493b-9dad-d43f82eac50a', 'DISCH_REV_REASON', 'DISCH_REV_REASON', '', 'string', 19.33, 8)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('2a4cade0-a7c1-4cc4-97ef-977b2eac4593', 'DISCH_REV_DATE', 'DISCH_REV_DATE', 'dd-mmm-yy', 'date', 16.5, 9)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('8173f654-5047-4860-aed3-f20dcb0058ea', 'AGE_IN_DAYS', 'AGE_IN_DAYS', '', 'string', 13.5, 10)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('05d83937-a638-413f-86c0-78a962d7b997', 'CAT_CODE', 'CAT_CODE', '', 'string', 10.33, 11)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('c14410ec-762b-4672-aad6-cd487c45308f', 'COSTS_AWARD_DATE', 'COSTS_AWARD_DATE', '', 'string', 20.5, 12)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('ad2096f9-d662-44c9-b5a3-cf88b1794e0a', 'CONTRIBS_SEC', 'CONTRIBS_SEC', 'dd-mmm-yy', 'date', 14.16, 13)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('c461d067-9b6f-4482-bb57-ef8bb3f5e6a5', 'COSTS_SEC', 'COSTS_SEC', '', 'string', 10.66, 14)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('1cd7f36b-e5f1-4962-a510-a0603f307c21', 'DAMAGES_SEC', 'DAMAGES_SEC', '', 'string', 14.5, 15)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('eb477bde-a29e-4859-9614-1039703bdeb3', 'REVOCATION_SEC', 'REVOCATION_SEC', '', 'string', 17, 16)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d561bfff-2c56-40a8-91b9-c23545e6d52d', 'STATCHG_SEC', 'STATCHG_SEC', '', 'string', 13.5, 17)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('6f95d898-007a-492e-bc9b-7fdd6ee678e9', 'CONTRIBS_DEBT', 'CONTRIBS_DEBT', '', 'string', 15.5, 18)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('fe371c6b-bbfa-4014-a748-9a3a4a247b2e', 'CONTRIBS_ADJ_TOTAL', 'CONTRIBS_ADJ_TOTAL', '', 'string', 21, 19)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('2cffe449-eee8-4004-bfbf-bc405445fa7b', 'CONTRIBS_ADJ_CHARGES', 'CONTRIBS_ADJ_CHARGES', '', 'string', 23.83, 20)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('7133b724-a7ad-4efc-969f-62fcc31408c5', 'CONTRIBS_ADJ_DEBT', 'CONTRIBS_ADJ_DEBT', '', 'string', 19.83, 21)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('30602b6c-94e7-46f3-bf9d-2c0467f00ece', 'CONTRIBS_ADJ_DEBT_GRE', 'CONTRIBS_ADJ_DEBT_GRE', '', 'string', 24.5, 22)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('026fce56-9961-4a5e-af12-74138f74d965', 'CONTRIBS_ADJ_DEBT_LOW', 'CONTRIBS_ADJ_DEBT_LOW', '', 'string', 25.33, 23)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('1af8d033-81c6-4a0d-b191-ebfd546ecebe', 'CONTRIBS_WO_TOTAL', 'CONTRIBS_WO_TOTAL', '', 'string', 21.16, 24)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('4da1bdeb-9cde-4d89-9c18-8c817e2209b6', 'CONTRIBS_WO_CHARGES', 'CONTRIBS_WO_CHARGES', '', 'string', 24, 25)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('8774454d-7b22-439a-a5e3-852a3579e20f', 'CONTRIBS_WO_DEBT', 'CONTRIBS_WO_DEBT', '', 'string', 20, 26)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('2e6ef373-aac0-418f-a9c1-99b54d0deac3', 'CONTRIBS_WD_TOTAL', 'CONTRIBS_WD_TOTAL', '', 'string', 21, 27)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('92f88b28-e28c-4078-a2bf-ceb89376a038', 'CONTRIBS_WD_CHARGES', 'CONTRIBS_WD_CHARGES', '', 'string', 23.83, 28)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('5aa766ca-3d82-4481-9c44-35a947d17bc3', 'CONTRIBS_WD_DEBT', 'CONTRIBS_WD_DEBT', '', 'string', 19.83, 29)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('9f2cdd19-69c5-4001-880c-048cab2d50cb', 'CONTRIBS_REC_TOTAL', 'CONTRIBS_REC_TOTAL', '', 'string', 21, 30)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('7d861358-54b6-4b81-ada2-7d6248209099', 'CONTRIBS_RECEIPTS_CHARGES', 'CONTRIBS_RECEIPTS_CHARGES', '', 'string', 28.66, 31)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('01af3928-a274-40be-ab25-543dc0f99376', 'CONTRIBS_RECEIPTS_DEBT', 'CONTRIBS_RECEIPTS_DEBT', '', 'string', 24.66, 32)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('b4886634-f7a8-4341-8e05-b199e9b31173', 'CONTRIBS_CM_TOTAL', 'CONTRIBS_CM_TOTAL', '', 'string', 20.66, 33)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('aeebf4de-0a18-4b0b-a222-3c2e0ab655eb', 'CONTRIBS_CM_CHARGES', 'CONTRIBS_CM_CHARGES', '', 'string', 23.5, 34)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('2a1cb6a7-8313-4b24-a12a-9c4caf8549e0', 'CONTRIBS_CM_DEBT', 'CONTRIBS_CM_DEBT', '', 'string', 19.5, 35)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('2995fe3d-69ce-4cae-99cf-5a14a3418603', 'COSTS_DEBT', 'COSTS_DEBT', '', 'string', 12, 36)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('70fa5549-8c47-4983-bb05-c19ae95695c4', 'COSTS_ADJ_TOTAL', 'COSTS_ADJ_TOTAL', '', 'string', 17.66, 37)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('970ea1bb-2090-40f8-a380-6fe7817efa0a', 'COSTS_ADJ_CHARGES', 'COSTS_ADJ_CHARGES', '', 'string', 20.5, 38)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('9e142631-2055-4a6a-bf92-5700e5f5367e', 'COSTS_ADJ_DEBT', 'COSTS_ADJ_DEBT', '', 'string', 16.5, 39)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('de6cc62d-cb92-4ec6-81c7-2e597ff8afe3', 'COSTS_ADJ_DEBT_GRE', 'COSTS_ADJ_DEBT_GRE', '', 'string', 21.16, 40)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('7649f604-db4a-49f1-9ea9-7e7ec87d9856', 'COSTS_ADJ_DEBT_LOW', 'COSTS_ADJ_DEBT_LOW', '', 'string', 22, 41)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('8bb9afa9-47c7-4e83-91db-85c7da17a946', 'COSTS_WO_TOTAL', 'COSTS_WO_TOTAL', '', 'string', 17.83, 42)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('4d9f1745-8cb6-403a-9ad1-2de2d73777eb', 'COSTS_WO_CHARGES', 'COSTS_WO_CHARGES', '', 'string', 20.5, 43)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('84738f05-efb6-4658-81d2-2282b8227cb4', 'COSTS_WO_DEBT', 'COSTS_WO_DEBT', '', 'string', 16.5, 44)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('383f1a9f-091f-43cf-ad25-c9e3b5d7dc42', 'COSTS_WD_TOTAL', 'COSTS_WD_TOTAL', '', 'string', 17.66, 45)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('e4e0ce28-569e-4c72-8490-2bcc25169f1f', 'COSTS_WD_CHARGES', 'COSTS_WD_CHARGES', '', 'string', 20.5, 46)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('6c25b145-e462-4d71-b691-2fc98196e54c', 'COSTS_WD_DEBT', 'COSTS_WD_DEBT', '', 'string', 16.5, 47)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('4b3afe3b-96f8-47a8-b47f-17dd05ff3d5a', 'COSTS_REC_TOTAL', 'COSTS_REC_TOTAL', '', 'string', 17.66, 48)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('f92eaef9-7fa4-4261-861f-44c87aad7aa3', 'COSTS_RECEIPTS_CHARGES', 'COSTS_RECEIPTS_CHARGES', '', 'string', 25.33, 49)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('b5951076-a15a-4a3d-8ced-73f0c23f0ba3', 'COSTS_RECEIPTS_DEBT', 'COSTS_RECEIPTS_DEBT', '', 'string', 21.5, 50)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('7cf9df39-e4fe-40f7-8001-d2b65e4c7d78', 'COSTS_CM_TOTAL', 'COSTS_CM_TOTAL', '', 'string', 17.5, 51)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('1fc95546-56bf-4d50-8efb-66266bc843ac', 'COSTS_CM_CHARGES', 'COSTS_CM_CHARGES', '', 'string', 20.16, 52)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('b91fa3f9-34ab-4710-840b-c11d7e53a914', 'COSTS_CM_DEBT', 'COSTS_CM_DEBT', '', 'string', 16.16, 53)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('f4830786-d0cf-4d17-b837-b702f0e700b9', 'AWARD_AMOUNT_OR_VALUATION', 'AWARD_AMOUNT_OR_VALUATION', '', 'string', 33.33, 54)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('0cc2adf2-7539-49cb-bfe0-d4dd5858f830', 'COST_AWARD_PRECERT_LEGAL_HELP', 'COST_AWARD_PRECERT_LEGAL_HELP', '', 'string', 34.66, 55)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('f3f6bb9d-9371-4013-914c-f9490daeb975', 'COST_AWARD_PRECERT_PRIVATE', 'COST_AWARD_PRECERT_PRIVATE', '', 'string', 31.33, 56)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('5b372441-0673-46bd-8406-9b9218b8f232', 'COST_AWARD_LSC_RATE_AMOUNT', 'COST_AWARD_LSC_RATE_AMOUNT', '', 'string', 33.16, 57)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('df568911-213f-4ebe-84fc-a7d2c582344c', 'COST_AWARD_MARKET_RATE_AMOUNT', 'COST_AWARD_MARKET_RATE_AMOUNT', '', 'string', 37.66, 58)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('0aa01cc5-98b9-4693-957f-8acfad1ad0e7', 'DAMAGES_DEBT', 'DAMAGES_DEBT', '', 'string', 15.66, 59)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('3eec884e-104a-4043-81d5-7dffe8bd492f', 'DAMAGES_ADJ_TOTAL', 'DAMAGES_ADJ_TOTAL', '', 'string', 21.5, 60)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('bfed1eb6-2489-4a4c-89a2-19141b27054d', 'DAMAGES_ADJ_CHARGES', 'DAMAGES_ADJ_CHARGES', '', 'string', 24.16, 61)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('06536659-2e2f-4d57-b710-892ed75fd638', 'DAMAGES_ADJ_DEBT', 'DAMAGES_ADJ_DEBT', '', 'string', 20.16, 62)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('1b8e1e29-6520-405b-88f7-18402e9cfe58', 'DAMAGES_ADJ_DEBT_GRE', 'DAMAGES_ADJ_DEBT_GRE', '', 'string', 24.83, 63)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('f23383ba-a54b-47bd-a4a3-f3e5c58e0089', 'DAMAGES_ADJ_DEBT_LOW', 'DAMAGES_ADJ_DEBT_LOW', '', 'string', 25.66, 64)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('01137fde-d753-406e-b365-e14e93fd601b', 'DAMAGES_WO_TOTAL', 'DAMAGES_WO_TOTAL', '', 'string', 21.5, 65)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('7c790692-2bb9-460a-8bee-635b5666bcdf', 'DAMAGES_WO_CHARGES', 'DAMAGES_WO_CHARGES', '', 'string', 24.33, 66)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('1b70b85b-5cb2-4f6a-81c5-60e336355414', 'DAMAGES_WO_DEBT', 'DAMAGES_WO_DEBT', '', 'string', 20.33, 67)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('0b25cc2a-930f-4246-bab5-80b47769b155', 'DAMAGES_WD_TOTAL', 'DAMAGES_WD_TOTAL', '', 'string', 21.5, 68)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('ec9449d4-d634-4c0e-8709-c8577252f49f', 'DAMAGES_WD_CHARGES', 'DAMAGES_WD_CHARGES', '', 'string', 24.16, 69)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('736bf47d-7511-4cba-9b3b-5b86e50aff78', 'DAMAGES_WD_DEBT', 'DAMAGES_WD_DEBT', '', 'string', 20.16, 70)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('dc2e3b89-4120-4df0-8235-e59b1b61042b', 'DAMAGES_REC_TOTAL', 'DAMAGES_REC_TOTAL', '', 'string', 21.5, 71)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('43364b6d-6783-408a-a67d-44471b224485', 'DAMAGES_RECEIPTS_CHARGES', 'DAMAGES_RECEIPTS_CHARGES', '', 'string', 29, 72)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('124b8b23-7082-46f7-a446-ba19922cd11f', 'DAMAGES_RECEIPTS_DEBT', 'DAMAGES_RECEIPTS_DEBT', '', 'string', 25, 73)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('ab3c1ba1-beee-4180-8b81-e8df9ce3ff41', 'DAMAGES_CM_TOTAL', 'DAMAGES_CM_TOTAL', '', 'string', 20, 74)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('64810e34-41bd-4b53-a1e4-85da435a1dbf', 'DAMAGES_CM_CHARGES', 'DAMAGES_CM_CHARGES', '', 'string', 23.83, 75)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('781c0f7f-bcef-4b94-83db-a447e02c4639', 'DAMAGES_CM_DEBT', 'DAMAGES_CM_DEBT', '', 'string', 19.83, 76)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('edc69e38-2ecf-47af-84ef-7a1f9f924c34', 'REVOCATION_DEBT', 'REVOCATION_DEBT', '', 'string', 18.5, 77)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d5ba0f2f-18ec-4ccf-8f3a-f1e7c04a639a', 'REVOCATION_ADJ_TOTAL', 'REVOCATION_ADJ_TOTAL', '', 'string', 24, 78)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('a828f328-04d2-4a3b-8306-259fcee2fa41', 'REVOCATION_ADJ_CHARGES', 'REVOCATION_ADJ_CHARGES', '', 'string', 26.83, 79)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('7656d386-1470-40fe-9066-b96e3b9da44b', 'REVOCATION_ADJ_DEBT', 'REVOCATION_ADJ_DEBT', '', 'string', 22.83, 80)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('0f0c124b-1a0e-424f-a61e-47fca7d4feb8', 'REVOCATION_ADJ_DEBT_GRE', 'REVOCATION_ADJ_DEBT_GRE', '', 'string', 27.5, 81)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('4b22f270-c9d1-43f7-bd24-7eaa7f74f0a0', 'REVOCATION_ADJ_DEBT_LOW', 'REVOCATION_ADJ_DEBT_LOW', '', 'string', 28.33, 82)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('569c1a1a-4df5-4c1c-a917-844926a3c552', 'REVOCATION_WO_TOTAL', 'REVOCATION_WO_TOTAL', '', 'string', 24.16, 83)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('f74c57b6-7051-4325-bf4a-5b347621f069', 'REVOCATION_WO_CHARGES', 'REVOCATION_WO_CHARGES', '', 'string', 27, 84)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('9aa68eac-37e2-4714-8d8a-6e97d2a39c01', 'REVOCATION_WO_DEBT', 'REVOCATION_WO_DEBT', '', 'string', 23, 85)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('43edff54-c715-4ddb-8f74-fa0aa1cf5c72', 'REVOCATION_WD_TOTAL', 'REVOCATION_WD_TOTAL', '', 'string', 24, 86)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('4f347084-a011-482f-95c6-2bc9c8f0907b', 'REVOCATION_WD_CHARGES', 'REVOCATION_WD_CHARGES', '', 'string', 26.83, 87)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('b3078f53-d7db-4a1b-971f-85cfc9d3c371', 'REVOCATION_WD_DEBT', 'REVOCATION_WD_DEBT', '', 'string', 22.83, 88)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('f26ced0e-d6a0-427a-9684-f5616662c71f', 'REVOCATION_REC_TOTAL', 'REVOCATION_REC_TOTAL', '', 'string', 24, 89)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('8308bd9f-c61b-40ce-a968-2e215ca871e3', 'REVOCATION_RECEIPTS_CHARGES', 'REVOCATION_RECEIPTS_CHARGES', '', 'string', 31.66, 90)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('19220b83-ddf9-4e78-a203-ea57e63e5c7b', 'REVOCATION_RECEIPTS_DEBT', 'REVOCATION_RECEIPTS_DEBT', '', 'string', 27.66, 91)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('7c402ea1-4ffc-419f-88e7-3db235d89c42', 'REVOCATION_CM_TOTAL', 'REVOCATION_CM_TOTAL', '', 'string', 23.33, 92)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('a55aa31d-ccf0-44ae-9b64-fd414329e143', 'REVOCATION_CM_CHARGES', 'REVOCATION_CM_CHARGES', '', 'string', 26, 93)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('faa94efd-7093-48c7-b988-ec50e17ca5a9', 'REVOCATION_CM_DEBT', 'REVOCATION_CM_DEBT', '', 'string', 22, 94)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('2ce3b890-677d-42b4-9ebf-7e51d6c63f2b', 'STATCHG_DEBT', 'STATCHG_DEBT', '', 'string', 14.66, 95)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('a70f1665-02be-41a0-b46d-32869d1aa5c8', 'STATCHG_ADJ_TOTAL', 'STATCHG_ADJ_TOTAL', '', 'string', 20.33, 96)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d094e0c3-1bcf-4ef0-97f8-e7e47b8100e9', 'STATCHG_ADJ_CHARGES', 'STATCHG_ADJ_CHARGES', '', 'string', 23.16, 97)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('357ea643-928d-438c-9c36-18161758ed90', 'STATCHG_ADJ_DEBT', 'STATCHG_ADJ_DEBT', '', 'string', 19.16, 98)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('26423183-283d-4bfc-83b7-9ab7480853cc', 'STATCHG_ADJ_DEBT_GRE', 'STATCHG_ADJ_DEBT_GRE', '', 'string', 23.83, 99)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d476a353-0b4e-47e9-a088-4e74ace61123', 'STATCHG_ADJ_DEBT_LOW', 'STATCHG_ADJ_DEBT_LOW', '', 'string', 24.5, 100)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('5dd4c32a-ee99-427d-b756-7903eb652b18', 'STATCHG_WO_TOTAL', 'STATCHG_WO_TOTAL', '', 'string', 20.5, 101)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('041074be-329e-4225-90b1-3dda9cbb682b', 'STATCHG_WO_CHARGES', 'STATCHG_WO_CHARGES', '', 'string', 23.33, 102)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('49ecc134-47c2-4297-b9dd-89ef7c90b870', 'STATCHG_WO_DEBT', 'STATCHG_WO_DEBT', '', 'string', 19.33, 103)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('3b1de474-b9e2-47d4-b888-e710685461ec', 'STATCHG_WD_TOTAL', 'STATCHG_WD_TOTAL', '', 'string', 20.33, 104)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('83d27951-d47e-4e45-813f-5223ec59a7f6', 'STATCHG_WD_CHARGES', 'STATCHG_WD_CHARGES', '', 'string', 23.16, 105)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('9ca13532-74de-437d-9a11-9243dafb9df8', 'STATCHG_WD_DEBT', 'STATCHG_WD_DEBT', '', 'string', 19.16, 106)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('6227286e-101c-43f2-bd47-21e65e0b08fe', 'STATCHG_REC_TOTAL', 'STATCHG_REC_TOTAL', '', 'string', 20.33, 107)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('35d003d7-b795-49c3-9634-5a37989c8b3d', 'STATCHG_RECEIPTS_CHARGES', 'STATCHG_RECEIPTS_CHARGES', '', 'string', 28, 108)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('30fcfd4c-8616-4992-8632-0a746d0527bc', 'STATCHG_RECEIPTS_DEBT', 'STATCHG_RECEIPTS_DEBT', '', 'string', 24, 109)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('a75e5f75-e8aa-48a4-9ed9-d4a713e21d6c', 'STATCHG_CM_TOTAL', 'STATCHG_CM_TOTAL', '', 'string', 20, 110)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('c61038f3-fcc2-477e-8f67-039fe339a5ff', 'STATCHG_CM_CHARGES', 'STATCHG_CM_CHARGES', '', 'string', 22.83, 111)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('9e62e065-aab7-44be-a2b5-407b400a1cc2', 'STATCHG_CM_DEBT', 'STATCHG_CM_DEBT', '', 'string', 18.83, 112)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('0936c5df-42ee-4f76-ae18-3f0a5f0a80ca', 'STATCHG_LAST_INT_DATE', 'STATCHG_LAST_INT_DATE', 'dd-mmm-yy', 'date', 24.33, 113)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('c1efeb9a-ce8d-40a9-9b4a-d66c21323576', 'FINAL_BILL_DATE', 'FINAL_BILL_DATE', 'dd-mmm-yy', 'date', 16.33, 114)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('ceb98c53-3208-4181-acea-46b08c07f165', 'TOTAL_RECOVERABLE', 'TOTAL_RECOVERABLE', '', 'string', 20.33, 115)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('45cba23c-68c6-4a6b-af3c-5cfe417283e6', 'LEGAL_HELP', 'LEGAL_HELP', '', 'string', 11.66, 116)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('79dab938-adf6-4835-b173-2cf87f6ea227', 'LAA_CONTRIBS_PRIN_DEBT', 'LAA_CONTRIBS_PRIN_DEBT', '', 'string', 25.5, 117)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('34ada203-fa2a-4c14-9009-bac1b234af36', 'LAA_CONTRIBS_PRIN_ADJ', 'LAA_CONTRIBS_PRIN_ADJ', '', 'string', 24.33, 118)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('c3ffd85d-8b2b-405c-8fab-3912a26c6953', 'LAA_CONTRIBS_PRIN_ADJ_LOW', 'LAA_CONTRIBS_PRIN_ADJ_LOW', '', 'string', 29.83, 119)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('ec52cc7a-34d1-4b03-b8aa-c26ab85528c7', 'LAA_CONTRIBS_PRIN_REC', 'LAA_CONTRIBS_PRIN_REC', '', 'string', 24.33, 120)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('c7a4969f-0980-45fa-995d-f3e371fe96be', 'LAA_CONTRIBS_PRIN_CM', 'LAA_CONTRIBS_PRIN_CM', '', 'string', 24, 121)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('716dc4fe-c4b3-45ef-8825-0010932ba31c', 'LAA_CONTRIBS_PRIN_WO', 'LAA_CONTRIBS_PRIN_WO', '', 'string', 24.5, 122)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('63d5c59a-88e0-4f5a-87e7-b2ed36e76fd6', 'LAA_CONTRIBS_PRIN_WD', 'LAA_CONTRIBS_PRIN_WD', '', 'string', 24.33, 123)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('29bbdcb6-8539-4ab6-b43a-516b93dea7f9', 'LAA_COSTS_PRIN_DEBT', 'LAA_COSTS_PRIN_DEBT', '', 'string', 22, 124)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('1dac9a8f-0157-4321-863a-b213c8475cd1', 'LAA_COSTS_PRIN_ADJ', 'LAA_COSTS_PRIN_ADJ', '', 'string', 20.83, 125)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('b0ed49b8-f694-43a8-b422-5746464171a7', 'LAA_COSTS_PRIN_ADJ_LOW', 'LAA_COSTS_PRIN_ADJ_LOW', '', 'string', 26.5, 126)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('dc323041-58c3-4a5c-89ba-8a844549ebe6', 'LAA_COSTS_PRIN_REC', 'LAA_COSTS_PRIN_REC', '', 'string', 20.83, 127)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('812b7628-785b-483b-ac93-f23684356f11', 'LAA_COSTS_PRIN_CM', 'LAA_COSTS_PRIN_CM', '', 'string', 20.5, 128)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('ac6240dd-aa24-43c0-b169-e9de98a67b77', 'LAA_COSTS_PRIN_WO', 'LAA_COSTS_PRIN_WO', '', 'string', 21, 129)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('b9d73879-5676-4ae0-81eb-eb5b3bdc1598', 'LAA_COSTS_PRIN_WD', 'LAA_COSTS_PRIN_WD', '', 'string', 20.83, 130)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d411defc-2b79-42fe-92a3-51ef3ee864a8', 'LAA_DAMAGES_PRIN_DEBT', 'LAA_DAMAGES_PRIN_DEBT', '', 'string', 25.83, 131)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d79fd1bb-5075-46ee-8d7a-eae6da09d508', 'LAA_DAMAGES_PRIN_ADJ', 'LAA_DAMAGES_PRIN_ADJ', '', 'string', 24.5, 132)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('092c1d56-db79-4169-8751-7ebeb420d532', 'LAA_DAMAGES_PRIN_ADJ_LOW', 'LAA_DAMAGES_PRIN_ADJ_LOW', '', 'string', 30.16, 133)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('cd1f7fff-752b-4838-a12b-dcfc95d8357b', 'LAA_DAMAGES_PRIN_REC', 'LAA_DAMAGES_PRIN_REC', '', 'string', 24.5, 134)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('703f7ba3-f37d-47c2-bd28-4d7e884cda86', 'LAA_DAMAGES_PRIN_CM', 'LAA_DAMAGES_PRIN_CM', '', 'string', 24.33, 135)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('1611e33a-fe6f-4ac4-9d19-33713ef57378', 'LAA_DAMAGES_PRIN_WO', 'LAA_DAMAGES_PRIN_WO', '', 'string', 24.66, 136)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('009de6be-265e-45cd-854b-e76e27a3101b', 'LAA_DAMAGES_PRIN_WD', 'LAA_DAMAGES_PRIN_WD', '', 'string', 24.5, 137)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('41302d84-a256-4347-b431-24cdc91fefef', 'LAA_REVOKE_PRIN_DEBT', 'LAA_REVOKE_PRIN_DEBT', '', 'string', 23.5, 138)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('3d5ed581-a22d-4902-95b0-3b047fc8204c', 'LAA_REVOKE_PRIN_ADJ', 'LAA_REVOKE_PRIN_ADJ', '', 'string', 22.5, 139)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('c0794093-c661-4ce5-88ff-047f9e54b072', 'LAA_REVOKE_PRIN_ADJ_LOW', 'LAA_REVOKE_PRIN_ADJ_LOW', '', 'string', 27.83, 140)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('a66a85fa-6053-44b4-8f18-19ca1e9331a0', 'LAA_REVOKE_PRIN_REC', 'LAA_REVOKE_PRIN_REC', '', 'string', 22.5, 141)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('b38bcf4a-481a-41ce-a5f6-d482b2df25c7', 'LAA_REVOKE_PRIN_CM', 'LAA_REVOKE_PRIN_CM', '', 'string', 22, 142)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('ec532f66-8bf6-44f5-8feb-53daf18730f5', 'LAA_REVOKE_PRIN_WO', 'LAA_REVOKE_PRIN_WO', '', 'string', 22.5, 143)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('a4ad11ca-f7e3-4dc8-9189-fbf81543e444', 'LAA_REVOKE_PRIN_WD', 'LAA_REVOKE_PRIN_WD', '', 'string', 22.5, 144)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('aa8bfbcb-c143-462a-8414-23437a709189', 'LAA_STATCHG_PRIN_DEBT', 'LAA_STATCHG_PRIN_DEBT', '', 'string', 24.66, 145)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d91f7f2e-5745-4379-8a74-5433e833abce', 'LAA_STATCHG_PRIN_ADJ', 'LAA_STATCHG_PRIN_ADJ', '', 'string', 23.5, 146)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('bbf79377-0a69-4bd7-94b6-fca38c7bbf51', 'LAA_STATCHG_PRIN_ADJ_LOW', 'LAA_STATCHG_PRIN_ADJ_LOW', '', 'string', 29, 147)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('663cb92a-3b87-4569-813b-101025b5d497', 'LAA_STATCHG_PRIN_REC', 'LAA_STATCHG_PRIN_REC', '', 'string', 23.5, 148)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('fc64fd12-519b-44f0-ac20-8effbdbbde83', 'LAA_STATCHG_PRIN_CM', 'LAA_STATCHG_PRIN_CM', '', 'string', 23.33, 149)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d0e09737-945e-41c1-be74-d74060e11122', 'LAA_STATCHG_PRIN_WO', 'LAA_STATCHG_PRIN_WO', '', 'string', 23.66, 150)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('3763ff4c-5f71-49e3-839d-b1c4fad63654', 'LAA_STATCHG_PRIN_WD', 'LAA_STATCHG_PRIN_WD', '', 'string', 23.5, 151)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('5506c0f4-9e2e-41f9-aab8-a07eac70a38f', 'THIRD_CONTRIBS_PRIN_DEBT', 'THIRD_CONTRIBS_PRIN_DEBT', '', 'string', 27.5, 152)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d60bd99f-6238-4478-a0db-179cbb6afbfd', 'THIRD_CONTRIBS_PRIN_ADJ', 'THIRD_CONTRIBS_PRIN_ADJ', '', 'string', 26.33, 153)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('a83967aa-c22a-444c-bec4-4db9d0c0e3fc', 'THIRD_CONTRIBS_PRIN_ADJ_LOW', 'THIRD_CONTRIBS_PRIN_ADJ_LOW', '', 'string', 31.66, 154)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('2ddf32a1-e668-4c0b-ba24-c1133aec1fb8', 'THIRD_CONTRIBS_PRIN_REC', 'THIRD_CONTRIBS_PRIN_REC', '', 'string', 26.33, 155)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('12d508b9-7b59-4e89-811c-51b48d3b1b5f', 'THIRD_CONTRIBS_PRIN_CM', 'THIRD_CONTRIBS_PRIN_CM', '', 'string', 26, 156)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('14dc3331-76d8-4b87-a74d-a6dc6d483eb2', 'THIRD_CONTRIBS_PRIN_WO', 'THIRD_CONTRIBS_PRIN_WO', '', 'string', 26.5, 157)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('028c2f3e-c906-49b3-a5e5-9e9e5818ad99', 'THIRD_CONTRIBS_PRIN_WD', 'THIRD_CONTRIBS_PRIN_WD', '', 'string', 26.33, 158)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('87f38b62-b222-4d96-adc9-f0a73502697e', 'THIRD_COSTS_PRIN_DEBT', 'THIRD_COSTS_PRIN_DEBT', '', 'string', 24, 159)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('ddc5dcb8-7ef0-40fc-86d8-a2b374b37c6b', 'THIRD_COSTS_PRIN_ADJ', 'THIRD_COSTS_PRIN_ADJ', '', 'string', 22.83, 160)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('84ce1ebf-1ac1-4bf1-af07-493701a5d4a2', 'THIRD_COSTS_PRIN_ADJ_LOW', 'THIRD_COSTS_PRIN_ADJ_LOW', '', 'string', 28.33, 161)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('bd4551e8-b79d-4c66-b3fb-8f5a57d39f3e', 'THIRD_COSTS_PRIN_REC', 'THIRD_COSTS_PRIN_REC', '', 'string', 22.83, 162)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d8be172b-ca44-40cb-b869-4d1888863fca', 'THIRD_COSTS_PRIN_CM', 'THIRD_COSTS_PRIN_CM', '', 'string', 22.5, 163)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('aef84b39-b3c5-4019-acd6-05031a545099', 'THIRD_COSTS_PRIN_WO', 'THIRD_COSTS_PRIN_WO', '', 'string', 23, 164)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('242fd332-3f00-4624-a56b-6e159a6c93ca', 'THIRD_COSTS_PRIN_WD', 'THIRD_COSTS_PRIN_WD', '', 'string', 22.83, 165)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('8da0f0f5-3f63-4eb7-9f97-abe0f50111c3', 'THIRD_DAMAGES_PRIN_DEBT', 'THIRD_DAMAGES_PRIN_DEBT', '', 'string', 23, 166)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('333bedb0-3242-4365-bcc1-e986e6e768e5', 'THIRD_DAMAGES_PRIN_ADJ', 'THIRD_DAMAGES_PRIN_ADJ', '', 'string', 27.5, 167)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('e9af4aac-0a18-4c1b-b47a-f82dbd0db5b0', 'THIRD_DAMAGES_PRIN_ADJ_LOW', 'THIRD_DAMAGES_PRIN_ADJ_LOW', '', 'string', 32, 168)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('0798f4e5-b48f-4a02-b96c-6dd5a11d10d2', 'THIRD_DAMAGES_PRIN_REC', 'THIRD_DAMAGES_PRIN_REC', '', 'string', 26.5, 169)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('99d7af54-424f-4ace-85b7-8f883fce0e89', 'THIRD_DAMAGES_PRIN_CM', 'THIRD_DAMAGES_PRIN_CM', '', 'string', 26.33, 170)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('37c62708-4c7f-4207-a282-46af0598c5f5', 'THIRD_DAMAGES_PRIN_WO', 'THIRD_DAMAGES_PRIN_WO', '', 'string', 26.66, 171)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('dd44b7c3-1c1e-403c-b4d6-c0d43c9a8353', 'THIRD_DAMAGES_PRIN_WD', 'THIRD_DAMAGES_PRIN_WD', '', 'string', 26.5, 172)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('ab3076b6-482b-4002-b2da-1ac91bbbca11', 'THIRD_REVOKE_PRIN_DEBT', 'THIRD_REVOKE_PRIN_DEBT', '', 'string', 25.5, 173)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('0921f5e3-a0f3-4568-b69d-c0bed7b26460', 'THIRD_REVOKE_PRIN_ADJ', 'THIRD_REVOKE_PRIN_ADJ', '', 'string', 24.33, 174)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('bb5c3bba-d1b2-4c25-b03e-e34d9ae90a47', 'THIRD_REVOKE_PRIN_ADJ_LOW', 'THIRD_REVOKE_PRIN_ADJ_LOW', '', 'string', 29.83, 175)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('e864ac7a-c6d2-4812-ad25-8af5beb2b325', 'THIRD_REVOKE_PRIN_REC', 'THIRD_REVOKE_PRIN_REC', '', 'string', 24.33, 176)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('e12fcb6f-6995-438c-9d4d-4ea7c6dee278', 'THIRD_REVOKE_PRIN_CM', 'THIRD_REVOKE_PRIN_CM', '', 'string', 24, 177)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('b13dfd3a-3c8e-4a20-acfa-a78282706636', 'THIRD_REVOKE_PRIN_WO', 'THIRD_REVOKE_PRIN_WO', '', 'string', 24.5, 178)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('76be7120-7be2-4202-a101-ea02cb4c4b80', 'THIRD_REVOKE_PRIN_WD', 'THIRD_REVOKE_PRIN_WD', '', 'string', 24.33, 179)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('432b1b88-fcde-48e8-b7b0-6870dff4c0d7', 'THIRD_STATCHG_PRIN_DEBT', 'THIRD_STATCHG_PRIN_DEBT', '', 'string', 26.66, 180)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('eb10b888-85bd-477b-8863-48fdd8052374', 'THIRD_STATCHG_PRIN_ADJ', 'THIRD_STATCHG_PRIN_ADJ', '', 'string', 25.5, 181)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('0da1d1d6-bfe5-489f-b248-12d43a00f5ce', 'THIRD_STATCHG_PRIN_ADJ_LOW', 'THIRD_STATCHG_PRIN_ADJ_LOW', '', 'string', 31, 182)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('0c00c653-3ad0-40ea-a8a5-d9e984ddc78b', 'THIRD_STATCHG_PRIN_REC', 'THIRD_STATCHG_PRIN_REC', '', 'string', 25.5, 183)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('37fef228-f628-484b-abb3-bccbe0daad8b', 'THIRD_STATCHG_PRIN_CM', 'THIRD_STATCHG_PRIN_CM', '', 'string', 25.16, 184)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d802c159-0280-452e-ae7d-220a013382b2', 'THIRD_STATCHG_PRIN_WO', 'THIRD_STATCHG_PRIN_WO', '', 'string', 25.5, 185)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d41fa75c-7ae2-4206-8bce-218c3fe2fafb', 'THIRD_STATCHG_PRIN_WD', 'THIRD_STATCHG_PRIN_WD', '', 'string', 25.33, 186)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('65d0335b-8de6-4354-9d84-80d56e15c130', 'LAA_SIMPLE_REMAINDER', 'LAA_SIMPLE_REMAINDER', '', 'string', 24, 187)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('c5134ed9-3e4f-4e8c-896e-34d1030e7a46', 'CONTRIBS_DEBT_BAL', 'CONTRIBS_DEBT_BAL', '', 'string', 19.83, 188)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('9015aece-9391-4873-972a-1112742304fa', 'COSTS_DEBT_BAL', 'COSTS_DEBT_BAL', '', 'string', 16.5, 189)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('5b6d3a89-4f1f-4b36-8cf5-b18dee0b5c87', 'DAMAGES_DEBT_BAL', 'DAMAGES_DEBT_BAL', '', 'string', 20.16, 190)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('72d3ad7f-c97c-4eb2-88e3-492dfe3bc4c4', 'REVOKE_DEBT_BAL', 'REVOKE_DEBT_BAL', '', 'string', 18, 191)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('dd0e6b45-d4fc-4514-a111-b2ddabd5a913', 'STATCHG_DEBT_BAL', 'STATCHG_DEBT_BAL', '', 'string', 19.16, 192)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('981d7ef5-ae4d-429c-a721-48ed182b5cbf', 'LAA_CONTRIBS_DEBT_BAL', 'LAA_CONTRIBS_DEBT_BAL', '', 'string', 24.5, 193)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('0ce5fc15-f5b6-4847-a9d5-06301594d94f', 'LAA_COSTS_DEBT_BAL', 'LAA_COSTS_DEBT_BAL', '', 'string', 21, 194)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('bd313c5f-4b44-4089-9bf6-efb30f812d19', 'LAA_DAMAGES_DEBT_BAL', 'LAA_DAMAGES_DEBT_BAL', '', 'string', 24.66, 195)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('c6e0d786-3bdc-451f-aa24-9e43cf1b7e4d', 'LAA_REVOKE_DEBT_BAL', 'LAA_REVOKE_DEBT_BAL', '', 'string', 22.5, 196)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('771c4fff-b1ed-4f3d-9a99-2ad1a3d8612b', 'LAA_STATCHG_DEBT_BAL', 'LAA_STATCHG_DEBT_BAL', '', 'string', 23.66, 197)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d52d8939-41b5-44d3-a97d-ebc9998a7214', 'LAA_UNAL_DEBT_BAL', 'LAA_UNAL_DEBT_BAL', '', 'string', 20.5, 198)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('1af6a187-5e99-4136-ac9c-2b699abf67bb', 'THIRD_CONTRIBS_DEBT_BAL', 'THIRD_CONTRIBS_DEBT_BAL', '', 'string', 26.5, 199)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('76d58c2a-bdea-4dac-8ae6-4097b4a63a37', 'THIRD_COSTS_DEBT_BAL', 'THIRD_COSTS_DEBT_BAL', '', 'string', 23, 200)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('20113b3f-db49-4502-b677-d0a1ec34b285', 'THIRD_DAMAGES_DEBT_BAL', 'THIRD_DAMAGES_DEBT_BAL', '', 'string', 26.66, 201)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('6c9489fe-67eb-4cdc-943d-b5b277b6e197', 'THIRD_REVOKE_DEBT_BAL', 'THIRD_REVOKE_DEBT_BAL', '', 'string', 24.5, 202)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('9014dbb4-9cfc-44a0-bb12-55287a6e558c', 'THRID_STATCHG_DEBT_BAL', 'THRID_STATCHG_DEBT_BAL', '', 'string', 25.66, 203)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('92397bc1-3f09-4603-9627-4eeae20d8577', 'FINAL_BILL', 'FINAL_BILL', '', 'string', 10.5, 204)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('3d5b9b5b-5179-40bd-a8d0-208240c787ea', 'CONTRIBS_CHARGE_BAL', 'CONTRIBS_CHARGE_BAL', '', 'string', 22.83, 205)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d48d478b-9855-49f3-af9a-6fe13b57e228', 'COSTS_CHARGE_BAL', 'COSTS_CHARGE_BAL', '', 'string', 19.5, 206)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('90db3366-68c7-4a8e-a4c4-7462dc9c1661', 'DAMAGES_CHARGE_BAL', 'DAMAGES_CHARGE_BAL', '', 'string', 23.16, 207)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('10b7227d-fa8b-4a23-98ea-75cb59e6c3bb', 'REVOKE_CHARGE_BAL', 'REVOKE_CHARGE_BAL', '', 'string', 20.83, 208)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('3c9fefa9-7772-4d78-bc13-928297b543e3', 'STATCHG_CHARGE_BAL', 'STATCHG_CHARGE_BAL', '', 'string', 22.16, 209)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('92fbea8a-7658-4d32-97d6-63a9a2f8d449', 'POSSIBLE_REFUND_DUE', 'POSSIBLE_REFUND_DUE', '', 'string', 22.5, 210)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('e299305a-7b43-44fb-afaf-433c7d87b86b', 'REFUNDS_MADE', 'REFUNDS_MADE', '', 'string', 15.83, 211)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('00450fa3-b687-4c24-ba84-4f8089210daf', 'REFUNDS_THIRD_CONTRIBS', 'REFUNDS_THIRD_CONTRIBS', '', 'string', 26, 212)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('83ce39b0-c276-4eb4-86fa-9976563ca850', 'REFUNDS_THIRD_COSTS', 'REFUNDS_THIRD_COSTS', '', 'string', 22.5, 213)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('1669e7e7-c2e4-4b9f-bad2-a09caadeefaa', 'REFUNDS_THIRD_DAMAGES', 'REFUNDS_THIRD_DAMAGES', '', 'string', 26.33, 214)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('4263707f-7077-4378-a3f0-1997cbed196f', 'REFUNDS_THIRD_REVOKE', 'REFUNDS_THIRD_REVOKE', '', 'string', 24, 215)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('39c43a9a-134a-4ee0-8480-d166ad359be6', 'REFUNDS_THIRD_STATCHG', 'REFUNDS_THIRD_STATCHG', '', 'string', 25.16, 216)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('c794b01c-cb0d-4919-9cb2-546e53c055de', 'REFUNDS_LAA_CONTRIBS', 'REFUNDS_LAA_CONTRIBS', '', 'string', 24, 217)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('5d5de065-f82f-4623-b504-3ab35f958ddb', 'REFUNDS_LAA_CONTRIBS_CHARGES', 'REFUNDS_LAA_CONTRIBS_CHARGES', '', 'string', 33.5, 218)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('e21614db-2d5e-4cce-9a5d-ac4f0073e499', 'REFUNDS_LAA_COSTS', 'REFUNDS_LAA_COSTS', '', 'string', 20.5, 219)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('52bef5b5-02d3-4e26-b2bb-9224408c1630', 'REFUNDS_LAA_COSTS_CHARGES', 'REFUNDS_LAA_COSTS_CHARGES', '', 'string', 30.33, 220)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('353948ae-b73b-4415-a434-5a6fd337d16e', 'REFUNDS_LAA_DAMAGES', 'REFUNDS_LAA_DAMAGES', '', 'string', 24.33, 221)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('593ca192-5edd-49e7-8f19-53e23859d475', 'REFUNDS_LAA_DAMAGES_CHARGES', 'REFUNDS_LAA_DAMAGES_CHARGES', '', 'string', 34, 223)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('81e8b995-f11c-4fc8-bd8d-cdf749f734ec', 'REFUNDS_LAA_REVOKE', 'REFUNDS_LAA_REVOKE', '', 'string', 22.16, 224)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('77cfd32d-4410-4b4d-9eea-eade51b7ecac', 'REFUNDS_LAA_REVOKE_CHARGES', 'REFUNDS_LAA_REVOKE_CHARGES', '', 'string', 31.66, 225)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('43f42daf-6074-4b15-9fe3-a91f3147937f', 'REFUNDS_LAA_STATCHG', 'REFUNDS_LAA_STATCHG', '', 'string', 23.33, 226)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('eb93b3a8-5d23-42fd-bdaa-15a264cf4669', 'REFUNDS_LAA_STATCHG_CHARGES', 'REFUNDS_LAA_STATCHG_CHARGES', '', 'string', 32.83, 227)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('765f3d4b-3c68-4263-9755-dc97124b8136', 'UNEXP_REFUND', 'UNEXP_REFUND', '', 'string', 15.5, 228)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('f6e7754b-343d-41c4-b00d-8963ec6981dc', 'TOTAL_LAA_REFUND', 'TOTAL_LAA_REFUND', '', 'string', 19.5, 229)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('e1263066-59d1-495d-983a-6a9ffc1b1998', 'TOTAL_EXPLAINED_REFUND', 'TOTAL_EXPLAINED_REFUND', '', 'string', 26.16, 230)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('af1c403b-69aa-42bc-9e46-e942b6803fed', 'COSTS_SEC', 'COSTS_SEC', '', 'string', 10.66, 9)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('a840a000-7f3c-496f-b107-9c3c9c88e28e', 'DAMAGES_SEC', 'DAMAGES_SEC', '', 'string', 14.5, 10)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('37d9de10-c026-4388-a6f9-05538e24b903', 'REVOCATION_SEC', 'REVOCATION_SEC', '', 'string', 17, 11)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('b93e5e4d-5c4a-465a-911d-3f5cd522b60e', 'STATCHG_SEC', 'STATCHG_SEC', '', 'string', 13.5, 12)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('00198317-5d53-4ae9-ab5d-88c5ffbb3b3c', 'CONTRIBS_DEBT', 'CONTRIBS_DEBT', '', 'string', 15.5, 13)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('9061bb6a-8b4a-4483-bf7d-3e481b7b79a3', 'CONTRIBS_ADJ_TOTAL', 'CONTRIBS_ADJ_TOTAL', '', 'string', 21, 15)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('a91582a5-9d98-4ab7-82c0-0544c6ef8e91', 'CONTRIBS_ADJ_CHARGES', 'CONTRIBS_ADJ_CHARGES', '', 'string', 23.83, 16)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('9726f6ca-489a-426c-95e8-5fc074c2d370', 'CONTRIBS_ADJ_DEBT', 'CONTRIBS_ADJ_DEBT', '', 'string', 19.83, 17)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d8fe8e33-8988-4286-9c6b-04fcc8c8a61d', 'CONTRIBS_ADJ_DEBT_GRE', 'CONTRIBS_ADJ_DEBT_GRE', '', 'string', 24.5, 18)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('413a3c0f-0607-4c12-be2e-1d0c0ee1468f', 'CONTRIBS_ADJ_DEBT_LOW', 'CONTRIBS_ADJ_DEBT_LOW', '', 'string', 25.33, 19)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('3047fc54-b9d5-4811-a131-64d577ea65f5', 'CONTRIBS_WD_TOTAL', 'CONTRIBS_WD_TOTAL', '', 'string', 21, 20)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('8f99f8f6-6a8d-47a1-b2b6-ca14eacbd31c', 'CONTRIBS_WD_CHARGES', 'CONTRIBS_WD_CHARGES', '', 'string', 23.83, 21)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('1ba046fd-fee6-42b3-b704-3affb98f4661', 'CONTRIBS_WD_DEBT', 'CONTRIBS_WD_DEBT', '', 'string', 19.83, 22)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('371e5004-e003-4691-b345-ef838bd948eb', 'CONTRIBS_WO_TOTAL', 'CONTRIBS_WO_TOTAL', '', 'string', 21.16, 23)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('c7f73f78-9d4a-43c5-9548-5f4c5d7a461e', 'CONTRIBS_WO_CHARGES', 'CONTRIBS_WO_CHARGES', '', 'string', 24, 24)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('e3efd89a-dcc7-4ba8-a61a-79c8affddd52', 'CONTRIBS_WO_DEBT', 'CONTRIBS_WO_DEBT', '', 'string', 20, 25)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('38fa60a5-477f-4fdd-aa49-53cabcc6c72f', 'CONTRIBS_REC_TOTAL', 'CONTRIBS_REC_TOTAL', '', 'string', 21, 26)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('706d26a5-3118-4816-ac14-053aad96d613', 'CONTRIBS_RECEIPTS_CHARGES', 'CONTRIBS_RECEIPTS_CHARGES', '', 'string', 28.66, 27)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('0cfcaedc-62cf-48f2-bb12-217a4a3f1b67', 'CONTRIBS_RECEIPTS_DEBT', 'CONTRIBS_RECEIPTS_DEBT', '', 'string', 24.66, 28)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('4f0df095-6891-4f6e-910a-95c94ee01fb2', 'CONTRIBS_CM_TOTAL', 'CONTRIBS_CM_TOTAL', '', 'string', 20.66, 29)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('1e87e7e8-2c58-4136-97e0-8ae78576bc4e', 'CONTRIBS_CM_CHARGES', 'CONTRIBS_CM_CHARGES', '', 'string', 23.5, 30)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('b284bc91-01e6-41ee-b7ab-cdc8bc3737dc', 'CONTRIBS_CM_DEBT', 'CONTRIBS_CM_DEBT', '', 'string', 19.5, 31)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('7b680fb8-05b2-4516-9f7a-cf4cf9728c46', 'COSTS_DEBT', 'COSTS_DEBT', '', 'string', 12, 32)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('3a1f4a19-04d1-4dcb-83ea-419717fd608d', 'COSTS_ADJ_TOTAL', 'COSTS_ADJ_TOTAL', '', 'string', 17.66, 33)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('14ecaad8-5d46-41c7-9673-22ac8e42f542', 'COSTS_ADJ_CHARGES', 'COSTS_ADJ_CHARGES', '', 'string', 20.5, 34)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('26871d53-07c5-4d49-8ea7-4b180fbde724', 'COSTS_ADJ_DEBT', 'COSTS_ADJ_DEBT', '', 'string', 16.5, 35)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('168d8de5-afc6-4d4c-8581-eb9a4c4d50b5', 'COSTS_ADJ_DEBT_GRE', 'COSTS_ADJ_DEBT_GRE', '', 'string', 21.16, 36)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('5c9ddb21-da81-473a-a36c-17e7c18241da', 'COSTS_ADJ_DEBT_LOW', 'COSTS_ADJ_DEBT_LOW', '', 'string', 22, 37)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('72b44f69-9640-41a7-9f5d-6b8104c214cd', 'COSTS_WD_TOTAL', 'COSTS_WD_TOTAL', '', 'string', 17.66, 38)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d7887f80-e1ff-4a0b-8e99-aec024a5f40b', 'COSTS_WD_CHARGES', 'COSTS_WD_CHARGES', '', 'string', 20.5, 39)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('08e61160-ab9f-413b-8ccf-d73075ac54f3', 'COSTS_WD_DEBT', 'COSTS_WD_DEBT', '', 'string', 16.5, 40)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('64ffd66f-e211-400e-856c-18f246c0aa5b', 'COSTS_WO_TOTAL', 'COSTS_WO_TOTAL', '', 'string', 17.83, 41)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('3d7237a1-aab4-4245-acd6-a90f599cd9e7', 'COSTS_WO_CHARGES', 'COSTS_WO_CHARGES', '', 'string', 20.5, 42)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d59d34c8-d629-470e-8cb3-0270f8acdc44', 'COSTS_WO_DEBT', 'COSTS_WO_DEBT', '', 'string', 16.5, 43)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('bf0429b6-e379-4065-8da8-d8b5efd47aa3', 'COSTS_REC_TOTAL', 'COSTS_REC_TOTAL', '', 'string', 17.66, 44)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('797ffa40-dc66-4bdf-9bba-945cd2944e46', 'COSTS_RECEIPTS_CHARGES', 'COSTS_RECEIPTS_CHARGES', '', 'string', 25.33, 45)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('14f4af0e-69bf-4c7b-a5f6-6c03c5b98d81', 'COSTS_RECEIPTS_DEBT', 'COSTS_RECEIPTS_DEBT', '', 'string', 21.5, 46)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('a7ba5893-2023-46ff-a256-4dbad86e0d3c', 'COSTS_CM_TOTAL', 'COSTS_CM_TOTAL', '', 'string', 17.5, 47)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('f355c0ec-8659-4829-8025-c9f1cbf47ef4', 'COSTS_CM_CHARGES', 'COSTS_CM_CHARGES', '', 'string', 20.16, 48)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('976ea857-e201-47d5-a3fe-d00246533a61', 'COSTS_CM_DEBT', 'COSTS_CM_DEBT', '', 'string', 16.16, 49)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('12e070b8-4b0e-437b-b5a6-77983acf2d48', 'DAMAGES_DEBT', 'DAMAGES_DEBT', '', 'string', 15.66, 50)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('c0dd9313-a3a6-4739-8338-4f5cc78ff82a', 'DAMAGES_ADJ_TOTAL', 'DAMAGES_ADJ_TOTAL', '', 'string', 21.5, 51)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('dd1432e8-bb15-4456-a061-e31c3a0ae279', 'DAMAGES_ADJ_CHARGES', 'DAMAGES_ADJ_CHARGES', '', 'string', 24.16, 52)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('ef8a1b68-6e31-4497-9a75-da18bce4b150', 'DAMAGES_ADJ_DEBT', 'DAMAGES_ADJ_DEBT', '', 'string', 20.16, 53)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('1b7ce80c-f5b1-4634-9380-cdf5fff610be', 'DAMAGES_ADJ_DEBT_GRE', 'DAMAGES_ADJ_DEBT_GRE', '', 'string', 24.83, 54)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('5d227d16-b147-40aa-93ca-b87e3c215497', 'DAMAGES_ADJ_DEBT_LOW', 'DAMAGES_ADJ_DEBT_LOW', '', 'string', 25.66, 55)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('6b3e8f07-905d-47f0-80a3-06c411f9d394', 'DAMAGES_WD_TOTAL', 'DAMAGES_WD_TOTAL', '', 'string', 21.5, 56)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('43903d4c-da22-4f3a-ad3e-7d8626773ad4', 'DAMAGES_WD_CHARGES', 'DAMAGES_WD_CHARGES', '', 'string', 24.16, 57)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('78e0da9d-d8ad-4c8b-a14f-132320902eac', 'DAMAGES_WD_DEBT', 'DAMAGES_WD_DEBT', '', 'string', 20.16, 58)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('9176e4a7-b4a3-4841-8dc3-fafb3f95b313', 'DAMAGES_WO_TOTAL', 'DAMAGES_WO_TOTAL', '', 'string', 21.5, 59)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('f0536337-4430-46d8-96b9-cd72b55a2086', 'DAMAGES_WO_CHARGES', 'DAMAGES_WO_CHARGES', '', 'string', 24.33, 60)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('00a9b4e5-3cd3-46cf-b447-1baf1c420af3', 'DAMAGES_WO_DEBT', 'DAMAGES_WO_DEBT', '', 'string', 20.33, 61)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('e10b527d-3102-406d-a7aa-ce479d86b35a', 'DAMAGES_REC_TOTAL', 'DAMAGES_REC_TOTAL', '', 'string', 21.5, 62)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('42461e2c-49fa-49e8-a504-8e28f3a2b946', 'DAMAGES_RECEIPTS_CHARGES', 'DAMAGES_RECEIPTS_CHARGES', '', 'string', 29, 63)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('61b53085-4558-4082-ad0e-655ce4f62aaf', 'DAMAGES_RECEIPTS_DEBT', 'DAMAGES_RECEIPTS_DEBT', '', 'string', 25, 64)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('e5ab38f0-22b8-48a0-b2df-9a4812ce298a', 'DAMAGES_CM_TOTAL', 'DAMAGES_CM_TOTAL', '', 'string', 20, 65)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('cbd65372-4eb8-4c3c-9127-f51e2cd2fa7c', 'DAMAGES_CM_CHARGES', 'DAMAGES_CM_CHARGES', '', 'string', 23.83, 66)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('c0ffaca8-1cb5-4450-b928-9e82b55f9271', 'DAMAGES_CM_DEBT', 'DAMAGES_CM_DEBT', '', 'string', 19.83, 67)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('416903a9-e807-487f-9971-a3930a5e17d0', 'REVOCATION_DEBT', 'REVOCATION_DEBT', '', 'string', 18.5, 68)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('cde9446d-08de-4007-8468-2b9ad950ed9a', 'LAA_SCHEME', 'LAA_SCHEME', '', 'string', 12.66, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('b34c87a0-bc31-4068-83d2-d6d699cc77aa', 'ATTACHED_TO_CASE', 'ATTACHED_TO_CASE', '', 'string', 19.5, 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('6f356cb2-a83f-4433-b2a5-264798204324', 'LAA_DEBT_TYPE', 'LAA_DEBT_TYPE', '', 'string', 15.83, 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('7166cf73-117e-4a2a-b768-294661d5af3a', 'DEBT_TRANS_CODE', 'DEBT_TRANS_CODE', '', 'string', 22.83, 4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('75fd9017-2657-407b-98a9-6234ae5ff396', 'DEBT_TRANS_DESCRIPTION', 'DEBT_TRANS_DESCRIPTION', '', 'string', 58.0, 5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('27bd0b0f-e813-44a6-8c59-8b3b07e74879', 'COUNT_OF_TRX_ID', 'COUNT_OF_TRX_ID', '', 'string', 18.33, 6)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('4d2d044d-be20-43d1-93ae-a606570f539e', 'SUM_OF_DEBT_TOTAL', 'SUM_OF_DEBT_TOTAL', '', 'string', 21.0, 7)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('82d2e6f9-18a5-441b-9984-257b5b4c198d', 'SUM_OF_CM_DEBT', 'SUM_OF_CM_DEBT', '', 'string', 18.42, 8)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('babf1d62-a715-4b23-a8ad-c3461d1cc277', 'SUM_OF_CM_CHARGE', 'SUM_OF_CM_CHARGE', '', 'string', 21.5, 9)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('ed5c230a-819b-4d36-a5a1-0d0fbbe07711', 'SUM_OF_CM_UNAPPLIED', 'SUM_OF_CM_UNAPPLIED', '', 'string', 24.33, 10)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('1fcadf0e-3755-488d-80b1-f50db76661d4', 'SUM_OF_ADJUST_DEBT', 'SUM_OF_ADJUST_DEBT', '', 'string', 22.33, 11)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('2073eee5-f833-4b51-9437-d81736a0a22e', 'SUM_OF_ADJUST_DEBT_GRE', 'SUM_OF_ADJUST_DEBT_GRE', '', 'string', 27.0, 12)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('897f7ab1-ff2a-4080-a39a-185e3046e25d', 'SUM_OF_ADJUST_DEBT_LOW', 'SUM_OF_ADJUST_DEBT_LOW', '', 'string', 27.66, 13)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d5d8e4e6-ec20-4c0f-bb25-d57c0e32cc2e', 'SUM_OF_ADJUST_CHARGE', 'SUM_OF_ADJUST_CHARGE', '', 'string', 25.16, 14)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('a0a6bb19-f9a0-495e-b68b-ab978bf39e4b', 'SUM_OF_WD_DEBT', 'SUM_OF_WD_DEBT', '', 'string', 18.66, 15)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('b9867f3c-dbe9-44f3-a1bf-700a58c1282b', 'SUM_OF_WD_CHARGE', 'SUM_OF_WD_CHARGE', '', 'string', 21.66, 16)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('a5e5fa73-f86a-4785-81b7-242245bceecc', 'SUM_OF_WO_DEBT', 'SUM_OF_WO_DEBT', '', 'string', 18.83, 17)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('2460e52b-5100-438d-8054-12a27a2c7c32', 'SUM_OF_WO_CHARGE', 'SUM_OF_WO_CHARGE', '', 'string', 21.83, 18)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('0455e42d-4ea2-4d25-bf3e-5c26fd30fcf5', 'SUM_OF_INCOME_DEBT', 'SUM_OF_INCOME_DEBT', '', 'string', 23.0, 19)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('fded5c13-7f71-4924-b29a-153c61a2dee3', 'SUM_OF_INCOME_CHARGE', 'SUM_OF_INCOME_CHARGE', '', 'string', 26.0, 20)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('ac4eb896-61c9-4b77-9ccb-fa603b94caa3', 'SUM_OF_INCOME_UNAPPLIED', 'SUM_OF_INCOME_UNAPPLIED', '', 'string', 28.83, 21)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('82a47334-8a18-49bf-9166-cf251b76296f', 'TRX_NUMBER', 'TRX_NUMBER', '', 'string', 13.5, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('3a7bbfab-05d6-4ae3-b0b0-f9767812a074', 'TRX_ID', 'TRX_ID', '', 'string', 8, 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('cd6b386d-add8-47b2-9890-3ec1684e4562', 'CIS_CASE_NO_ON_TRANS', 'CIS_CASE_NO_ON_TRANS', '', 'string', 24.16, 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width)
VALUES ('27f7305e-fa16-42cb-aad3-15be006c4767', 'SECURED CIS_CASE_NO_ON_PARTY', 'SECURED CIS_CASE_NO_ON_PARTY', '', 'string', 8.17)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('af132b69-3469-48be-ba46-470f79e88ff2', 'CCMS_CASE_ID', 'CCMS_CASE_ID', '', 'string', 14.5, 6)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('653b50db-659c-4093-b0be-7dc45368c244', 'CCMS_CASE_NUMBER', 'CCMS_CASE_NUMBER', '', 'string', 20.83, 7)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('5acea4db-2874-439a-8a9c-698dd7da65e2', 'CCMS_CASE_MIGRATED', 'CCMS_CASE_MIGRATED', '', 'string', 22.5, 8)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d45dc364-655a-4f45-ae61-9a65be31bd06', 'DEBT_TRANS_MIGRATED', 'DEBT_TRANS_MIGRATED', '', 'string', 23.33, 9)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('28dd5f3e-5ffb-40fd-8239-ca853743d051', 'DEBT_TRANS_CODE', 'DEBT_TRANS_CODE', '', 'string', 22.83, 10)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('7a09c6b4-451f-4d36-bba0-0378224ca06e', 'DEBT_TRANS_DESCRIPTION', 'DEBT_TRANS_DESCRIPTION', '', 'string', 58, 11)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('b0351307-6c4d-41ba-9bb0-6aeb818f2824', 'LAA_DEBT_TYPE', 'LAA_DEBT_TYPE', '', 'string', 15.83, 12)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('b7570e9c-2234-461c-b1a8-7076dc431e2c', 'LAA_SCHEME', 'LAA_SCHEME', '', 'string', 12.66, 13)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('068dbf45-7e0d-44bb-8053-b5bddff69030', 'ATTACHED_TO_CASE', 'ATTACHED_TO_CASE', '', 'string', 19.5, 14)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('8394cc33-2aa6-4578-9d13-dc034b1f30c4', 'DEBT_TOTAL', 'DEBT_TOTAL', '', 'string', 12, 15)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width)
VALUES ('b62ababb-5b28-4cc7-9639-451f90c42872', 'CM_DEBT CM_CHARGE', 'CM_DEBT CM_CHARGE', '', 'string', 11.17)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('aa37aabd-1a35-4882-9544-041076c4046e', 'CM_UNAPPLIED', 'CM_UNAPPLIED', '', 'string', 15.33, 18)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('75dcfc81-7780-49fd-a846-0d994b04b6ab', 'ADJUST_DEBT', 'ADJUST_DEBT', '', 'string', 13.33, 19)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('1c1307fe-6cf5-41c2-aca9-65162194019a', 'ADJUST_DEBT_GRE', 'ADJUST_DEBT_GRE', '', 'string', 18, 20)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('490e1f82-98b1-4cf1-a738-7374daa152b8', 'ADJUST_DEBT_LOW', 'ADJUST_DEBT_LOW', '', 'string', 18.66, 21)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('e05e00c4-f3ab-4494-8019-81a5f7cffed8', 'ADJUST_CHARGE', 'ADJUST_CHARGE', '', 'string', 16.16, 22)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('394385fb-394f-4c93-b1d9-8a9880d70e1e', 'WD_DEBT', 'WD_DEBT', '', 'string', 9.66, 23)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('5f13e9bb-19e0-45aa-a2de-f9bb18d5ded0', 'WD_CHARGE', 'WD_CHARGE', '', 'string', 12.5, 24)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('77e07338-5aed-4d73-ad14-99eacd8eb5b2', 'WO_DEBT', 'WO_DEBT', '', 'string', 9.83, 25)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('0727068e-9946-4569-b5ac-c15a5d2c5294', 'WO_CHARGE', 'WO_CHARGE', '', 'string', 12.66, 26)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('5712e209-5a1c-4881-b966-acc3f8f72a05', 'INCOME_DEBT', 'INCOME_DEBT', '', 'string', 14, 27)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('90eff626-cbc7-4e15-b639-a7124d095214', 'INCOME_CHARGE', 'INCOME_CHARGE', '', 'string', 16.83, 28)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('ff5aef23-b653-4260-8700-5f56e901eb23', 'INCOME_UNAPPLIED', 'INCOME_UNAPPLIED', '', 'string', 19.83, 29)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('2e3372fc-6012-439c-852b-31d5c73dc4aa', 'LAST_INT_DATE', 'LAST_INT_DATE', '', 'string', 14.83, 30)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('01427a89-4850-4a84-8a3c-34e5860405b4', 'FIRMS_ACC_CODE', 'FIRMS_ACC_CODE', '', 'string', 18.16, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('02427a89-4850-4a84-8a3c-34e5860405b4', 'ACC_ID', 'ACC_ID', '', 'number', 9.00, 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('03427a89-4850-4a84-8a3c-34e5860405b4', 'FIRMS_NAME', 'FIRMS_NAME', '', 'string', 49.50, 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('04427a89-4850-4a84-8a3c-34e5860405b4', 'DATE_RECEIVED', 'DATE_RECEIVED', '', 'date', 16.16, 4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('05427a89-4850-4a84-8a3c-34e5860405b4', 'DATE_PROCESSED', 'DATE_PROCESSED', '', 'date', 18.50, 5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('06427a89-4850-4a84-8a3c-34e5860405b4', 'LA_REQ_NO', 'LA_REQ_NO', '', 'number', 12.00, 6)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('07427a89-4850-4a84-8a3c-34e5860405b4', 'CASE_REFERENCE', 'CASE_REFERENCE', '', 'string', 20.83, 7)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('08427a89-4850-4a84-8a3c-34e5860405b4', 'SCHEME', 'SCHEME', '', 'number', 8.66, 8)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('09427a89-4850-4a84-8a3c-34e5860405b4', 'TRANS_INT_ID', 'TRANS_INT_ID', '', 'number', 14.33, 9)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('10427a89-4850-4a84-8a3c-34e5860405b4', 'TRANS_ID', 'TRANS_ID', '', 'number', 10.16, 10)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('11427a89-4850-4a84-8a3c-34e5860405b4', 'REC_BILL_TYPE', 'REC_BILL_TYPE', '', 'string', 15.66, 11)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('12427a89-4850-4a84-8a3c-34e5860405b4', 'DOC_SOURCE', 'DOC_SOURCE', '', 'string', 13.83, 12)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('13427a89-4850-4a84-8a3c-34e5860405b4', 'DOC_TYPE', 'DOC_TYPE', '', 'string', 12.5, 13)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('14427a89-4850-4a84-8a3c-34e5860405b4', 'TLINE_ID', 'TLINE_ID', '', 'number', 10.00, 14)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('15427a89-4850-4a84-8a3c-34e5860405b4', 'LINE_TYPE', 'LINE_TYPE', '', 'string', 13.33, 15)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('16427a89-4850-4a84-8a3c-34e5860405b4', 'AMOUNT', 'AMOUNT', '', 'number', 10.00, 16)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('17427a89-4850-4a84-8a3c-34e5860405b4', 'ACC_CODE', 'ACC_CODE', '', 'string', 11.00, 17)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('18427a89-4850-4a84-8a3c-34e5860405b4', 'TOTAL', 'TOTAL', '', 'number', 9.50, 18)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('19427a89-4850-4a84-8a3c-34e5860405b4', 'HOLD_STATUS', 'HOLD_STATUS', '', 'string', 14.66, 19)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('20427a89-4850-4a84-8a3c-34e5860405b4', 'CREDITOR_STATUS', 'CREDITOR_STATUS', '', 'string', 19.16, 20)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('21427a89-4850-4a84-8a3c-34e5860405b4', 'LOOKUP', 'LOOKUP', '', 'string', 15.66, 21)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('22427a89-4850-4a84-8a3c-34e5860405b4', 'CRED_POA', 'CRED_POA', '', 'number', 11.00, 22)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('23427a89-4850-4a84-8a3c-34e5860405b4', 'CRED_NON_DISB_POA_ADJ', 'CRED_NON_DISB_POA_ADJ', '', 'number', 26.50, 23)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('24427a89-4850-4a84-8a3c-34e5860405b4', 'CRED_REC_POA', 'CRED_REC_POA', '', 'number', 15.83, 24)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('25427a89-4850-4a84-8a3c-34e5860405b4', 'CRED_NON_DISB_REC_POA_ADJ', 'CRED_NON_DISB_REC_POA_ADJ', '', 'number', 31.50, 25)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('26427a89-4850-4a84-8a3c-34e5860405b4', 'CRED_BILLS', 'CRED_BILLS', '', 'number', 12.50, 26)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('728c4334-d956-468d-a55f-53e52e541ea3', 'CM_DEBT', 'CM_DEBT', '', 'string', 12, 4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('728c4334-d956-468d-a55f-53e52e541ea4', 'CM_CHARGE', 'CM_CHARGE', '', 'string', 12.33, 5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('728c4334-d956-468d-a55f-53e52e541eaa', 'WO_DEBT', 'WO_DEBT', '', 'string', 12.66, 11)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('728c4334-d956-468d-a55f-53e52e541eab', 'WO_CHARGE', 'WO_CHARGE', '', 'string', 12.66, 12)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('728c4334-d956-468d-a55f-53e52e541eac', 'WD_DEBT', 'WD_DEBT', '', 'string', 12.66, 13)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('728c4334-d956-468d-a55f-53e52e541ead', 'WD_CHARGE', 'WD_CHARGE', '', 'string', 12.5, 14)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a120', 'MIGRATED_CASE', 'MIGRATED_CASE', '', 'string', 16.16, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a121', 'MIGRATED_FLAG', 'MIGRATED_FLAG', '', 'string', 16.33, 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a122', 'DATE_MIGRATED', 'DATE_MIGRATED', 'dd-mmm-yy', 'date', 16.33, 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a123', 'CASE_REFERENCE', 'CASE_REFERENCE', '', 'string', 27.5, 4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a124', 'CCMS_CASE_ID', 'CCMS_CASE_ID', '', 'string', 14.5, 5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a125', 'FULL_CASE', 'FULL_CASE', '', 'string', 10.5, 6)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a126', 'CIS_CASE_NUMBER', 'CIS_CASE_NUMBER', '', 'string', 27.5, 7)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a127', 'CONTRIBS_SEC', 'CONTRIBS_SEC', '', 'string', 14.16, 8)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a16d', 'REVOCATION_ADJ_TOTAL', 'REVOCATION_ADJ_TOTAL', '', 'string', 24, 69)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a16e', 'REVOCATION_ADJ_CHARGES', 'REVOCATION_ADJ_CHARGES', '', 'string', 26.83, 70)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a16f', 'REVOCATION_ADJ_DEBT', 'REVOCATION_ADJ_DEBT', '', 'string', 22.83, 71)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a170', 'REVOCATION_ADJ_DEBT_GRE', 'REVOCATION_ADJ_DEBT_GRE', '', 'string', 27.5, 72)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a171', 'REVOCATION_ADJ_DEBT_LOW', 'REVOCATION_ADJ_DEBT_LOW', '', 'string', 28.33, 73)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a172', 'REVOCATION_WD_TOTAL', 'REVOCATION_WD_TOTAL', '', 'string', 24, 74)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a173', 'REVOCATION_WD_CHARGES', 'REVOCATION_WD_CHARGES', '', 'string', 26.83, 75)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a174', 'REVOCATION_WD_DEBT', 'REVOCATION_WD_DEBT', '', 'string', 22.83, 76)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a175', 'REVOCATION_WO_TOTAL', 'REVOCATION_WO_TOTAL', '', 'string', 24.16, 77)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a176', 'REVOCATION_WO_CHARGES', 'REVOCATION_WO_CHARGES', '', 'string', 27, 78)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a177', 'REVOCATION_WO_DEBT', 'REVOCATION_WO_DEBT', '', 'string', 23, 79)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a178', 'REVOCATION_REC_TOTAL', 'REVOCATION_REC_TOTAL', '', 'string', 24, 80)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a179', 'REVOCATION_RECEIPTS_CHARGES', 'REVOCATION_RECEIPTS_CHARGES', '', 'string', 31.66, 81)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a17a', 'REVOCATION_RECEIPTS_DEBTS', 'REVOCATION_RECEIPTS_DEBT', '', 'string', 27.66, 82)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a17b', 'REVOCATION_CM_TOTAL', 'REVOCATION_CM_TOTAL', '', 'string', 23.66, 83)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a17c', 'REVOCATION_CM_CHARGES', 'REVOCATION_CM_CHARGES', '', 'string', 26.5, 84)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a17d', 'REVOCATION_CM_DEBT', 'REVOCATION_CM_DEBT', '', 'string', 22.5, 85)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a17e', 'STATCHG_DEBT', 'STATCHG_DEBT', '', 'string', 14.66, 86)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a17f', 'STATCHG_ADJ_TOTAL', 'STATCHG_ADJ_TOTAL', '', 'string', 20.33, 87)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a180', 'STATCHG_ADJ_CHARGES', 'STATCHG_ADJ_CHARGES', '', 'string', 23.16, 88)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a181', 'STATCHG_ADJ_DEBT', 'STATCHG_ADJ_DEBT', '', 'string', 19.16, 89)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a182', 'STATCHG_WD_TOTAL', 'STATCHG_WD_TOTAL', '', 'string', 20.33, 90)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a183', 'STATCHG_WD_CHARGES', 'STATCHG_WD_CHARGES', '', 'string', 23.16, 91)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a184', 'STATCHG_WD_DEBT', 'STATCHG_WD_DEBT', '', 'string', 19.16, 92)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a185', 'STATCHG_ADJ_DEBT_GRE', 'STATCHG_ADJ_DEBT_GRE', '', 'string', 23.83, 93)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a186', 'STATCHG_ADJ_DEBT_LOW', 'STATCHG_ADJ_DEBT_LOW', '', 'string', 24.5, 94)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a187', 'STATCHG_WO_TOTAL', 'STATCHG_WO_TOTAL', '', 'string', 20.5, 95)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a188', 'STATCHG_WO_CHARGES', 'STATCHG_WO_CHARGES', '', 'string', 23.33, 96)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a189', 'STATCHG_WO_DEBT', 'STATCHG_WO_DEBT', '', 'string', 19.33, 97)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a18a', 'STATCHG_REC_TOTAL', 'STATCHG_REC_TOTAL', '', 'string', 20.33, 98)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a18b', 'STATCHG_RECEIPTS_CHARGES', 'STATCHG_RECEIPTS_CHARGES', '', 'string', 28, 99)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a18c', 'STATCHG_RECEIPTS_DEBT', 'STATCHG_RECEIPTS_DEBT', '', 'string', 24, 100)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a18d', 'STATCHG_CM_TOTAL', 'STATCHG_CM_TOTAL', '', 'string', 20, 101)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a18e', 'STATCHG_CM_CHARGES', 'STATCHG_CM_CHARGES', '', 'string', 22.83, 102)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a18f', 'STATCHG_CM_DEBT', 'STATCHG_CM_DEBT', '', 'string', 18.83, 103)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a190', 'STATCHG_LAST_INT_DATE', 'STATCHG_LAST_INT_DATE', 'dd-mmm-yy', 'date', 24.33, 104)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a191', 'FINAL_BILL_DATE', 'FINAL_BILL_DATE', 'dd-mmm-yy', 'date', 16.33, 105)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a192', 'TOTAL_RECOVERABLE', 'TOTAL_RECOVERABLE', '', 'string', 20.33, 106)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('d509add1-de34-4c7f-8dd9-fddf8763a193', 'LEGAL_HELP', 'LEGAL_HELP', '', 'string', 11.66, 107)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('645be5da-0248-4f6f-87e4-28957d4da103', 'SECURED', 'SECURED', '', 'string', 9, 4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('645be5da-0248-4f6f-87e4-28957d4da104', 'CIS_CASE_NO_ON_PARTY', 'CIS_CASE_NO_ON_PARTY', '', 'string', 24, 5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('645be5da-0248-4f6f-87e4-28957d4da10f', 'CM_DEBT', 'CM_DEBT', '', 'string', 9.5, 16)
ON CONFLICT (id) DO NOTHING;

INSERT INTO glad.field_attributes (id, source_name, mapped_name, format, format_type, column_width, column_order)
VALUES ('645be5da-0248-4f6f-87e4-28957d4da110', 'CM_CHARGE', 'CM_CHARGE', '', 'string', 12.33, 17)
ON CONFLICT (id) DO NOTHING;

-- -----------------------------------------------------------
-- REPORT ROLES (27 rows)
-- -----------------------------------------------------------
INSERT INTO glad.report_roles (role_id, report_id)
VALUES (1, '523f38f0-2179-4824-b885-3a38c5e149e8')
ON CONFLICT (report_id, role_id) DO NOTHING;

INSERT INTO glad.report_roles (role_id, report_id)
VALUES (2, 'cc55e276-97b0-4dd8-a919-26d4aa373266')
ON CONFLICT (report_id, role_id) DO NOTHING;

INSERT INTO glad.report_roles (role_id, report_id)
VALUES (2, 'aca2120c-8f82-45a8-a682-8dedfb7997a7')
ON CONFLICT (report_id, role_id) DO NOTHING;

INSERT INTO glad.report_roles (role_id, report_id)
VALUES (3, 'b36f9bbb-1178-432c-8f99-8090e285f2d3')
ON CONFLICT (report_id, role_id) DO NOTHING;

INSERT INTO glad.report_roles (role_id, report_id)
VALUES (3, 'f46b4d3d-c100-429a-bf9a-6c3305dbdbf1')
ON CONFLICT (report_id, role_id) DO NOTHING;

INSERT INTO glad.report_roles (role_id, report_id)
VALUES (3, 'f46b4d3d-c100-429a-bf9a-6c3305dbdbf2')
ON CONFLICT (report_id, role_id) DO NOTHING;

INSERT INTO glad.report_roles (role_id, report_id)
VALUES (3, 'f46b4d3d-c100-429a-bf9a-6c3305dbdbf3')
ON CONFLICT (report_id, role_id) DO NOTHING;

INSERT INTO glad.report_roles (role_id, report_id)
VALUES (3, 'f46b4d3d-c100-429a-bf9a-6c3305dbdbf4')
ON CONFLICT (report_id, role_id) DO NOTHING;

INSERT INTO glad.report_roles (role_id, report_id)
VALUES (3, 'f46b4d3d-c100-429a-bf9a-6c3305dbdbf5')
ON CONFLICT (report_id, role_id) DO NOTHING;

INSERT INTO glad.report_roles (role_id, report_id)
VALUES (3, 'f46b4d3d-c100-429a-bf9a-6c3305dbdbf6')
ON CONFLICT (report_id, role_id) DO NOTHING;

INSERT INTO glad.report_roles (role_id, report_id)
VALUES (3, 'f46b4d3d-c100-429a-bf9a-6c3305dbdbf7')
ON CONFLICT (report_id, role_id) DO NOTHING;

INSERT INTO glad.report_roles (role_id, report_id)
VALUES (3, 'f46b4d3d-c100-429a-bf9a-6c3305dbdbf8')
ON CONFLICT (report_id, role_id) DO NOTHING;

INSERT INTO glad.report_roles (role_id, report_id)
VALUES (3, 'f46b4d3d-c100-429a-bf9a-6c3305dbdbf9')
ON CONFLICT (report_id, role_id) DO NOTHING;

INSERT INTO glad.report_roles (role_id, report_id)
VALUES (3, 'f46b4d3d-c100-429a-bf9a-6c3305dbdbfa')
ON CONFLICT (report_id, role_id) DO NOTHING;

INSERT INTO glad.report_roles (role_id, report_id)
VALUES (3, 'f46b4d3d-c100-429a-bf9a-6c3305dbdbfb')
ON CONFLICT (report_id, role_id) DO NOTHING;

INSERT INTO glad.report_roles (role_id, report_id)
VALUES (3, 'f46b4d3d-c100-429a-bf9a-223305dbdbfb')
ON CONFLICT (report_id, role_id) DO NOTHING;

INSERT INTO glad.report_roles (role_id, report_id)
VALUES (3, 'eee30b23-2c8d-4b4b-bb11-8cd67d07915c')
ON CONFLICT (report_id, role_id) DO NOTHING;

INSERT INTO glad.report_roles (role_id, report_id)
VALUES (3, 'a017241a-359f-4fdb-a0cd-7f28f1946ef1')
ON CONFLICT (report_id, role_id) DO NOTHING;

INSERT INTO glad.report_roles (role_id, report_id)
VALUES (3, '7073dd13-e325-4863-a05c-a049a815d1f7')
ON CONFLICT (report_id, role_id) DO NOTHING;

INSERT INTO glad.report_roles (role_id, report_id)
VALUES (3, 'e6823193-f5b0-451b-8965-e4d4914980da')
ON CONFLICT (report_id, role_id) DO NOTHING;

INSERT INTO glad.report_roles (role_id, report_id)
VALUES (3, '56328b13-254d-435d-813a-5863f94b996d')
ON CONFLICT (report_id, role_id) DO NOTHING;

INSERT INTO glad.report_roles (role_id, report_id)
VALUES (3, '7bda9aa4-6129-4c71-bd12-7d4e46fdd882')
ON CONFLICT (report_id, role_id) DO NOTHING;

INSERT INTO glad.report_roles (role_id, report_id)
VALUES (3, '516cdbff-5fa8-4050-b5e6-7edf71daf679')
ON CONFLICT (report_id, role_id) DO NOTHING;

INSERT INTO glad.report_roles (role_id, report_id)
VALUES (3, '8b9f0484-819f-4e0f-b60a-0b3f9d30d9ba')
ON CONFLICT (report_id, role_id) DO NOTHING;

INSERT INTO glad.report_roles (role_id, report_id)
VALUES (3, '90af8289-2c07-4b65-8f37-6b4659920207')
ON CONFLICT (report_id, role_id) DO NOTHING;

INSERT INTO glad.report_roles (role_id, report_id)
VALUES (2, '55daf3c1-28f0-4260-9396-2ee6d537abab')
ON CONFLICT (report_id, role_id) DO NOTHING;

INSERT INTO glad.report_roles (role_id, report_id)
VALUES (2, 'c4ba2e89-c106-48a7-8e1d-7c19dbd7710d')
ON CONFLICT (report_id, role_id) DO NOTHING;
