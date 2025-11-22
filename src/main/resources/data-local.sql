-- Sample data for local H2 database
-- This data mirrors the test fixtures to enable local development

-- Insert report output types
INSERT INTO GPFD.REPORT_OUTPUT_TYPES (ID, EXTENSION, DESCRIPTION) VALUES 
  ('6ebd27ac-4d83-485d-a4fd-3e45f9a53484', 'csv', 'Comma Separated Text'),
  ('bd098666-94e4-4b0e-822c-8e5dfb04c908', 'xlsx', 'Excel Document'),
  ('bd098666-94e4-4b0e-822c-8e5dfb04c909', 'csv', 'Brand new Output Type'),
  ('bd098666-94e4-4b0e-822c-8e5dfb04c910', 's3storage', 'New Output Type');

-- Insert sample reports
INSERT INTO GPFD.REPORTS (ID, NAME, TEMPLATE_SECURE_DOCUMENT_ID, REPORT_CREATION_DATE, DESCRIPTION, NUM_DAYS_TO_KEEP, FILE_NAME, ACTIVE, REPORT_OUTPUT_TYPE, REPORT_OWNER_ID, REPORT_OWNER_NAME, REPORT_OWNER_EMAIL) VALUES
  ('b36f9bbb-1178-432c-8f99-8090e285f2d3', 'CCMS Invoice Analysis (CIS to CCMS)', '00000000-0000-0000-0000-000000000000', TIMESTAMP '2025-02-15 00:00:00', 'Summary of invoices in CIS and CCMS by original source IT system', 30, 'CCMS_invoice_analysis', 'Y', 'bd098666-94e4-4b0e-822c-8e5dfb04c908', '00000000-0000-0000-0000-000000000001', 'Chancey McTavish', 'owneremail@example.com'),
  ('f46b4d3d-c100-429a-bf9a-223305dbdbfb', 'CCMS General ledger extractor (small manual batches)', 'f46b4d3d-c100-429a-bf9a-223305dbdbfb', CURRENT_TIMESTAMP, 'CCMS General ledger extractor (small manual batches)', 30, 'CCMS_General_ledger_extractor_(small_manual_batches)', 'Y', 'bd098666-94e4-4b0e-822c-8e5dfb04c908', '00000000-0000-0000-0000-000000000003', 'Fionn mac Cumhaill', 'finn.maccool@example.org'),
  ('eee30b23-2c8d-4b4b-bb11-8cd67d07915c', 'CCMS and CIS Bank Account Report w Category Code (YTD)', 'eee30b23-2c8d-4b4b-bb11-8cd67d07915c', CURRENT_TIMESTAMP, 'CCMS and CIS Bank Account Report w Category Code (YTD)', 30, 'CCMS_AND_CIS_BANK_ACCOUNT_REPORT_W_CATEGORY_CODE_YTD', 'Y', 'bd098666-94e4-4b0e-822c-8e5dfb04c908', '00000000-0000-0000-0000-000000000003', 'Fionn mac Cumhaill', 'finn.maccool@example.org'),
  ('7073dd13-e325-4863-a05c-a049a815d1f7', 'Legal Help contract balances', 'eee30b23-2c8d-4b4b-bb11-8cd67d07915c', CURRENT_TIMESTAMP, 'Legal Help contract balances', 30, 'LEGAL_HELP_CONTRACT_BALANCES', 'Y', 'bd098666-94e4-4b0e-822c-8e5dfb04c908', '00000000-0000-0000-0000-000000000003', 'Teresa Green', 'TeresaGreen@example.org'),
  ('7bda9aa4-6129-4c71-bd12-7d4e46fdd882', 'AGFS late processed bills', '7bda9aa4-6129-4c71-bd12-7d4e46fdd882', CURRENT_TIMESTAMP, 'AGFS late processed bills', 30, 'LATE_PROCESSED_BILLS', 'Y', 'bd098666-94e4-4b0e-822c-8e5dfb04c908', '00000000-0000-0000-0000-000000000003', 'Teresa Green', 'TeresaGreen@example.org'),
  ('8b9f0484-819f-4e0f-b60a-0b3f9d30d9ba', 'CCMS Third party report', '8b9f0484-819f-4e0f-b60a-0b3f9d30d9ba', NULL, 'CCMS Third party report', NULL, 'CCMS_THIRD_PARTY_REPORT', 'Y', 'bd098666-94e4-4b0e-822c-8e5dfb04c908', '00000000-0000-0000-0000-000000000003', NULL, NULL),
  ('f46b4d3d-c100-429a-bf9a-6c3305dbdbf1', 'CCMS Invoice Analysis (CIS to CCMS)', '00000000-0000-0000-0000-000000000000', TIMESTAMP '2025-02-15 00:00:00', 'Summary of invoices in CIS and CCMS by original source IT system', 30, 'CCMS_invoice_analysis', 'Y', '6ebd27ac-4d83-485d-a4fd-3e45f9a53484', '00000000-0000-0000-0000-000000000001', 'Chancey McTavish', 'owneremail@example.com'),
  ('0fbec75b-2d72-44f5-a0e3-2dcb29d92f79', 'acceptance_test_table', '00000000-0000-0000-0000-000000000000', TIMESTAMP '2025-02-15 00:00:00', 'acceptance_test_table', 30, 'acceptance_test_table', 'Y', '6ebd27ac-4d83-485d-a4fd-3e45f9a53484', '00000000-0000-0000-0000-000000000003', 'Teresa Green', 'teresagreen@example.org'),
  ('f46b4d3d-c100-429a-bf9a-6c3305dbdbf5', 'CIS to CCMS payment value Not Defined', '00000000-0000-0000-0000-000000000000', TIMESTAMP '2025-02-15 00:00:00', 'Details of invoices transferred from CIS to CCMS by Legal Aid Scheme', 30, 'CIS_TO_CCMS_PAYMENT_VALUE_NOT_DEFINED', 'Y', '6ebd27ac-4d83-485d-a4fd-3e45f9a53484', '00000000-0000-0000-0000-000000000003', 'Fionn mac Cumhaill', 'finn.maccool@example.org'),
  ('0d4da9ec-b0b3-4371-af10-f375330d85d3', 'CIS to CCMS payment value Defined', '00000000-0000-0000-0000-000000000000', TIMESTAMP '2025-02-15 00:00:00', 'Details of invoices transferred from CIS to CCMS by Legal Aid Scheme', 30, 'CIS_TO_CCMS_PAYMENT_VALUE_NOT_DEFINED', 'Y', '6ebd27ac-4d83-485d-a4fd-3e45f9a53484', '00000000-0000-0000-0000-000000000003', 'Fionn mac Cumhaill', 'finn.maccool@example.org'),
  ('b36f9bbb-1178-432c-8f99-8090e285f2d4', 'New report with a new output type', '00000000-0000-0000-0000-000000000000', TIMESTAMP '2025-03-15 00:00:00', 'A report with output type as yet unknown to the API', 30, 'New report', 'Y', 'bd098666-94e4-4b0e-822c-8e5dfb04c909', '00000000-0000-0000-0000-000000000001', 'Dr Frankenstein', 'dr.frankenstein@example.com'),
  ('cc55e276-97b0-4dd8-a919-26d4aa373266', 'REP012 - Original Submissions Value Report', '00000000-0000-0000-0000-000000000000', TIMESTAMP '2025-03-15 00:00:00', 'A report from s3 storage', 30, 'New report', 'Y', 'bd098666-94e4-4b0e-822c-8e5dfb04c910', '00000000-0000-0000-0000-000000000001', 'Dr Frankenstein', 'dr.frankenstein@example.com');

-- Insert a sample report query
INSERT INTO GPFD.REPORT_QUERIES (ID, REPORT_ID, QUERY, TAB_NAME) VALUES
  ('069bd36f-b3e5-474d-9408-75b8de56de03', '0fbec75b-2d72-44f5-a0e3-2dcb29d92f79', 'SELECT * FROM ANY_REPORT.CIRCUS_VISIT_INVOICES', 'MAIN');

-- Insert a sample report tracking record
INSERT INTO GPFD.REPORTS_TRACKING (ID, NAME, REPORT_ID, CREATION_DATE, REPORT_DOWNLOADED_BY, REPORT_CREATOR, REPORT_OWNER, REPORT_OUTPUT_TYPE, TEMPLATE_URL, REPORT_URL) VALUES
  ('00000000-0000-0000-0001-000000000001', 'Test Report Name', 'b36f9bbb-1178-432c-8f99-8090e285f2d3', TIMESTAMP '2025-02-15 00:00:00', '00000000-0000-0000-0003-000000000001', '00000000-0000-0000-0004-000000000001', '00000000-0000-0000-0005-000000000001', '00000000-0000-0000-0006-000000000001', 'www.example.org/template', 'www.example.org/report');
