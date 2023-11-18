package uk.gov.laa.pfla.auth.service.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;
import uk.gov.laa.pfla.auth.service.responses.ReportListResponse;
import uk.gov.laa.pfla.auth.service.responses.ReportResponse;
import uk.gov.laa.pfla.auth.service.services.MappingTableService;
import uk.gov.laa.pfla.auth.service.services.ReportService;
import uk.gov.laa.pfla.auth.service.services.ReportTrackingTableService;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@EnableAutoConfiguration
@Slf4j
public class ReportsController {

    private final MappingTableService mappingTableService;

    private final ReportService reportService;

    private final ReportTrackingTableService reportTrackingTableService;

    List<ReportListResponse> reportListResponseArray = new ArrayList<>();



    @Autowired
    public ReportsController(MappingTableService mappingTableService, ReportService reportService, ReportTrackingTableService reportTrackingTableService){
        this.mappingTableService = mappingTableService;
        this.reportService = reportService;
        this.reportTrackingTableService = reportTrackingTableService;
    }

    /**
     * Method to allow the user to see a list of all available reports, which are available to generate and download
     * @return A POJO list, converted to json by spring -  A list of report names, id's and some information on each report, in the form of json objects
     */
    @RequestMapping(value ="/reports", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<ReportListResponse>>getReportList() throws Exception {

        reportListResponseArray.clear(); // Prevent response data accumulating after multiple requests

        //Converting the model object arraylist to a response object arraylist
        reportListResponseArray = mappingTableService
                .createReportListResponseList();


        return new ResponseEntity<>(reportListResponseArray, HttpStatus.OK);
    }


    /**
     *
     * @return A SingleReportResponse POJO, converted to JSON by spring, and wrapped in a ResponseEntity object.
     * It is a single JSON object which contains the name, id and url of a report
     */
    @RequestMapping(value ="/report/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ReportResponse> getReport(@PathVariable(value="id") int requestedId) {


        reportTrackingTableService.updateReportTracking(requestedId, LocalDateTime.now());

        ReportResponse reportResponse =  reportService.createReportResponse(requestedId);

        return new ResponseEntity<>(reportResponse, HttpStatus.OK);


    }









}