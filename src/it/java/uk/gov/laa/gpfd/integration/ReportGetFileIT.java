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
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Object;
import uk.gov.laa.gpfd.integration.config.TestS3Config;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.springframework.jdbc.core.RowMapper;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.laa.gpfd.utils.TokenUtils.ID_REP012;
import uk.gov.laa.gpfd.config.TestSecurityConfig;
import uk.gov.laa.gpfd.config.TestDatabaseConfig;
import uk.gov.laa.gpfd.utils.SecurityUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.mockito.Mock;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import uk.gov.laa.gpfd.dao.ReportDao;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = {TestS3Config.class, TestSecurityConfig.class, TestDatabaseConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {"gpfd.s3.has-s3-access=true", "AWS_REGION=eu-west-1",
        "S3_TEMPLATE_STORE=test2", "S3_REPORT_STORE=test"
})
@TestInstance(PER_CLASS)
final class ReportGetFileIT extends BaseIT {

    @Autowired
    private S3Client s3Client;

    @Mock
    private JdbcTemplate readOnlyJdbcTemplate;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private ReportDao reportDao;

        @Test
        @SneakyThrows
        void shouldSuccessfullyPassStreamReturnedFromAWSToUserWithPermission() {

            UUID reportId = ID_REP012;
            when(readOnlyJdbcTemplate.query(
                    eq(reportDao.SELECT_REPORT_ROLES),
                    any(RowMapper.class),
                    eq(reportId.toString())
            )).thenReturn(List.of("REP000"));
            when(securityUtils.extractRoles()).thenReturn(List.of("Financial"));
            when(securityUtils.isAuthorized(List.of("REP000"), List.of("REP000")))
                    .thenReturn(true);

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

            mockMvc.perform(get("/reports/" + reportId + "/file")
                    .with(oidcLogin()
                            .idToken(token -> token.claim("LAA_APP_ROLES", List.of("REP000")))))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(APPLICATION_OCTET_STREAM))
                    .andExpect(header().longValue("Content-Length", 25L))
                    .andExpect(header().string("Content-Disposition", "attachment; filename=\"report_2025-12-15.csv\""))
                    .andExpect(content().string("csv,data,here,123,4.3,cat"));
        }

    @Test
    @SneakyThrows
    void shouldCloseStreamWhenSuccessful() {

        UUID reportId = ID_REP012;
        when(readOnlyJdbcTemplate.query(
                eq(reportDao.SELECT_REPORT_ROLES),
                any(RowMapper.class),
                eq(reportId.toString())
        )).thenReturn(List.of("REP000"));
        when(securityUtils.extractRoles()).thenReturn(List.of("Financial", "REP000"));
        when(securityUtils.isAuthorized(List.of("REP000"), List.of("Financial", "REP000")))
                .thenReturn(true);

        var responseList = List.of(
                S3Object.builder().key("reports/daily/report_2025-12-14.csv").lastModified(Instant.parse("2025-12-14T05:00:00Z")).build(),
                S3Object.builder().key("reports/daily/report_2025-12-15.csv").lastModified(Instant.parse("2025-12-15T05:00:00Z")).build()
        );
        var mockListResponse = ListObjectsV2Response.builder().contents(new ArrayList<>(responseList)).build();
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(mockListResponse);

        var mockS3Response = mock(ResponseInputStream.class);
        var mockS3ResponseInternal = mock(GetObjectResponse.class);
        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(mockS3Response);
        when(mockS3Response.response()).thenReturn(mockS3ResponseInternal);
        when(mockS3ResponseInternal.contentLength()).thenReturn(32L);


        mockMvc.perform(get("/reports/" + ID_REP012 + "/file").with(oidcLogin()
                .idToken(token -> token.claim("LAA_APP_ROLES", List.of("REP000")))))
                .andExpect(status().isOk());
        verify(mockS3Response).close();
    }

    @Test
    @SneakyThrows
    void shouldRejectUserIfNoPermissionForReport() {

        mockMvc.perform(get("/reports/" + ID_REP012 + "/file").with(oidcLogin()
                        .idToken(token -> token.claim("LAA_APP_ROLES", List.of("ABC")))))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    @Test
    @SneakyThrows
    void shouldErrorIfIdNotSupportedByEndpoint() {
        mockMvc.perform(get("/reports/" + ID_REP012 + "/file").with(oidcLogin()
                        .idToken(token -> token.claim("FOOO", List.of("REP000")))))
                .andExpect(status().isForbidden())
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

        var result =  mockMvc.perform(get("/reports/" + ID_REP012 + "/file").with(oidcLogin()
                        .idToken(token -> token.claim("LAA_APP_ROLES", List.of("REP000")))))
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

        mockMvc.perform(get("/reports/" + ID_REP012 + "/file").with(oidcLogin()
                        .idToken(token -> token.claim("LAA_APP_ROLES", List.of("REP000")))))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();
    }
}
