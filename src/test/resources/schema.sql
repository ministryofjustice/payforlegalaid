CREATE TABLE report_tracking
(
    id                  INTEGER      NOT NULL,
    report_name         VARCHAR(80)  NOT NULL,
    report_url          VARCHAR(500) NOT NULL,
    creation_time       TIMESTAMP    NOT NULL,
    mapping_id          INTEGER      NOT NULL,
    report_generated_by VARCHAR(500) NOT NULL,
    CONSTRAINT pk_report_tracking_id PRIMARY KEY (id)
);