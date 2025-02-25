CREATE SCHEMA IF NOT EXISTS GPFD;

CREATE TABLE IF NOT EXISTS GPFD.REPORT_OUTPUT_TYPES
(
    ID                  UUID         NOT NULL DEFAULT sys_guid(),
    EXTENSION           VARCHAR(20)  NOT NULL,
    DESCRIPTION         VARCHAR(150) NOT NULL,
    CONSTRAINT pk_report_output_types_id PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS GPFD.REPORTS
(
    ID                             UUID          NOT NULL DEFAULT sys_guid(),
    NAME                           VARCHAR(150)  NOT NULL,
    TEMPLATE_SECURE_DOCUMENT_ID    VARCHAR(300)  NULL,
    REPORT_CREATION_DATE           DATE          NULL,
    LAST_DATABASE_REFRESH_DATETIME TIMESTAMP     NULL,
    DESCRIPTION                    VARCHAR(4000) NULL,
    NUM_DAYS_TO_KEEP               INT           NULL,
    REPORT_OUTPUT_TYPE             UUID          NOT NULL,
    REPORT_CREATOR_ID              UUID          NULL,
    REPORT_CREATOR_NAME            VARCHAR(150)  NULL,
    REPORT_CREATOR_EMAIL           VARCHAR(150)  NULL,
    REPORT_OWNER_ID                UUID          NOT NULL,
    REPORT_OWNER_NAME              VARCHAR(150)  NOT NULL,
    REPORT_OWNER_EMAIL             VARCHAR(150)  NOT NULL,
    FILE_NAME                      VARCHAR(150)  NULL,
    ACTIVE                         CHAR(1)       NULL,
    CONSTRAINT pk_reports_id PRIMARY KEY (id),
    CONSTRAINT fk_reports_report_output_type_id FOREIGN KEY (REPORT_OUTPUT_TYPE) REFERENCES REPORT_OUTPUT_TYPES(id)
);

CREATE TABLE IF NOT EXISTS GPFD.REPORT_GROUPS
(
    REPORT_ID             UUID          NOT NULL,
    GROUP_ID              UUID          NOT NULL,
    CONSTRAINT fk_report_groups_report_id FOREIGN KEY (REPORT_ID) REFERENCES REPORTS(id)
);

CREATE TABLE IF NOT EXISTS GPFD.REPORT_QUERIES
(
    ID               UUID           NOT NULL DEFAULT sys_guid(),
    REPORT_ID        UUID           NOT NULL,
    QUERY            VARCHAR(4000)  NOT NULL,
    TAB_NAME         VARCHAR(100)  NOT NULL,
    CONSTRAINT pk_report_queries_id PRIMARY KEY (id),
    CONSTRAINT fk_report_queries_report_id FOREIGN KEY (REPORT_ID) REFERENCES REPORTS(id)
);

CREATE TABLE IF NOT EXISTS GPFD.FIELD_ATTRIBUTES
(
    ID               UUID           NOT NULL DEFAULT sys_guid(),
    REPORT_QUERY_ID  UUID           NOT NULL,
    SOURCE_NAME      VARCHAR(100)   NOT NULL,
    MAPPED_NAME      VARCHAR(100)   NOT NULL,
    FORMAT           VARCHAR(100)   NULL,
    FORMAT_TYPE      VARCHAR(100)   NOT NULL,
    COLUMN_WIDTH     NUMBER(6,2)    NOT NULL,
    CONSTRAINT pk_field_attributes_id PRIMARY KEY (id),
    CONSTRAINT fk_field_attributes_report_query_id FOREIGN KEY (REPORT_QUERY_ID) REFERENCES REPORT_QUERIES(id)
);

CREATE TABLE IF NOT EXISTS GPFD.REPORTS_TRACKING
(
    ID                      UUID           NOT NULL DEFAULT sys_guid(),
    NAME                    VARCHAR(150)   NOT NULL,
    REPORT_ID               UUID           NOT NULL,
    CREATION_DATE           DATE           NULL,
    REPORT_DOWNLOADED_BY    VARCHAR(150)   NULL,
    REPORT_GENERATED_BY     VARCHAR(150)   NULL,
    REPORT_CREATOR          VARCHAR(150)   NOT NULL,
    REPORT_OWNER            VARCHAR(150)   NOT NULL,
    REPORT_OUTPUT_TYPE      VARCHAR(150)   NOT NULL,
    TEMPLATE_URL            VARCHAR(300)   NOT NULL,
    REPORT_URL              VARCHAR(300)   NOT NULL,
    CONSTRAINT pk_reports_tracking_id PRIMARY KEY (id),
    CONSTRAINT fk_reports_tracking_reports_id FOREIGN KEY (REPORT_ID) REFERENCES REPORTS(id)
);