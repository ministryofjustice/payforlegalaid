package uk.gov.laa.gpfd.integration;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.ResultActions;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import uk.gov.laa.gpfd.integration.config.TestS3Config;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.laa.gpfd.utils.TokenUtils.ID_REP012;

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = {TestS3Config.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {"gpfd.s3.has-s3-access=true", "AWS_REGION=eu-west-1",
        "S3_TEMPLATE_STORE=test2", "S3_REPORT_STORE=test",
        "gpfd.s3.permissions.rep000=fjfh34-fdsff33-fdfj444", "gpfd.s3.permissions.submission-reconciliation=jfdsf234-32434fd-34234"
})
@TestInstance(PER_CLASS)
final class ReportGetFileIT extends BaseIT {

    @Autowired
    private S3Client s3Client;

    @Test
    @SneakyThrows
    void shouldSuccessfullyPassStreamReturnedFromAWSToUserWithPermission() {

        var responseMetadata = GetObjectResponse.builder().contentLength(25L).build();
        var inputStream = new ByteArrayInputStream("csv,data,here,123,4.3,cat".getBytes());
        var mockS3Response = new ResponseInputStream<>(responseMetadata, inputStream);

        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(mockS3Response);

        performGetRequestWithUserHavingGroup("/reports/" + ID_REP012 + "/file", "jfdsf234-32434fd-34234")
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_OCTET_STREAM))
                .andExpect(header().longValue("Content-Length", 25L))
                .andExpect(content().string("csv,data,here,123,4.3,cat"));
    }

    @Test
    @SneakyThrows
    void shouldRejectUserIfNoPermissionForReport() {

        performGetRequestWithUserHavingGroup("/reports/" + ID_REP012 + "/file", "fjfh34-fdsff33-fdfj444")
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    @Test
    @SneakyThrows
    void shouldErrorIfIdNotSupportedByEndpoint() {
        performGetRequestWithUserHavingGroup("/reports/dd55e276-97b0-4dd8-a919-26d4aa373277/file", "")
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    @Test
    @SneakyThrows
    void shouldErrorIfIdNotValid() {
        performGetRequest("/reports/hi/file")
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

        var result = performGetRequestWithUserHavingGroup("/reports/" + ID_REP012 + "/file", "jfdsf234-32434fd-34234")
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();

        var responseJson = result.getResponse().getContentAsString();
        // Just ensuring we sanitise user facing output
        assertFalse(responseJson.contains("File don't exist and some maybe sensitive stuff about addresses here"));
    }

    @SneakyThrows
    private ResultActions performGetRequestWithUserHavingGroup(String url, String group) {
        var auth = mock(Authentication.class);
        var principal = mock(DefaultOidcUser.class);

        when(auth.getPrincipal()).thenReturn(principal);
        when(principal.getClaimAsStringList("groups")).thenReturn(List.of(group));
        var mockSecurityContext = mock(SecurityContext.class);
        when(mockSecurityContext.getAuthentication()).thenReturn(auth);

        return performGetRequest(url, mockSecurityContext);
    }
}
