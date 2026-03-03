package uk.gov.laa.gpfd.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.laa.gpfd.data.ReportListEntryTestDataFactory;
import uk.gov.laa.gpfd.services.ReportManagementService;
import uk.gov.laa.gpfd.utils.BaseMvcTest;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("testauth")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = uk.gov.laa.gpfd.config.TestDatabaseConfig .class)
class OAuth2LoginTest extends BaseMvcTest {

    @MockitoBean
    ReportManagementService reportManagementServiceMock;

    @MockitoBean
    ReportManagementService reportServiceMock;

    @Autowired
    MockMvc mockMvc;

    @Test
    void shouldRedirectToLoginWhenUserIsNotAuthenticated() throws Exception {
        performGetRequest("/reports")
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void shouldReturnResponseWhenUserIsAuthenticatedWithAdminRole() throws Exception {
        var mock = ReportListEntryTestDataFactory.aValidReportsGet200ResponseReportListInner();
        when(reportManagementServiceMock.fetchReportListEntries()).thenReturn(singletonList(mock));

        performAuthenticatedGet("/reports", List.of("REP000"))
                .andExpect(status().isOk());
    }


}
