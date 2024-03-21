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