CREATE SCHEMA IF NOT EXISTS GPFD;

CREATE SEQUENCE IF NOT EXISTS GPFD_TRACKING_TABLE_SEQUENCE
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS GPFD.REPORT_TRACKING
(
    ID                  INTEGER      NOT NULL,
    REPORT_NAME         VARCHAR(80)  NOT NULL,
    REPORT_URL          VARCHAR(500) NOT NULL,
    CREATION_TIME       TIMESTAMP    NOT NULL,
    MAPPING_ID          INTEGER      NOT NULL,
    REPORT_GENERATED_BY VARCHAR(500) NOT NULL,
    CONSTRAINT pk_report_tracking_id PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS GPFD.CSV_TO_SQL_MAPPING_TABLE
(
    ID                  INTEGER       NOT NULL,
    REPORT_NAME         VARCHAR(500)  NOT NULL,
    SQL_QUERY           VARCHAR(500)  NOT NULL,
    BASE_URL            VARCHAR(500)  NOT NULL,
    REPORT_OWNER        VARCHAR(500)  NOT NULL,
    REPORT_CREATOR      VARCHAR(500)  NOT NULL,
    REPORT_DESCRIPTION  VARCHAR(500)  NOT NULL,
    EXCEL_REPORT        VARCHAR(500)  NOT NULL,
    EXCEL_SHEET_NUM     INTEGER       NOT NULL,
    CSV_NAME            VARCHAR(500)  NOT NULL,
    OWNER_EMAIL         VARCHAR(500)  NOT NULL,
    CONSTRAINT pk_csv_to_sql_mapping_table_id PRIMARY KEY (id)
);