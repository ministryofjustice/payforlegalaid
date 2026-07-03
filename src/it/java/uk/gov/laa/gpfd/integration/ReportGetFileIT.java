package uk.gov.laa.gpfd.integration;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Object;
import uk.gov.laa.gpfd.integration.config.TestS3Config;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.laa.gpfd.utils.ReportIds.ID_REP012;

@Import(TestS3Config.class)
@TestPropertySource(properties = {"gpfd.s3.has-s3-access=true", "AWS_REGION=eu-west-1",
        "S3_TEMPLATE_STORE=test2", "S3_REPORT_STORE=test"
})
final class ReportGetFileIT extends BaseIT {

    @Autowired
    private S3Client s3Client;

    @Test
    @SneakyThrows
    void shouldSuccessfullyPassStreamReturnedFromAWSToUserWithPermission() {

        var responseMetadata = GetObjectResponse.builder().contentLength(25L).build();
        var inputStream = new ByteArrayInputStream("csv,data,here,123,4.3,cat".getBytes());

        var responseList = List.of(
                S3Object.builder().key("reports/daily/report_2025-12-14.csv").lastModified(Instant.parse("2025-12-14T05:00:00Z")).build(),
                S3Object.builder().key("reports/daily/report_2025-12-15.csv").lastModified(Instant.parse("2025-12-15T05:00:00Z")).build()
        );
        var mockListResponse = ListObjectsV2Response.builder().contents(new ArrayList<>(responseList)).build();
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(mockListResponse);

        var mockS3Response = new ResponseInputStream<>(responseMetadata, inputStream);
        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(mockS3Response);

        performStreamingGetRequestWithRoles("/reports/" + ID_REP012 + "/file", List.of("Reconciliation"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_OCTET_STREAM))
                .andExpect(header().longValue("Content-Length", 25L))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"report_2025-12-15.csv\""))
                .andExpect(content().string("csv,data,here,123,4.3,cat"));
    }

    @Test
    @SneakyThrows
    void shouldRejectUserIfNoPermissionForReport() {
        performGetRequestWithRoles("/reports/" + ID_REP012 + "/file", List.of("ABC"))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    @Test
    @SneakyThrows
    void shouldErrorIfIdNotSupportedByEndpoint() {
        performGetRequestWithRoles("/reports/0d4da9ec-b0b3-4371-af10-f375330d85d3/file", List.of("Financial"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    @Test
    @SneakyThrows
    void shouldErrorIfIdNotValid() {
        performGetRequestWithRoles("/reports/hi/file", List.of("Financial"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    @Test
    @SneakyThrows
    void shouldHandleS3Errors() {
        var exception = NoSuchKeyException.builder().message("File don't exist and some maybe sensitive stuff about addresses here")
                .awsErrorDetails(AwsErrorDetails.builder().errorCode("312").errorMessage("uh oh").build())
                .build();

        when(s3Client.getObject(any(GetObjectRequest.class))).thenThrow(exception);

        var result = performGetRequestWithRoles("/reports/" + ID_REP012 + "/file", List.of("Reconciliation"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();

        var responseJson = result.getResponse().getContentAsString();
        // Just ensuring we sanitise user facing output
        assertFalse(responseJson.contains("File don't exist and some maybe sensitive stuff about addresses here"));
    }

    @Test
    @SneakyThrows
    void shouldHandleErrorWhenNothingFoundInS3() {
        var mockListResponse = ListObjectsV2Response.builder().contents(new ArrayList<>()).build();
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(mockListResponse);

        performGetRequestWithRoles("/reports/" + ID_REP012 + "/file", List.of("Reconciliation"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();
    }
}
