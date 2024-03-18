CREATE TABLE report_tracking (
                        id number NOT NULL,
                        report_name varchar(80) NOT NULL,         -- Course Title
                        report_url varchar(500) NOT NULL,  -- Course Description
                        creation_time Date NOT NULL,  -- time request was made
                        mapping_id number NOT NULL,
                        report_generated_by varchar(500) NOT NULL,
                        CONSTRAINT pk_report_tracking_id PRIMARY KEY (id)
);