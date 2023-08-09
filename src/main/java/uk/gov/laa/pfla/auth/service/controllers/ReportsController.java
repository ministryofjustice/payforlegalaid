package uk.gov.laa.pfla.auth.service.controllers;

import org.springframework.http.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;
import uk.gov.laa.pfla.auth.service.ReportListResponse;
import uk.gov.laa.pfla.auth.service.ReportResponse;
import java.time.LocalDateTime;


import java.util.*;

@RestController
@EnableAutoConfiguration
public class ReportsController {

    /**
     * Method to allow the user to see a list of all available reports, which are available to generate and download
     * @return A POJO list, converted to json by spring -  A list of report names, id's and some information on each report, in the form of json objects
     */
    @RequestMapping("/reports")
    ResponseEntity<Object>getReportList() {

        ReportListResponse response1 = new ReportListResponse(
                1,
                "V_AP_AR_COMBINED_DATA",
                "01/08/2023 - 01/09/2023",
                "Chancey Mctavish",
                "Alan Rachnid",
                "List all unpaid AP invoices and all outstanding AR debts at the end of the previous month. Detailed data, one row per invoice",
                "www.sharepoint.com/the-folder-we're-using");

        ReportListResponse response2 = new ReportListResponse(
                2,
                "V_AP_AR_DEBT_AGING_SUMMARY",
                "01/07/2023 - 01/09/2023",
                "Chancey Mctavish",
                "Sophia Patel",
                "List all unpaid AP invoices and all outstanding AR debts at the end of the previous month. Summary data, one row per provider",
                "www.sharepoint.com/a-different-folder-we're-using");


        List<ReportListResponse> reportList = new ArrayList<>();
        reportList.add(0, response1);
        reportList.add(1, response2);



        return new ResponseEntity<>(reportList, HttpStatus.OK);

    }

    /**
     *
     * @return A SingleReportResponse POJO, converted to JSON by spring - this is a single JSON object which contains the name, id and url of a report
     */
    @RequestMapping(value ="/report/{id}")
    ResponseEntity<Object> getReport(@PathVariable(value="id") int requestedId) {

        LocalDateTime placeHolderDateTime = LocalDateTime.now();

        ReportResponse report1 = new ReportResponse(
                requestedId,
                "V_AP_AR_COMBINED_DATA",
                "www.sharepoint.com/an-example-report.csv",
                placeHolderDateTime);



        return new ResponseEntity<>(report1, HttpStatus.OK);

    }

}