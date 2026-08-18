CREATE TABLE IF NOT EXISTS glad.report_tracking (
    id                  UUID NOT NULL,
    report_id           UUID NOT NULL,
    user_id             UUID NOT NULL,
    download_time       TIMESTAMPTZ NOT NULL,
    CONSTRAINT pk_report_tracking PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_report_tracking_report_id ON glad.report_tracking(report_id);
CREATE INDEX IF NOT EXISTS idx_report_tracking_user_id ON glad.report_tracking(user_id);
