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
import uk.gov.laa.pfla.auth.service.exceptions.CsvStreamException;
import uk.gov.laa.pfla.auth.service.exceptions.DatabaseReadException;
import uk.gov.laa.pfla.auth.service.exceptions.ReportIdNotFoundException;
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
@Slf4j
public class ReportsController {

    private final MappingTableService mappingTableService;

    private final ReportService reportService;

    private final ReportTrackingTableService reportTrackingTableService;

    private final UserService userService;


    @Autowired
    public ReportsController(MappingTableService mappingTableService, ReportService reportService,
                             ReportTrackingTableService reportTrackingTableService, UserService userService){
        this.mappingTableService = mappingTableService;
        this.reportService = reportService;
        this.reportTrackingTableService = reportTrackingTableService;
        this.userService = userService;

    }

    /**
     * Allows the user to see a list of all available reports, which are available to generate and download
     *
     * @return A POJO list, converted to json by spring -  A list of report names, id's and some information on each report, in the form of json objects
     */
    @RequestMapping(value ="/reports", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<ReportListResponse>>getReportList() throws DatabaseReadException {

        //Converting the model object arraylist to a response object arraylist
        List<ReportListResponse> reportListResponseArray = mappingTableService
                .createReportListResponseList();


        return new ResponseEntity<>(reportListResponseArray, HttpStatus.OK);
    }


    /**
     * This method will be useful once a content management/caching system is in place, it will return the name of the requested report, and other data such as the time at which it was requested
     * @param requestedId - id of the requested report
     * @return A SingleReportResponse POJO, converted to JSON by spring, and wrapped in a ResponseEntity object.
     * It is a single JSON object which contains the name, id and download url of a report
     **/
    @RequestMapping(value ="/report/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ReportResponse> getReport(@PathVariable(value="id") int requestedId) throws IndexOutOfBoundsException {



        ReportResponse reportResponse = reportService.createReportResponse(requestedId);


        return new ResponseEntity<>(reportResponse, HttpStatus.OK);

    }

    /**
     * Sends a report to the user in the form of a CSV data stream. If the user requests via a web browser this response then triggers the browser to download the file.
     *
     * @param requestedId - id of the requested report
     * @return CSV data stream or reports data
     */
    @RequestMapping(value ="/csv/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> getCSV(@PathVariable(value="id") int requestedId,
                                                        @RegisteredOAuth2AuthorizedClient("graph") OAuth2AuthorizedClient graphClient) throws ReportIdNotFoundException,
            CsvStreamException, DatabaseReadException, IndexOutOfBoundsException, UserServiceException {

        reportTrackingTableService.updateReportTrackingTable(requestedId, LocalDateTime.now(),graphClient);

        return reportService.createCSVResponse(requestedId);
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