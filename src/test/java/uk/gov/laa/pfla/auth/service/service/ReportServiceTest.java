package uk.gov.laa.pfla.auth.service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.pfla.auth.service.dao.ReportViewsDao;
import uk.gov.laa.pfla.auth.service.models.report_view_models.ReportModel;
import uk.gov.laa.pfla.auth.service.models.report_view_models.VBankMonth;
import uk.gov.laa.pfla.auth.service.services.MappingTableService;
import uk.gov.laa.pfla.auth.service.services.ReportService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @Mock
    ReportViewsDao reportViewsDAO;
    @Mock
    MappingTableService mappingTableService;

    @InjectMocks
    ReportService reportService;


    @Test
    public void fetchReportViewObjectList_ReturnsCorrectReportModelType(){

        // Arrange
        String sqlQuery = "SELECT * FROM ANY_REPORT.V_BANK_MONTH";

        VBankMonth expectedReportViewObject = VBankMonth.builder()
                .source("CCMS")
                .invSource("CCMS")
                .subSource("Applied Receipts")
                .paymentDate("05-jul-23")
                .paymentMonth("31-jul-23")
                .settlementType("Applied Receipts")
                .scheme("Civil")
                .subScheme("Civil Representation")
                .detailDesc("Costs Interest")
                .catCode("N/A")
                .apArMovement("N")
                .total(-15)
                .build();

        final List<ReportModel> expectedReportViewObjectList = new ArrayList<>();
        expectedReportViewObjectList.add(expectedReportViewObject);

        // Simulate report model and service behavior
        when(reportViewsDAO.fetchReport(sqlQuery,VBankMonth.class)).thenReturn(expectedReportViewObjectList);


        // Act
        List<ReportModel> actualReportViewObjectList = reportService.fetchReportViewObjectList(VBankMonth.class, sqlQuery);

        // Assert
        verify(reportViewsDAO, times(1)).fetchReport(sqlQuery,VBankMonth.class);
        assertEquals(1, actualReportViewObjectList.size());
        assertEquals(expectedReportViewObject, actualReportViewObjectList.get(0));
        assertThat(actualReportViewObjectList.get(0), instanceOf(VBankMonth.class));

    }
}