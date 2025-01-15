--------------------------------------------------------
--  File created - Tuesday-December-12-2023   
--------------------------------------------------------
DROP TABLE "GPFD"."CSV_TO_SQL_MAPPING_TABLE";
DROP TABLE "GPFD"."REPORT_TRACKING";
--------------------------------------------------------
--  Create functions
--------------------------------------------------------
CREATE OR REPLACE PACKAGE utils_pkg
AS
  FUNCTION generate_uuid
  RETURN VARCHAR2;
END utils_pkg;
/

CREATE OR REPLACE PACKAGE BODY utils_pkg
AS
  FUNCTION generate_uuid
  RETURN VARCHAR2
  IS
    v_uuid VARCHAR2(36);
  BEGIN
    SELECT LOWER(REGEXP_REPLACE(RAWTOHEX(SYS_GUID()), '([A-F0-9]{8})([A-F0-9]{4})([A-F0-9]{4})([A-F0-9]{4})([A-F0-9]{12})', '\1-\2-\3-\4-\5'))
    INTO v_uuid
    FROM DUAL;

    RETURN v_uuid;
  END generate_uuid;
END utils_pkg;
/
--------------------------------------------------------
--  DDL for Table CSV_TO_SQL_MAPPING_TABLE
--------------------------------------------------------

  CREATE TABLE "GPFD"."CSV_TO_SQL_MAPPING_TABLE2"
   (
    "ID" CHAR(36 BYTE),
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
   (
    "ID" CHAR(36 BYTE),
	"REPORT_NAME" VARCHAR2(150 BYTE), 
	"REPORT_URL" VARCHAR2(150 BYTE), 
	"CREATION_TIME" DATE, 
	"MAPPING_ID" CHAR(36 BYTE),
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

/
DECLARE
  base_url CONSTANT VARCHAR2(400) := 'https://justiceuk.sharepoint.com/sites/FinanceSysReference/SitePages/GPFD-Test-Site-page.aspx';
  report_owner CONSTANT VARCHAR2(400) := 'William Moran';
  report_creator CONSTANT VARCHAR2(400) := 'William Unwin';
  owner_email CONSTANT VARCHAR2(400) := 'William.Moran@Justice.gov.uk';
  generic_report_description CONSTANT VARCHAR2(400) := 'Details of invoices transferred from CIS to CCMS by Legal Aid Scheme';
BEGIN
    Insert into GPFD.CSV_TO_SQL_MAPPING_TABLE (ID,REPORT_NAME,SQL_QUERY,BASE_URL,REPORT_OWNER,REPORT_CREATOR,REPORT_DESCRIPTION,EXCEL_REPORT,EXCEL_SHEET_NUM,CSV_NAME,OWNER_EMAIL) values (utils_pkg.generate_uuid(), 'CCMS_invoice_analysis-CIS-to-CCMS-import-analysis-2','SELECT * FROM ANY_REPORT.V_CIS_TO_CCMS_INVOICE_SUMMARY',' https://justiceuk.sharepoint.com/:x:/r/sites/FinanceSysReference/Shared%20Documents/General/Monthly%20Accounts/Sharepoint%20base%20reports/General%20CCMS%20Tools/CCMS%20invoice%20analysis.xlsb?d=w7bc78b4b2c94489e899415353a37d234','Chancey Mctavish','Sophia Patel','Summary of invoices in CIS and CCMS by original source IT system','CCMS_invoice_analysis',2,'CIS-to-CCMS-import-analysis','owneremail@email.com');
    Insert into GPFD.CSV_TO_SQL_MAPPING_TABLE (ID,REPORT_NAME,SQL_QUERY,BASE_URL,REPORT_OWNER,REPORT_CREATOR,REPORT_DESCRIPTION,EXCEL_REPORT,EXCEL_SHEET_NUM,CSV_NAME,OWNER_EMAIL) values (utils_pkg.generate_uuid(), 'CCMS_and_CIS_Bank_Account_Report_w_Category_Code_(MNTH)-MAIN-12','SELECT * FROM ANY_REPORT.V_BANK_MONTH','https://justiceuk.sharepoint.com/sites/FinanceSysReference/Shared%20Documents/Forms/AllItems.aspx?id=%2Fsites%2FFinanceSysReference%2FShared%20Documents%2FGeneral%2FMonthly%20Accounts%2F2023%2D06%2FBank%20reporting','Daniel Mctavish','Brian Limond','Summary of all payments made by CCMS/CIS and all cash receipts applied to debt in the previous month','CCMS_and_CIS_Bank_Account_Report_w_Category_Code_(MNTH)',12,'MAIN','secondowneremail@email.com');
    Insert into GPFD.CSV_TO_SQL_MAPPING_TABLE (ID,REPORT_NAME,SQL_QUERY,BASE_URL,REPORT_OWNER,REPORT_CREATOR,REPORT_DESCRIPTION,EXCEL_REPORT,EXCEL_SHEET_NUM,CSV_NAME,OWNER_EMAIL) values (utils_pkg.generate_uuid(), 'CIS to CCMS import exceptions','SELECT * FROM ANY_REPORT.V_CIS_TO_CCMS_INVOICE_EXCEPTIONS',base_url,report_owner,report_creator,generic_report_description,'CIS_TO_CCMS_IMPORT_EXCEPTIONS',3,'MAIN',owner_email);
    Insert into GPFD.CSV_TO_SQL_MAPPING_TABLE (ID,REPORT_NAME,SQL_QUERY,BASE_URL,REPORT_OWNER,REPORT_CREATOR,REPORT_DESCRIPTION,EXCEL_REPORT,EXCEL_SHEET_NUM,CSV_NAME,OWNER_EMAIL) values (utils_pkg.generate_uuid(), 'CIS to CCMS payment value Defined','SELECT * FROM ANY_REPORT.V_CIS_TO_CCMS_INVOICE_DEFINED_PAYMENT_GROUPS',base_url,report_owner,report_creator,generic_report_description,'CIS_TO_CCMS_PAYMENT_VALUE_DEFINED',4,'MAIN',owner_email);
    Insert into GPFD.CSV_TO_SQL_MAPPING_TABLE (ID,REPORT_NAME,SQL_QUERY,BASE_URL,REPORT_OWNER,REPORT_CREATOR,REPORT_DESCRIPTION,EXCEL_REPORT,EXCEL_SHEET_NUM,CSV_NAME,OWNER_EMAIL) values (utils_pkg.generate_uuid(),'CIS to CCMS payment value Not Defined','SELECT * FROM ANY_REPORT.V_CIS_TO_CCMS_INVOICE_NOT_DEFINED_PAYMENT_GROUPS',base_url,report_owner,report_creator,generic_report_description,'CIS_TO_CCMS_PAYMENT_VALUE_NOT_DEFINED',5,'MAIN',owner_email);
    Insert into GPFD.CSV_TO_SQL_MAPPING_TABLE (ID,REPORT_NAME,SQL_QUERY,BASE_URL,REPORT_OWNER,REPORT_CREATOR,REPORT_DESCRIPTION,EXCEL_REPORT,EXCEL_SHEET_NUM,CSV_NAME,OWNER_EMAIL) values (utils_pkg.generate_uuid(), 'CCMS Held Payments','SELECT * FROM ANY_REPORT.V_CIS_TO_CCMS_INVOICE_HELD_PAYMENTS',base_url,report_owner,report_creator,generic_report_description,'CCMS_HELD_PAYMENTS',6,'MAIN',owner_email);
    Insert into GPFD.CSV_TO_SQL_MAPPING_TABLE (ID,REPORT_NAME,SQL_QUERY,BASE_URL,REPORT_OWNER,REPORT_CREATOR,REPORT_DESCRIPTION,EXCEL_REPORT,EXCEL_SHEET_NUM,CSV_NAME,OWNER_EMAIL) values (utils_pkg.generate_uuid(), 'CCMS AP Debtors','SELECT * FROM ANY_REPORT.V_CIS_TO_CCMS_INVOICE_CCMS_AP_DEBTS',base_url,report_owner,report_creator,generic_report_description,'CCMS_AP_DEBTORS',7,'MAIN',owner_email);
    Insert into GPFD.CSV_TO_SQL_MAPPING_TABLE (ID,REPORT_NAME,SQL_QUERY,BASE_URL,REPORT_OWNER,REPORT_CREATOR,REPORT_DESCRIPTION,EXCEL_REPORT,EXCEL_SHEET_NUM,CSV_NAME,OWNER_EMAIL) values (utils_pkg.generate_uuid(), 'CCMS_and_CIS_Bank_Account_Report_w_Category_Code_YTD','SELECT * FROM ANY_REPORT.V_BANK_YTD',base_url,report_owner,report_creator,'Summary of all payments made by CCMS/CIS and all cash receipts applied to debt YTD','CCMS_and_CIS_Bank_Account_Report_w_Category_Code_YTD',8,'MAIN',owner_email);
    Insert into GPFD.CSV_TO_SQL_MAPPING_TABLE (ID,REPORT_NAME,SQL_QUERY,BASE_URL,REPORT_OWNER,REPORT_CREATOR,REPORT_DESCRIPTION,EXCEL_REPORT,EXCEL_SHEET_NUM,CSV_NAME,OWNER_EMAIL) values (utils_pkg.generate_uuid(), 'CCMS General Ledger (tiny columns multiple periods)','SELECT * FROM ANY_REPORT.V_CCMS_GL_EXTRACTOR_YTD',base_url,report_owner,report_creator,'CCMS General ledger extractor (tiny columns multiple periods)','CCMS_GEN_LEDGER_TINYCOL_MULPERIODS',9,'MAIN',owner_email);
    Insert into GPFD.CSV_TO_SQL_MAPPING_TABLE (ID,REPORT_NAME,SQL_QUERY,BASE_URL,REPORT_OWNER,REPORT_CREATOR,REPORT_DESCRIPTION,EXCEL_REPORT,EXCEL_SHEET_NUM,CSV_NAME,OWNER_EMAIL) values (utils_pkg.generate_uuid(), 'CCMS General Ledger (tiny columns multiple periods manual)','SELECT * FROM ANY_REPORT.V_CCMS_GL_EXTRACTOR_MANUAL_YTD',base_url,report_owner,report_creator,'CCMS General ledger extractor (tiny columns multiple periods manual)','CCMS_GEN_LEDGER_TINYCOL_MULPERIODS_MANUAL',10,'MAIN',owner_email);
    Insert into GPFD.CSV_TO_SQL_MAPPING_TABLE (ID,REPORT_NAME,SQL_QUERY,BASE_URL,REPORT_OWNER,REPORT_CREATOR,REPORT_DESCRIPTION,EXCEL_REPORT,EXCEL_SHEET_NUM,CSV_NAME,OWNER_EMAIL) values (utils_pkg.generate_uuid(), 'CCMS Authorised expenditure (Contracting live system)','SELECT * FROM ANY_REPORT.V_CCMS_AUTHORISED_INVS_CONTRACTING',base_url,report_owner,report_creator,'CCMS Authorised expenditure (Contracting live system)','CCMS_AUTH_EXPEND_CONTRACTING',11,'MAIN',owner_email);
END;
/

-- INSERTING into GPFD.REPORT_TRACKING
SET DEFINE OFF;
