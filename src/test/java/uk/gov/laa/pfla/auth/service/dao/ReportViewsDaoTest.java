package uk.gov.laa.pfla.auth.service.dao;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.gov.laa.pfla.auth.service.models.report_view_models.ReportModel;
import uk.gov.laa.pfla.auth.service.models.report_view_models.VCisToCcmsInvoiceSummaryModel;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportViewsDaoTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private ReportViewsDao reportViewsDao;

    @Test
    public void fetchReportShouldMapResultListObjectToReportObject() {

        //Arrange
        String sqlQuery = "SELECT * FROM ANY_REPORT.V_CIS_TO_CCMS_INVOICE_SUMMARY";

        List<Map<String, Object>> resultList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        java.util.Date utilDate = null;
        try {
            utilDate = dateFormat.parse("07-AUG-2023");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date = new Date(utilDate.getTime());

        Map<String, Object> column = new HashMap<>();
        column.put("DATE_AUTHORISED_CIS", date );
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

        final List<ReportModel> expectedReportViewObjectList = List.of(expectedReportViewObject);


        // Simulate jdbc/DB behavior
        when(jdbcTemplate.queryForList(sqlQuery)).thenReturn(resultList);


        // Act
        List<ReportModel> actualReportViewObjectList = reportViewsDao.fetchReport(sqlQuery, VCisToCcmsInvoiceSummaryModel.class);
        ReportModel reportModel = actualReportViewObjectList.get(0);
        assertThat(reportModel, CoreMatchers.instanceOf(VCisToCcmsInvoiceSummaryModel.class));

        VCisToCcmsInvoiceSummaryModel actualInvoiceSummaryObject = (VCisToCcmsInvoiceSummaryModel) reportModel;

        // Assert
        verify(jdbcTemplate, times(1)).queryForList(sqlQuery);
        assertEquals(1, actualReportViewObjectList.size());


        assertAll("Test expectedReportViewObject with obj2 equality",
        () -> assertEquals(expectedReportViewObject.getDateAuthorisedCis(), actualInvoiceSummaryObject.getDateAuthorisedCis()),
        () -> assertEquals(expectedReportViewObject.getTheSystem(), actualInvoiceSummaryObject.getTheSystem()),
        () -> assertEquals(expectedReportViewObject.getCisValue(), actualInvoiceSummaryObject.getCisValue()),
        () -> assertEquals(expectedReportViewObject.getCcmsValue(), actualInvoiceSummaryObject.getCcmsValue()));



    }
}



