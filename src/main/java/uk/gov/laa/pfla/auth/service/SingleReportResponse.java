package uk.gov.laa.pfla.auth.service;

import lombok.Data;

@Data

public class SingleReportResponse {

    private double id;
    private String report_name;
    private String report_url;

    public SingleReportResponse(double id, String report_name, String report_url) {
        this.id = id;
        this.report_name = report_name;
        this.report_url = report_url;
    }
}
