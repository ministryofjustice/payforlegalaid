package uk.gov.laa.pfla.auth.service.models;

import lombok.Data;

@Data
public class MappingTableModel {

    private double id;
    private String report_name;
    private String report_period;

    private String report_owner;
    private String report_created_by;
    private String report_description;
    private String sql;

    public MappingTableModel(double id, String report_name, String report_period, String report_owner, String report_created_by, String report_description, String sql) {
        this.id = id;
        this.report_name = report_name;
        this.report_period = report_period;
        this.report_owner = report_owner;
        this.report_created_by = report_created_by;
        this.report_description = report_description;
        this.sql = sql;
    }




}
