INSERT INTO GPFD.REPORT_TRACKING(ID, REPORT_NAME, REPORT_URL, CREATION_TIME, MAPPING_ID, REPORT_GENERATED_BY)
VALUES (GPFD_TRACKING_TABLE_SEQUENCE.NEXTVAL, 'Initial Test Report Name', 'https://www.some-url.com',
        '2024-03-18T12:10:11.814596',
        2, 'Bill Evans');

INSERT INTO GPFD.CSV_TO_SQL_MAPPING_TABLE(ID,REPORT_NAME,SQL_QUERY,BASE_URL,REPORT_OWNER,REPORT_CREATOR,REPORT_DESCRIPTION,EXCEL_REPORT,EXCEL_SHEET_NUM,CSV_NAME,OWNER_EMAIL)
VALUES (1,'CCMS_invoice_analysis-CIS-to-CCMS-import-analysis-2','SELECT * FROM ANY_REPORT.V_CIS_TO_CCMS_INVOICE_SUMMARY',' https://justiceuk.sharepoint.com/:x:/r/sites/FinanceSysReference/Shared%20Documents/General/Monthly%20Accounts/Sharepoint%20base%20reports/General%20CCMS%20Tools/CCMS%20invoice%20analysis.xlsb?d=w7bc78b4b2c94489e899415353a37d234&csf=1&web=1&e=K0h3OD','Chancey Mctavish','Sophia Patel','Summary of invoices in CIS and CCMS by original source IT system','CCMS_invoice_analysis',2,'CIS-to-CCMS-import-analysis','owneremail@email.com');