package uk.gov.laa.gpfd.integration;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junitpioneer.jupiter.RetryingTest;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import uk.gov.laa.gpfd.dao.ReportsTrackingDao;
import uk.gov.laa.gpfd.model.ReportsTracking;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("testauth")
@Import(OAuth2TestConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application-test.yml")
class GetCsvByIdIT extends BaseIT {

    @MockitoSpyBean
    private ReportsTrackingDao reportsTrackingDao;

    @Test
    @RetryingTest(maxAttempts = 2)
    // We retry this test once because it fails about 1% of the time due to a Spring Framework issue reported here:
        // https://github.com/spring-projects/spring-framework/issues/31543
    @WithMockUser(username = "Mock User")
    void shouldReturnCsvWithMatchingId() throws Exception {

        performGetRequest("/csv/" + BaseIT.REPORT_UUID_1)
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Disposition", "attachment; filename=CIS to CCMS payment value Defined.csv"));

        ArgumentCaptor<ReportsTracking> captor = ArgumentCaptor.forClass(ReportsTracking.class);
        Mockito.verify(reportsTrackingDao).saveReportsTracking(captor.capture());
        ReportsTracking capturedArgument = captor.getValue();
        Assertions.assertEquals(BaseIT.REPORT_UUID_1, capturedArgument.getReportId().toString());

    }

    @Test
    @WithMockUser(username = "Mock User")
    void shouldReturn404WhenNoReportsFound() throws Exception {
        performGetRequest("/csv/0d4da9ec-b0b3-4371-af10-321")
            .andExpect(status().isNotFound());
        Mockito.verify(reportsTrackingDao, Mockito.never()).saveReportsTracking(Mockito.any());
    }
}