package uk.gov.laa.pfla.auth.service.models.report_view_models;

public interface ReportModel {

    //this interface is purely used to group the report view model classes together, so we can process them interchangeably, in methods using generics

    //Each class in report_view_models should correspond to a database view in the MOJFIN DB


    //Implement this method with logic to convert a stream of DB data to CSV file format
//    String asCSV();

}
