INSERT INTO GPFD.REPORT_TRACKING(ID, REPORT_NAME, REPORT_URL, CREATION_TIME, MAPPING_ID, REPORT_GENERATED_BY)
VALUES (GPFD_TRACKING_TABLE_SEQUENCE.NEXTVAL, 'Initial Test Report Name', 'https://www.some-url.com',
        '2024-03-18T12:10:11.814596',
        2, 'Bill Evans');

INSERT INTO GPFD.CSV_TO_SQL_MAPPING_TABLE(ID,REPORT_NAME,SQL_QUERY,BASE_URL,REPORT_OWNER,REPORT_CREATOR,REPORT_DESCRIPTION,EXCEL_REPORT,EXCEL_SHEET_NUM,CSV_NAME,OWNER_EMAIL)
VALUES (1,'CCMS_invoice_analysis-CIS-to-CCMS-import-analysis-2','SELECT * FROM ANY_REPORT.V_CIS_TO_CCMS_INVOICE_SUMMARY',' https://justiceuk.sharepoint.com/:x:/r/sites/FinanceSysReference/Shared%20Documents/General/Monthly%20Accounts/Sharepoint%20base%20reports/General%20CCMS%20Tools/CCMS%20invoice%20analysis.xlsb?d=w7bc78b4b2c94489e899415353a37d234&csf=1&web=1&e=K0h3OD','Chancey Mctavish','Sophia Patel','Summary of invoices in CIS and CCMS by original source IT system','CCMS_invoice_analysis',2,'CIS-to-CCMS-import-analysis','owneremail@email.com'),
(2,'CCMS_and_CIS_Bank_Account_Report_w_Category_Code_(MNTH)-MAIN-12','SELECT * FROM ANY_REPORT.V_BANK_MONTH','https://justiceuk.sharepoint.com/sites/FinanceSysReference/Shared%20Documents/Forms/AllItems.aspx?id=%2Fsites%2FFinanceSysReference%2FShared%20Documents%2FGeneral%2FMonthly%20Accounts%2F2023%2D06%2FBank%20reporting&viewid=5d6ec327%2D2975%2D4d7c%2Dbd8d%2D6b793c45868b','Daniel Mctavish','Brian Limond','Summary of all payments made by CCMS/CIS and all cash receipts applied to debt in the previous month','CCMS_and_CIS_Bank_Account_Report_w_Category_Code_(MNTH)',12,'MAIN','secondowneremail@email.com'),
(3,'CIS to CCMS import exceptions','SELECT * FROM ANY_REPORT.V_CIS_TO_CCMS_INVOICE_EXCEPTIONS','https://justiceuk.sharepoint.com/sites/FinanceSysReference/SitePages/GPFD-Test-Site-page.aspx','William Moran','William Unwin','Details of invoices transferred from CIS to CCMS by Legal Aid Scheme','CIS_TO_CCMS_IMPORT_EXCEPTIONS',3,'MAIN','William.Moran@Justice.gov.uk'),
(4,'CIS to CCMS payment value Defined','SELECT * FROM ANY_REPORT.V_CIS_TO_CCMS_INVOICE_DEFINED_PAYMENT_GROUPS','https://justiceuk.sharepoint.com/sites/FinanceSysReference/SitePages/GPFD-Test-Site-page.aspx','William Moran','William Unwin','Details of invoices transferred from CIS to CCMS by Legal Aid Scheme','CIS_TO_CCMS_PAYMENT_VALUE_DEFINED',4,'MAIN','William.Moran@Justice.gov.uk'),
(5,'CIS to CCMS payment value Not Defined','SELECT * FROM ANY_REPORT.V_CIS_TO_CCMS_INVOICE_NOT_DEFINED_PAYMENT_GROUPS','https://justiceuk.sharepoint.com/sites/FinanceSysReference/SitePages/GPFD-Test-Site-page.aspx','William Moran','William Unwin','Details of invoices transferred from CIS to CCMS by Legal Aid Scheme','CIS_TO_CCMS_PAYMENT_VALUE_NOT_DEFINED',5,'MAIN','William.Moran@Justice.gov.uk'),
(6,'CCMS Held Payments','SELECT * FROM ANY_REPORT.V_CIS_TO_CCMS_INVOICE_HELD_PAYMENTS','https://justiceuk.sharepoint.com/sites/FinanceSysReference/SitePages/GPFD-Test-Site-page.aspx','William Moran','William Unwin','Details of invoices transferred from CIS to CCMS by Legal Aid Scheme','CCMS_HELD_PAYMENTS',6,'MAIN','William.Moran@Justice.gov.uk'),
(7,'CCMS AP Debtors','SELECT * FROM ANY_REPORT.V_CIS_TO_CCMS_INVOICE_CCMS_AP_DEBTS','https://justiceuk.sharepoint.com/sites/FinanceSysReference/SitePages/GPFD-Test-Site-page.aspx','William Moran','William Unwin','Details of invoices transferred from CIS to CCMS by Legal Aid Scheme','CCMS_AP_DEBTORS',7,'MAIN','William.Moran@Justice.gov.uk'),
(8,'CCMS_and_CIS_Bank_Account_Report_w_Category_Code_YTD','SELECT * FROM ANY_REPORT.V_BANK_YTD','https://justiceuk.sharepoint.com/sites/FinanceSysReference/SitePages/GPFD-Test-Site-page.aspx','William Moran','William Unwin','Summary of all payments made by CCMS/CIS and all cash receipts applied to debt YTD','CCMS_and_CIS_Bank_Account_Report_w_Category_Code_YTD',8,'MAIN','William.Moran@Justice.gov.uk'),
(9,'CCMS General Ledger (tiny columns multiple periods)','SELECT * FROM ANY_REPORT.V_CCMS_GL_EXTRACTOR_YTD','https://justiceuk.sharepoint.com/sites/FinanceSysReference/SitePages/GPFD-Test-Site-page.aspx','William Moran','William Unwin','CCMS General ledger extractor (tiny columns multiple periods)','CCMS_GEN_LEDGER_TINYCOL_MULPERIODS',9,'MAIN','William.Moran@Justice.gov.uk'),
(10,'CCMS General Ledger (tiny columns multiple periods manual)','SELECT * FROM ANY_REPORT.V_CCMS_GL_EXTRACTOR_MANUAL_YTD','https://justiceuk.sharepoint.com/sites/FinanceSysReference/SitePages/GPFD-Test-Site-page.aspx','William Moran','William Unwin','CCMS General ledger extractor (tiny columns multiple periods manual)','CCMS_GEN_LEDGER_TINYCOL_MULPERIODS_MANUAL',10,'MAIN','William.Moran@Justice.gov.uk'),
(11,'CCMS Authorised expenditure (Contracting live system)','SELECT * FROM ANY_REPORT.V_CCMS_AUTHORISED_INVS_CONTRACTING','https://justiceuk.sharepoint.com/sites/FinanceSysReference/SitePages/GPFD-Test-Site-page.aspx','William Moran','William Unwin','CCMS Authorised expenditure (Contracting live system)','CCMS_AUTH_EXPEND_CONTRACTING',11,'MAIN','William.Moran@Justice.gov.uk');