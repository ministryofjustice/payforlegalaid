package uk.gov.laa.pfla.auth.service.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.gov.laa.pfla.auth.service.models.report_view_models.ReportModel;
import uk.gov.laa.pfla.auth.service.models.report_view_models.VCisToCcmsInvoiceSummaryModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class ReportViewsDaoTest{

    @Mock
    JdbcTemplate jdbcTemplate;

    @InjectMocks
    @Spy
    ReportViewsDao reportViewsDaoSpy;

    @Test
    public void fetchReportShouldMapResultListObjectToReportObject(){

        //Arrange
        String sqlQuery = "SELECT * FROM ANY_REPORT.V_CIS_TO_CCMS_INVOICE_SUMMARY";

        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, Object> column = new HashMap<>();
        column.put("DATE_AUTHORISED_CIS", "2023-08-07 00:00:00.0");
        column.put("THE_SYSTEM", "CWA Crime Lower Contract");
        column.put("CIS_VALUE", "10466.08");
        column.put("CCMS_VALUE", "10466.08");

        resultList.add(column);


        VCisToCcmsInvoiceSummaryModel expectedReportViewObject = VCisToCcmsInvoiceSummaryModel.builder()
                .dateAuthorisedCis("07-AUG-2023")
                .theSystem("CWA Crime Lower Contract")
                .cisValue(10466.08)
                .ccmsValue(10466.08)
                .build();

        final List<ReportModel> expectedReportViewObjectList = new ArrayList<>();
        expectedReportViewObjectList.add(expectedReportViewObject);


        // Simulate report model and service behavior

//        when(reportViewsDaoSpy.callDataBase(sqlQuery)).thenReturn(resultList);
//        reportViewsDaoSpy = spy(new ReportViewsDao());
        doReturn(resultList).when(reportViewsDaoSpy).callDataBase(sqlQuery);



        // Act
        List<ReportModel> actualReportViewObjectList = reportViewsDaoSpy.fetchReport(sqlQuery, VCisToCcmsInvoiceSummaryModel.class);
        VCisToCcmsInvoiceSummaryModel actualInvoiceSummaryObject = (VCisToCcmsInvoiceSummaryModel)actualReportViewObjectList.get(0);

        // Assert
        verify(reportViewsDaoSpy, times(1)).callDataBase(sqlQuery);
        assertEquals(1, actualReportViewObjectList.size());
//        assertEquals(expectedReportViewObject.getDateAuthorisedCis(), actualInvoiceSummaryObject.getDateAuthorisedCis());
        assertEquals(expectedReportViewObject.getTheSystem(), actualInvoiceSummaryObject.getTheSystem());
//        assertAll("Test expectedReportViewObject with obj2 equality",
//                () -> assertEquals(expectedReportViewObject.getDateAuthorisedCis(), actualInvoiceSummaryObject.getDateAuthorisedCis()),
//                () -> assertEquals(expectedReportViewObject.getTheSystem(), actualInvoiceSummaryObject.toString()),
//                () -> assertEquals(expectedReportViewObject.getCisValue(), actualInvoiceSummaryObject.toString()),
//                () -> assertEquals(expectedReportViewObject.getCcmsValue(), actualInvoiceSummaryObject.toString()));
//
        assertThat(actualReportViewObjectList.get(0), instanceOf(VCisToCcmsInvoiceSummaryModel.class));



    }
}



