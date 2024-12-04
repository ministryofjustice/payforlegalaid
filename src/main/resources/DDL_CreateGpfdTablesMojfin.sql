--------------------------------------------------------
--  File created - Tuesday-December-12-2023   
--------------------------------------------------------
DROP TABLE "GPFD"."CSV_TO_SQL_MAPPING_TABLE";
DROP TABLE "GPFD"."REPORT_TRACKING";
--------------------------------------------------------
--  DDL for Table CSV_TO_SQL_MAPPING_TABLE
--------------------------------------------------------

  CREATE TABLE "GPFD"."CSV_TO_SQL_MAPPING_TABLE" 
   (	"ID" NUMBER(*,0), 
	"REPORT_NAME" VARCHAR2(200 BYTE), 
	"SQL_QUERY" VARCHAR2(4000 BYTE),
	"BASE_URL" VARCHAR2(450 BYTE), 
	"REPORT_OWNER" VARCHAR2(100 BYTE), 
	"REPORT_CREATOR" VARCHAR2(100 BYTE), 
	"REPORT_DESCRIPTION" VARCHAR2(400 BYTE), 
	"EXCEL_REPORT" VARCHAR2(200 BYTE), 
	"EXCEL_SHEET_NUM" NUMBER, 
	"CSV_NAME" VARCHAR2(200 BYTE), 
	"OWNER_EMAIL" VARCHAR2(300 BYTE)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
  GRANT SELECT ON "GPFD"."CSV_TO_SQL_MAPPING_TABLE" TO "ANY_REPORT";
--------------------------------------------------------
--  DDL for Table REPORT_TRACKING
--------------------------------------------------------

  CREATE TABLE "GPFD"."REPORT_TRACKING" 
   (	"ID" NUMBER(*,0), 
	"REPORT_NAME" VARCHAR2(150 BYTE), 
	"REPORT_URL" VARCHAR2(150 BYTE), 
	"CREATION_TIME" DATE, 
	"MAPPING_ID" NUMBER(*,0), 
	"REPORT_GENERATED_BY" VARCHAR2(50 BYTE)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
  GRANT SELECT ON "GPFD"."REPORT_TRACKING" TO "ANY_REPORT";
-- INSERTING into GPFD.CSV_TO_SQL_MAPPING_TABLE
SET DEFINE OFF;
Insert into GPFD.CSV_TO_SQL_MAPPING_TABLE (ID,REPORT_NAME,SQL_QUERY,BASE_URL,REPORT_OWNER,REPORT_CREATOR,REPORT_DESCRIPTION,EXCEL_REPORT,EXCEL_SHEET_NUM,CSV_NAME,OWNER_EMAIL) values (1,'CCMS_invoice_analysis-CIS-to-CCMS-import-analysis-2','SELECT * FROM ANY_REPORT.V_CIS_TO_CCMS_INVOICE_SUMMARY',' https://justiceuk.sharepoint.com/:x:/r/sites/FinanceSysReference/Shared%20Documents/General/Monthly%20Accounts/Sharepoint%20base%20reports/General%20CCMS%20Tools/CCMS%20invoice%20analysis.xlsb?d=w7bc78b4b2c94489e899415353a37d234&csf=1&web=1&e=K0h3OD','Chancey Mctavish','Sophia Patel','Summary of invoices in CIS and CCMS by original source IT system','CCMS_invoice_analysis',2,'CIS-to-CCMS-import-analysis','owneremail@email.com');
Insert into GPFD.CSV_TO_SQL_MAPPING_TABLE (ID,REPORT_NAME,SQL_QUERY,BASE_URL,REPORT_OWNER,REPORT_CREATOR,REPORT_DESCRIPTION,EXCEL_REPORT,EXCEL_SHEET_NUM,CSV_NAME,OWNER_EMAIL) values (2,'CCMS_and_CIS_Bank_Account_Report_w_Category_Code_(MNTH)-MAIN-12','SELECT * FROM ANY_REPORT.V_BANK_MONTH','https://justiceuk.sharepoint.com/sites/FinanceSysReference/Shared%20Documents/Forms/AllItems.aspx?id=%2Fsites%2FFinanceSysReference%2FShared%20Documents%2FGeneral%2FMonthly%20Accounts%2F2023%2D06%2FBank%20reporting&viewid=5d6ec327%2D2975%2D4d7c%2Dbd8d%2D6b793c45868b','Daniel Mctavish','Brian Limond','Summary of all payments made by CCMS/CIS and all cash receipts applied to debt in the previous month','CCMS_and_CIS_Bank_Account_Report_w_Category_Code_(MNTH)',12,'MAIN','secondowneremail@email.com');
Insert into GPFD.CSV_TO_SQL_MAPPING_TABLE (ID,REPORT_NAME,SQL_QUERY,BASE_URL,REPORT_OWNER,REPORT_CREATOR,REPORT_DESCRIPTION,EXCEL_REPORT,EXCEL_SHEET_NUM,CSV_NAME,OWNER_EMAIL) values (3,'CIS to CCMS import exceptions','SELECT * FROM ANY_REPORT.V_CIS_TO_CCMS_INVOICE_EXCEPTIONS','https://justiceuk.sharepoint.com/sites/FinanceSysReference/SitePages/GPFD-Test-Site-page.aspx','William Moran','William Unwin','Details of invoices transferred from CIS to CCMS by Legal Aid Scheme','CIS_TO_CCMS_IMPORT_EXCEPTIONS',3,'MAIN','William.Moran@Justice.gov.uk');
Insert into GPFD.CSV_TO_SQL_MAPPING_TABLE (ID,REPORT_NAME,SQL_QUERY,BASE_URL,REPORT_OWNER,REPORT_CREATOR,REPORT_DESCRIPTION,EXCEL_REPORT,EXCEL_SHEET_NUM,CSV_NAME,OWNER_EMAIL) values (4,'CIS to CCMS payment value Defined','SELECT * FROM ANY_REPORT.V_CIS_TO_CCMS_INVOICE_DEFINED_PAYMENT_GROUPS','https://justiceuk.sharepoint.com/sites/FinanceSysReference/SitePages/GPFD-Test-Site-page.aspx','William Moran','William Unwin','Details of invoices transferred from CIS to CCMS by Legal Aid Scheme','CIS_TO_CCMS_PAYMENT_VALUE_DEFINED',4,'MAIN','William.Moran@Justice.gov.uk');
-- INSERTING into GPFD.REPORT_TRACKING
SET DEFINE OFF;
