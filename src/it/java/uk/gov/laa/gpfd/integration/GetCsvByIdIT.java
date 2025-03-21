package uk.gov.laa.gpfd.integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import uk.gov.laa.gpfd.dao.ReportsTrackingDao;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("testauth")
@Import(OAuth2TestConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application-test.yml")
class GetCsvByIdIT extends BaseIT {

    @Autowired
    private ReportsTrackingDao reportsTrackingDao;

    @Test
    void shouldReturnCsvWithMatchingId() throws Exception {

        MockHttpServletResponse response = getResponseForAuthenticatedRequest("/csv/0d4da9ec-b0b3-4371-af10-f375330d85d3");

        var reportTrackingData = reportsTrackingDao.list().stream()
            .filter(record -> "0d4da9ec-b0b3-4371-af10-f375330d85d3".equals(record.get("REPORT_ID").toString()))
            .toList();
        Assertions.assertEquals(1, reportTrackingData.size());
        Assertions.assertEquals("0d4da9ec-b0b3-4371-af10-f375330d85d3", reportTrackingData.get(0).get("REPORT_ID").toString());
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("attachment; filename=CIS to CCMS payment value Defined.csv", response.getHeader("Content-Disposition"));
    }

    @Test
    void shouldReturn404WhenNoReportsFound() throws Exception {
        MockHttpServletResponse response = getResponseForAuthenticatedRequest("/csv/0d4da9ec-b0b3-4371-af10-321");
        var reportTrackingData = reportsTrackingDao.list().stream()
            .filter(record -> "0d4da9ec-b0b3-4371-af10-321".equals(record.get("REPORT_ID").toString()))
            .toList();

        Assertions.assertEquals(0, reportTrackingData.size());
        Assertions.assertEquals(404, response.getStatus());
    }
}