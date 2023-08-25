package uk.gov.laa.pfla.auth.service.dao;

import org.springframework.stereotype.Repository;
import uk.gov.laa.pfla.auth.service.builders.MappingTableModelBuilder;
import uk.gov.laa.pfla.auth.service.models.MappingTableModel;

import java.util.ArrayList;
import java.util.List;
@Repository
public class MappingTableDao {

    private final List<MappingTableModel> mappingTableObjectList = new ArrayList<>();
    public MappingTableDao() {
        //empty contructor to allow builder to do its work
    }

    public List<MappingTableModel> fetchReportList() {

        mappingTableObjectList.clear(); // Prevent response data accumulating after multiple requests

        //fetch data from database
        //fetchSingleRowOfData()

        //create a list of MappingTableModel objects
        MappingTableModel mappingTableObject1 = new MappingTableModelBuilder()
                .withId(1).withReportName("Excel_Report_Name-CSV-NAME-sheetnumber")
                .withReportPeriod("01/08/2023 - 01/09/2023")
                .withReportOwner("Chancey Mctavish")
                .withReportCreator("Barry Gibb")
                .withReportDescription("List all unpaid AP invoices and all outstanding AR debts at the end of the previous month. Detailed data, one row per invoice")
                .withBaseUrl("www.sharepoint.com/the-folder-we're-using")
                .withSql("SELECT * FROM SOMETHING")
                .createMappingTableModel();



        MappingTableModel mappingTableObject2 = new MappingTableModelBuilder()
                .withId(2).withReportName("AP_and_AR_Combined-DEBT-AGING-SUMMARY-4")
                .withReportPeriod("01/07/2023 - 01/09/2023")
                .withReportOwner("Chancey Mctavish")
                .withReportCreator("Sophia Patel")
                .withReportDescription("List all unpaid AP invoices and all outstanding AR debts at the end of the previous month. Summary data, one row per provider")
                .withBaseUrl("www.sharepoint.com/a-different-folder-we're-using")
                .withSql("SELECT * FROM SOMETHING")
                .createMappingTableModel();



        mappingTableObjectList.add(0, mappingTableObject1);
        mappingTableObjectList.add(1, mappingTableObject2);
        return mappingTableObjectList;


    }


}
