package uk.gov.laa.pfla.auth.service.controllers;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.laa.pfla.auth.service.builders.ReportListEntryTestBuilder;
import uk.gov.laa.pfla.auth.service.builders.ReportResponseTestBuilder;
import uk.gov.laa.pfla.auth.service.graph.GraphClientHelper;
import uk.gov.laa.pfla.auth.service.responses.ReportListEntry;
import uk.gov.laa.pfla.auth.service.responses.ReportResponse;
import uk.gov.laa.pfla.auth.service.services.MappingTableService;
import uk.gov.laa.pfla.auth.service.services.ReportService;
import uk.gov.laa.pfla.auth.service.services.ReportTrackingTableService;
import uk.gov.laa.pfla.auth.service.services.UserService;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Client;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
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
    private ReportTrackingTableService reportTrackingTableService; // This is required, despite the sonarlint suggestions

    @Autowired
    private MockMvc mockMvc;


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
    @WithMockUser(roles = "ADMIN")
    void getReportListReturnsCorrectResponseEntity() throws Exception {
        //Create Mock Response objects
        ReportListEntry reportListEntryMock1 = new ReportListEntryTestBuilder().withId(1)
                .withReportName("Test Report 1")
                .withBaseUrl("www.sharepoint.com/a-different-folder-we're-using").createReportListResponse();
        ReportListEntry reportListEntryMock2 = new ReportListEntryTestBuilder().withId(2).createReportListResponse();

        //Add mock response objects to a list
        List<ReportListEntry> reportListResponseMockList = Arrays.asList(reportListEntryMock1, reportListEntryMock2);
        // Mock the Service call
        when(mappingTableServiceMock.fetchReportListEntries()).thenReturn(reportListResponseMockList);

        // Perform request and assert results
        mockMvc.perform(MockMvcRequestBuilders.get("/reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reportList", hasSize(2)))
                .andExpect(jsonPath("$.reportList[0].id").value(reportListEntryMock1.getId()))
                .andExpect(jsonPath("$.reportList[0].baseUrl").value(reportListEntryMock1.getBaseUrl()))
                .andExpect(jsonPath("$.reportList[1].id").value(reportListEntryMock2.getId()));

        verify(mappingTableServiceMock, times(1)).fetchReportListEntries();

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getReportListReturnsCorrectResponseEntity2() throws Exception {
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getReportReturnsCorrectResponseEntity() throws Exception {

        int reportId = 2;
        ReportResponse reportResponseMock = new ReportResponseTestBuilder().withId(reportId).createReportResponse();

        // Mock the service
        when(reportServiceMock.createReportResponse(reportId)).thenReturn(reportResponseMock);

        // Perform request and assert results
        mockMvc.perform(MockMvcRequestBuilders.get("/report/{id}", reportId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reportId))
                .andExpect(jsonPath("$.reportName").value(reportResponseMock.getReportName()));

        verify(reportServiceMock, times(1)).createReportResponse(reportId);

    }
}