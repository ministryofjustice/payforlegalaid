CREATE SCHEMA IF NOT EXISTS GPFD;

CREATE SEQUENCE IF NOT EXISTS GPFD_TRACKING_TABLE_SEQUENCE
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS GPFD.REPORT_TRACKING
(
    ID                  UUID         NOT NULL DEFAULT sys_guid(),
    REPORT_NAME         VARCHAR(80)  NOT NULL,
    REPORT_URL          VARCHAR(500) NOT NULL,
    CREATION_TIME       TIMESTAMP    NOT NULL,
    MAPPING_ID          UUID      NOT NULL,
    REPORT_GENERATED_BY VARCHAR(500) NOT NULL,
    CONSTRAINT pk_report_tracking_id PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS GPFD.CSV_TO_SQL_MAPPING_TABLE
(
    ID                  UUID         NOT NULL DEFAULT sys_guid(),
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

CREATE TABLE IF NOT EXISTS GPFD.REPORT_OUTPUT_TYPES
(
    ID                  UUID         NOT NULL DEFAULT sys_guid(),
    EXTENSION           VARCHAR(20)  NOT NULL,
    DESCRIPTION         VARCHAR(150) NOT NULL,
    CONSTRAINT pk_id PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS GPFD.REPORTS
(
    ID                             UUID          NOT NULL DEFAULT sys_guid(),
    NAME                           VARCHAR(150)  NOT NULL,
    TEMPLATE_SECURE_DOCUMENT_ID    VARCHAR(300)  NOT NULL,
    REPORT_CREATION_DATE           DATE          NULL,
    LAST_DATABASE_REFRESH_DATETIME TIMESTAMP     NULL,
    DESCRIPTION                    VARCHAR(4000) NULL,
    NUM_DAYS_TO_KEEP               INT           NULL,
    REPORT_OUTPUT_TYPE             UUID          NOT NULL,
    REPORT_CREATOR_ID              UUID          NOT NULL,
    REPORT_CREATOR_NAME            VARCHAR(150)  NOT NULL,
    REPORT_CREATOR_EMAIL           VARCHAR(150)  NOT NULL,
    REPORT_OWNER_ID                UUID          NOT NULL,
    REPORT_OWNER_NAME              UUID          NOT NULL,
    REPORT_OWNER_EMAIL             VARCHAR(150)  NOT NULL,
    CONSTRAINT pk_id PRIMARY KEY (id),
    CONSTRAINT fk_report_output_type_id FOREIGN KEY (REPORT_OUTPUT_TYPE) REFERENCES REPORT_OUTPUT_TYPES(id)
);

CREATE TABLE IF NOT EXISTS GPFD.REPORT_GROUPS
(
    REPORT_ID             UUID          NOT NULL,
    GROUP_ID              UUID          NOT NULL,
    CONSTRAINT fk_reports_id FOREIGN KEY (REPORT_ID) REFERENCES REPORTS(id)
);

CREATE TABLE IF NOT EXISTS GPFD.REPORT_QUERIES
(
    ID               UUID           NOT NULL DEFAULT sys_guid(),
    REPORT_ID        UUID           NOT NULL,
    QUERY            VARCHAR(4000)  NOT NULL,
    TAB_NAME         VARCHAR(100)  NOT NULL,
    CONSTRAINT pk_id PRIMARY KEY (id),
    CONSTRAINT fk_reports_id FOREIGN KEY (REPORT_ID) REFERENCES REPORTS(id)
);

CREATE TABLE IF NOT EXISTS GPFD.FIELD_ATTRIBUTES
(
    ID               UUID           NOT NULL DEFAULT sys_guid(),
    CONSTRAINT pk_id PRIMARY KEY (id),
);


<createTable tableName="" schemaName="GPFD">
            <column name="REPORT_QUERY_ID" type="CHAR (36)">
                <constraints nullable="false" />
            </column>
            <column name="SOURCE_NAME" type="VARCHAR(100)">
                <constraints nullable="false"/>
            </column>
            <column name="MAPPED_NAME" type="VARCHAR(100)">
                <constraints nullable="false"/>
            </column>
            <column name="FORMAT" type="VARCHAR(100)">
                <constraints nullable="false"/>
            </column>
            <column name="FORMAT_TYPE" type="VARCHAR(100)">
                <constraints nullable="false"/>
            </column>
            <column name="COLUMN_WIDTH" type="int">
                <constraints nullable="false"/>
            </column>
        </createTable>
