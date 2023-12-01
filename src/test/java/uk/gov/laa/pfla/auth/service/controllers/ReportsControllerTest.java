package uk.gov.laa.pfla.auth.service.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.laa.pfla.auth.service.builders.ReportListResponseTestBuilder;
import uk.gov.laa.pfla.auth.service.builders.ReportResponseTestBuilder;
import uk.gov.laa.pfla.auth.service.responses.ReportListResponse;
import uk.gov.laa.pfla.auth.service.responses.ReportResponse;
import uk.gov.laa.pfla.auth.service.services.MappingTableService;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import org.springframework.http.HttpStatus;
import uk.gov.laa.pfla.auth.service.services.ReportService;
import uk.gov.laa.pfla.auth.service.services.ReportTrackingTableService;

@ExtendWith(MockitoExtension.class)
class ReportsControllerTest {


    @Mock
    private MappingTableService mappingTableServiceMock;
    @Mock
    private ReportService reportServiceMock;

    @Mock
    private ReportTrackingTableService reportTrackingTableService;


    @InjectMocks // creating a ReportsController object and then inject the mocked MappingTableService + reportService instances into it.
    private ReportsController reportsController;

    @Test
    void getReportListReturnsCorrectResponseEntity() throws Exception {
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
    void getReportReturnsCorrectResponseEntity() {

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
        assertEquals(reportResponseMock.getReportUrl(), response.getReportUrl());
        assertEquals(reportResponseMock.getCreationTime(), response.getCreationTime());


    }

}
