package uk.gov.laa.gpfd.integration;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;
import uk.gov.laa.gpfd.exception.sds.SdsFileNotFoundException;
import uk.gov.laa.gpfd.integration.config.TestS3Config;
import uk.gov.laa.gpfd.services.sds.SdsService;
import uk.gov.laa.gpfd.services.sds.client.model.SdsFileDownloadResponse;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.laa.gpfd.utils.ReportIds.ID_REP012;

@Import(TestS3Config.class)
@TestPropertySource(properties = {
        "gpfd.s3.has-s3-access=true",
        "AWS_REGION=eu-west-1",
        "S3_TEMPLATE_STORE=test2",
        "S3_REPORT_STORE=test",
        "gpfd.sds-enabled.enabled=true",
        "app.sds-api.url=http://localhost",
        "app.sds-api.client-registration-id=sds-api-client",
        "app.sds-api.principal-name=laa-sds-test",
        "spring.security.oauth2.client.registration.sds-api-client.client-id=test",
        "spring.security.oauth2.client.registration.sds-api-client.client-secret=test",
        "spring.security.oauth2.client.registration.sds-api-client.authorization-grant-type=client_credentials",
        "spring.security.oauth2.client.registration.sds-api-client.scope=api://test/.default",
        "spring.security.oauth2.client.registration.sds-api-client.provider=sds-api-client",
        "spring.security.oauth2.client.provider.sds-api-client.token-uri=http://localhost/oauth2/token"
})
final class ReportGetFileSdsEnabledIT extends uk.gov.laa.gpfd.integration.BaseIT {

    @Autowired
    private S3Client s3Client;

    @MockitoBean
    private SdsService sdsService;

    @Test
    @SneakyThrows
    void shouldReturnStableFileEndpointInReportMetadataWhenSdsFeatureFlagEnabled() {
        performGetRequestWithRoles("/reports/" + ID_REP012, List.of("REP000", "Financial", "Reconciliation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reportDownloadUrl")
                        .value("http://localhost/reports/" + ID_REP012 + "/file"));
    }

    @Test
    @SneakyThrows
    void shouldRedirectToSdsDownloadWhenReportExistsInSds() {
        when(sdsService.getFile("REP012 - Original Submissions Value Report.csv"))
                .thenReturn(new SdsFileDownloadResponse().fileURL("https://sds.example.com/presigned/report.csv"));

        performGetRequestWithRoles("/reports/" + ID_REP012 + "/file", List.of("Reconciliation"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://sds.example.com/presigned/report.csv"));

        verify(sdsService).getFile("REP012 - Original Submissions Value Report.csv");
    }

    @Test
    @SneakyThrows
    void shouldFallbackToS3WhenSdsFileDoesNotExist() {
        when(sdsService.getFile("REP012 - Original Submissions Value Report.csv"))
                .thenThrow(new SdsFileNotFoundException("File not found"));

        var responseMetadata = GetObjectResponse.builder().contentLength(25L).build();
        var inputStream = new ByteArrayInputStream("csv,data,here,123,4.3,cat".getBytes());
        var responseList = List.of(
                S3Object.builder().key("reports/daily/report_2025-12-14.csv").lastModified(Instant.parse("2025-12-14T05:00:00Z")).build(),
                S3Object.builder().key("reports/daily/report_2025-12-15.csv").lastModified(Instant.parse("2025-12-15T05:00:00Z")).build()
        );
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class)))
                .thenReturn(ListObjectsV2Response.builder().contents(new ArrayList<>(responseList)).build());
        when(s3Client.getObject(any(GetObjectRequest.class)))
                .thenReturn(new ResponseInputStream<>(responseMetadata, inputStream));

        performStreamingGetRequestWithRoles("/reports/" + ID_REP012 + "/file", List.of("Reconciliation"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_OCTET_STREAM))
                .andExpect(header().longValue("Content-Length", 25L))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"report_2025-12-15.csv\""))
                .andExpect(content().string("csv,data,here,123,4.3,cat"));
    }
}
