package uk.gov.laa.pfla.auth.service.controllers;

import org.springframework.http.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;
import uk.gov.laa.pfla.auth.service.models.MappingTableModel;
import uk.gov.laa.pfla.auth.service.SingleReportResponse;

import java.util.*;

@RestController
@EnableAutoConfiguration
public class ReportsController {

    /**
     * Method to allow the user to see a list of all available reports, which are available to generate and download
     * @return A POJO list, converted to json by spring -  A list of report names, id's and some information on each report, in the form of json objects
     */
    @RequestMapping("/report")
    ResponseEntity<Object>getReportList() {

        //POJO which will hold the data from MOJFIN Mappingtable
        MappingTableModel modelItem1 = new MappingTableModel(
                1,
                "V_AP_AR_COMBINED_DATA",
                "01/08/2023 - 01/09/2023",
                "Chancey Mctavish",
                "Alan Rachnid",
                "List all unpaid AP invoices and all outstanding AR debts at the end of the previous month. Detailed data, one row per invoice",
                "SELECT * FROM A_CERTAIN_DB_VIEW"); // TODO -  We don't want this field in the actual response, so need to agree best way to  deal with it (new response class, or create a new JSONObject and return that?)

        MappingTableModel modelItem2 = new MappingTableModel(
                2,
                "V_AP_AR_DEBT_AGING_SUMMARY",
                "01/07/2023 - 01/09/2023",
                "Chancey Mctavish",
                "Sophia Patel",
                "List all unpaid AP invoices and all outstanding AR debts at the end of the previous month. Summary data, one row per provider",
                "SELECT * FROM A_CERTAIN_DB_VIEW");


        List<MappingTableModel> reportList = new ArrayList<>();
        reportList.add(0, modelItem1);
        reportList.add(1, modelItem2);



        return new ResponseEntity<>(reportList, HttpStatus.OK);

    }

    /**
     *
     * @return A SingleReportResponse POJO, converted to JSON by spring - this is a single JSON object which contains the name, id and url of a report
     */
    @RequestMapping(value ="/report", method = RequestMethod.POST)
    ResponseEntity<Object> getReport() {


        SingleReportResponse singleReport1 = new SingleReportResponse(
                1,
                "V_AP_AR_COMBINED_DATA",
                "www.sharepoint.com/an-example-report.csv");





        return new ResponseEntity<>(singleReport1, HttpStatus.OK);

    }

}