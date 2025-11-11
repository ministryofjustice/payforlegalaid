package uk.gov.laa.gpfd.controller.ui;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import uk.gov.laa.gpfd.controller.ReportsController;
import uk.gov.laa.gpfd.model.GetReportById200Response;
import uk.gov.laa.gpfd.model.ReportsGet200Response;
import uk.gov.laa.gpfd.model.ReportsGet200ResponseReportListInner;

import java.net.URI;
import java.util.List;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.ResponseEntity.ok;

@ExtendWith(MockitoExtension.class)
class ReportsViewControllerTest {

    @Mock
    ReportsController reportsApiController;

    @Mock
    Model model;

    @InjectMocks
    ReportsViewController reportsViewController;

    @Test
    void index_shouldRedirectToRoot() {
        var result = reportsViewController.index();
        assertEquals("redirect:/", result);
    }

    @Test
    @SneakyThrows
    void getAllReports_shouldReturnCorrectViewName() {
        var reportId1 = randomUUID();
        var reportId2 = randomUUID();

        var reportsResponse = new ReportsGet200Response() {{
            setReportList(List.of(
                    new ReportsGet200ResponseReportListInner() {{
                        setId(reportId1);
                        setReportName("Report 1");
                        setDescription("Description 1");
                    }},
                    new ReportsGet200ResponseReportListInner() {{
                        setId(reportId2);
                        setReportName("Report 2");
                        setDescription("Description 2");
                    }})
            );
        }};

        when(reportsApiController.reportsGet()).thenReturn(ok(reportsResponse));
        when(reportsApiController.getReportById(reportId1)).thenReturn(ok(new GetReportById200Response() {{
            setReportDownloadUrl(new URI("http://example.com/report1"));
        }}));
        when(reportsApiController.getReportById(reportId2)).thenReturn(ok(new GetReportById200Response() {{
            setReportDownloadUrl(new URI("http://example.com/report2"));
        }}));

        var viewName = reportsViewController.getAllReports(model);

        assertEquals("reports/list", viewName);
        verify(model).addAttribute(eq("reportListResponse"), anyList());
        verify(reportsApiController).reportsGet();
        verify(reportsApiController).getReportById(reportId1);
        verify(reportsApiController).getReportById(reportId2);
    }

    @Test
    void getAllReports_shouldHandleEmptyReportList() {
        when(reportsApiController.reportsGet()).thenReturn(ok(new ReportsGet200Response() {{
            setReportList(List.of());
        }}));

        var viewName = reportsViewController.getAllReports(model);

        assertEquals("reports/list", viewName);
        verify(model).addAttribute("reportListResponse", List.of());
        verify(reportsApiController).reportsGet();
        verify(reportsApiController, never()).getReportById(any());
    }

}