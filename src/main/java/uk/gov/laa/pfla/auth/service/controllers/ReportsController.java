package uk.gov.laa.pfla.auth.service.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.*;
import uk.gov.laa.pfla.auth.service.beans.UserDetails;
import uk.gov.laa.pfla.auth.service.exceptions.UserServiceException;
import uk.gov.laa.pfla.auth.service.responses.ReportListResponse;
import uk.gov.laa.pfla.auth.service.responses.ReportResponse;
import uk.gov.laa.pfla.auth.service.services.MappingTableService;
import uk.gov.laa.pfla.auth.service.services.ReportService;
import uk.gov.laa.pfla.auth.service.services.ReportTrackingTableService;
import uk.gov.laa.pfla.auth.service.services.UserService;

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

    private final UserService userService;


    public ReportsController(MappingTableService mappingTableService, ReportService reportService, ReportTrackingTableService reportTrackingTableService, final UserService userService){
        this.mappingTableService = mappingTableService;
        this.reportService = reportService;
        this.reportTrackingTableService = reportTrackingTableService;
        this.userService = userService;

    }

    /**
     * Method to allow the user to see a list of all available reports, which are available to generate and download
     * @return A POJO list, converted to json by spring -  A list of report names, id's and some information on each report, in the form of json objects
     */
    @RequestMapping(value ="/reports", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<ReportListResponse>>getReportList() {

        log.info("Get reportLIST log 1 ");

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
        log.info("Get report log 1 ");


        reportTrackingTableService.updateReportTracking(requestedId, LocalDateTime.now());

        ReportResponse reportResponse =  reportService.createReportResponse(requestedId);
        log.info("Get report log 2 ");

        return new ResponseEntity<>(reportResponse, HttpStatus.OK);


    }

        @RequestMapping(value ="/sso", produces = MediaType.APPLICATION_JSON_VALUE)
        @ResponseBody
        public String sso(@RegisteredOAuth2AuthorizedClient("graph") OAuth2AuthorizedClient graphClient) throws UserServiceException {


        UserDetails user = userService.getUserDetails(graphClient);
            log.info("Logging current graph user: " + user.getUserPrincipalName());


        return "Principal Name:"  + user.getUserPrincipalName();
        }








}