package uk.gov.laa.pfla.auth.service.controllers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.laa.pfla.auth.service.builders.ReportListResponseTestBuilder;
import uk.gov.laa.pfla.auth.service.builders.ReportResponseTestBuilder;
import uk.gov.laa.pfla.auth.service.graph.GraphClientHelper;
import uk.gov.laa.pfla.auth.service.responses.ReportListResponse;
import uk.gov.laa.pfla.auth.service.responses.ReportResponse;
import uk.gov.laa.pfla.auth.service.services.MappingTableService;
import uk.gov.laa.pfla.auth.service.services.ReportService;
import uk.gov.laa.pfla.auth.service.services.ReportTrackingTableService;
import uk.gov.laa.pfla.auth.service.services.UserService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Client;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ReportsControllerTest {

    @MockBean
    UserService userService;
    @MockBean
    GraphClientHelper mockGraphClientHelper;
    @MockBean
    private MappingTableService mappingTableServiceMock;
    @MockBean
    private ReportService reportServiceMock;
    @MockBean
    private ReportTrackingTableService reportTrackingTableService;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
    }


    @AfterEach
    void tearDown() {
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void downloadCsvReturnsCorrectResponse() throws Exception {

        // Mock CSV data
        ByteArrayOutputStream csvDataOutputStream = new ByteArrayOutputStream();
        csvDataOutputStream.write("1,John,Doe\n".getBytes());
        csvDataOutputStream.write("2,Jane,Smith\n".getBytes());

        // Mock response body
        StreamingResponseBody responseBody = outputStream -> {
            csvDataOutputStream.writeTo(outputStream);
            outputStream.flush();
        };

        // Mock ResponseEntity
        ResponseEntity<StreamingResponseBody> mockResponseEntity = ResponseEntity.ok().header("Content-Disposition", "attachment; filename=data.csv").contentType(MediaType.APPLICATION_OCTET_STREAM).body(responseBody);

        // Mock method call
        when(reportServiceMock.createCSVResponse(1)).thenReturn(mockResponseEntity);

        // Perform the GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/csv/1")
                        .with(oidcLogin())
                        .with(oauth2Client("graph")))
                .andExpect(status().isOk()).andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=data.csv"));
        verify(reportServiceMock).createCSVResponse(1);
    }

    @Test
    @WithAnonymousUser
    void downloadCsvReturnsCorrectResponseWhenNotAuthenticated() throws Exception {
        // Perform the GET request without any oidc login credentials - i.e. user does not have an MOJ SSO account
        mockMvc.perform(MockMvcRequestBuilders.get("/csv/1"))
                .andExpect(status().is3xxRedirection());
        verify(reportServiceMock, never()).createCSVResponse(anyInt());
    }








    @Test
    void getReportListReturnsCorrectResponseEntity()  {
        //Create Mock Response objects
        ReportListResponse reportListResponseMock1 = new ReportListResponseTestBuilder().withId(1)
                .withReportName("Test Report 1")
                .withBaseUrl("www.sharepoint.com/a-different-folder-we're-using").createReportListResponse();
        ReportListResponse reportListResponseMock2 = new ReportListResponseTestBuilder().withId(2).createReportListResponse();

        //Add mock response objects to a list
        List<ReportListResponse> reportListResponseMockList = Arrays.asList(reportListResponseMock1, reportListResponseMock2);
        // Mock the Service call
        when(mappingTableServiceMock.createReportListResponseList()).thenReturn(reportListResponseMockList);

        //Get response object List from a call to the controller
        ResponseEntity<List<ReportListResponse>> responseEntity = reportsController.getReportList();
        List<ReportListResponse> responseList = responseEntity.getBody();


        verify(mappingTableServiceMock, times(1)).createReportListResponseList();
        assertNotNull(responseEntity);
        assertNotNull(responseEntity.getBody());
        assertNotNull(responseList);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(2, responseList.size());

        //check the first and last elements are the same in each response object
        for (int i = 0; i < reportListResponseMockList.size(); i++) {
            ReportListResponse reportListResponseMock = reportListResponseMockList.get(i);
            ReportListResponse reportListResponse = responseList.get(i);

            assertEquals(reportListResponseMock.getId(), reportListResponse.getId());
            assertEquals(reportListResponseMock.getBaseUrl(), reportListResponse.getBaseUrl());


        }


    }

    @Test
    void getReportReturnsCorrectResponseEntity() throws IOException {

        int reportId = 2;

        //Create Mock Response object
        ReportResponse reportResponseMock = new ReportResponseTestBuilder().withId(reportId).createReportResponse();
        //Mock report service
        when(reportServiceMock.createReportResponse(reportId)).thenReturn(reportResponseMock);

        ResponseEntity<ReportResponse> responseEntity = reportsController.getReport(reportId);
        ReportResponse response = responseEntity.getBody();


        verify(reportServiceMock, times(1)).createReportResponse(reportId);
        assertNotNull(responseEntity);
        assertNotNull(response);
        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(reportResponseMock.getId(), response.getId());
        assertEquals(reportResponseMock.getReportName(), response.getReportName());
//        assertEquals(reportResponseMock.getReportUrl(), response.getReportUrl());
//        assertEquals(reportResponseMock.getCreationTime(), response.getCreationTime());


    }
}