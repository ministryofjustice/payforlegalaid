package uk.gov.laa.pfla.auth.service.dao;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.laa.pfla.auth.service.dao.ReportViewsDao;
import uk.gov.laa.pfla.auth.service.models.report_view_models.VBankMonth;
import uk.gov.laa.pfla.auth.service.models.report_view_models.VCisToCcmsInvoiceSummaryModel;
import uk.gov.laa.pfla.auth.service.services.ReportService;
import org.springframework.jdbc.core.JdbcTemplate;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ReportViewsDAOTest {
    @InjectMocks
    ReportViewsDao reportViewsDAO;
    @Mock
    JdbcTemplate jdbcTemplate;
    @Test
    <T>void fetchReportShouldReturnCorrectReport() {
        // Arrange
        ReportService reportService = mock(ReportService.class);
//        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);

        String sqlQuery = "SELECT * FROM ANY_REPORT.V_BANK_MONTH";
        when(jdbcTemplate.queryForList(sqlQuery)).thenReturn()

        // Simulate report model and service behavior
        VBankMonth expectedReport = new VBankMonth();
        List<VBankMonth> expectedReportList = new ArrayList<>();
        expectedReportList.add(expectedReport);
        when(reportService.fetchReportViewObjectList(1, sqlQuery )).thenReturn(Collections.singletonList(expectedReportList));

        // Act
        List<VBankMonth> result = reportViewsDAO.fetchReport(sqlQuery, VBankMonth.class);

        // Assert
        assertEquals(1, result.size());
        assertEquals(expectedReport, result.get(0));
    }
}