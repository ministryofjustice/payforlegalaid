package uk.gov.laa.pfla.auth.service.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.laa.pfla.auth.service.beans.UserDetails;
import uk.gov.laa.pfla.auth.service.exceptions.UserServiceException;
import uk.gov.laa.pfla.auth.service.responses.ReportListResponse;
import uk.gov.laa.pfla.auth.service.responses.ReportResponse;
import uk.gov.laa.pfla.auth.service.services.MappingTableService;
import uk.gov.laa.pfla.auth.service.services.ReportService;
import uk.gov.laa.pfla.auth.service.services.ReportTrackingTableService;
import uk.gov.laa.pfla.auth.service.services.UserService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@EnableAutoConfiguration
@Slf4j
public class ReportsController {

    private final MappingTableService mappingTableService;

    private final ReportService reportService;

    private final ReportTrackingTableService reportTrackingTableService;

    private final UserService userService;


    @Autowired
    public ReportsController(MappingTableService mappingTableService, ReportService reportService, ReportTrackingTableService reportTrackingTableService, UserService userService){
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

        //Converting the model object arraylist to a response object arraylist
        List<ReportListResponse> reportListResponseArray = mappingTableService
                .createReportListResponseList();


        return new ResponseEntity<>(reportListResponseArray, HttpStatus.OK);
    }


    /**
     *
     * @return A SingleReportResponse POJO, converted to JSON by spring, and wrapped in a ResponseEntity object.
     * It is a single JSON object which contains the name, id and url of a report
     */
    @RequestMapping(value ="/report/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ReportResponse> getReport(
            @RegisteredOAuth2AuthorizedClient("graph") OAuth2AuthorizedClient graphClient,
            @PathVariable(value="id") int requestedId) {


        reportTrackingTableService.updateReportTracking(requestedId, LocalDateTime.now());

        ReportResponse reportResponse = new ReportResponse();
        try {
            reportResponse = reportService.createReportResponse(requestedId, graphClient);
        } catch (IndexOutOfBoundsException e) {
            log.error("index out of bounds  Error: " + e);
            reportResponse.setReportName("Report ID not found");
            return new ResponseEntity<>(reportResponse, HttpStatus.BAD_REQUEST);
        } catch ( NumberFormatException e) { //todo - catch a different type of exception
            log.error("Number format exception: " + e);
            reportResponse.setReportName("Invalid input, report id must be a number with no decimal places");
            return new ResponseEntity<>(reportResponse, HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            log.error("Technical error creating CSV file: " + e);
            reportResponse.setReportName("Technical error creating report");
        }

        return new ResponseEntity<>(reportResponse, HttpStatus.OK);

    }
    @RequestMapping(value ="/csv/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> getCSV(@PathVariable(value="id") int requestedId) {
        //Get CSV data stream
        ByteArrayOutputStream csvDataOutputStream;
        try {
             csvDataOutputStream = reportService.createCsvStream(requestedId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Create response
        StreamingResponseBody responseBody = outputStream -> {
            try {
                csvDataOutputStream.writeTo(outputStream);
                outputStream.flush();
            } catch (IOException e) {
                // Handle IO exception
            }
        };

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=data.csv")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(responseBody);
    }

    //This method is just for development, for testing that graph is working properly. It displays the details of the current SSO user
    @GetMapping("/graph")
    @ResponseBody
    public String graph(
            @RegisteredOAuth2AuthorizedClient("graph") OAuth2AuthorizedClient graphClient
    ) throws UserServiceException {
        UserDetails user = userService.getUserDetails(graphClient);

        log.info("Here's the graphClient.getClientRegistration(): " + graphClient.getClientRegistration());
        log.info("Here's the graphClient.getRefreshToken(): " + graphClient.getRefreshToken());
        log.info("Here's the graphClient.getPrincipalName(): " + graphClient.getPrincipalName());
        log.info("Here's the graphClient.getAccessToken(): " + graphClient.getAccessToken());


        return user.toString();

    }






}