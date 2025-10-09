package uk.gov.laa.gpfd.services.s3;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import uk.gov.laa.gpfd.config.S3Config;
import uk.gov.laa.gpfd.exception.ReportAccessException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static uk.gov.laa.gpfd.utils.TokenUtils.ID_REP000;
import static uk.gov.laa.gpfd.utils.TokenUtils.ID_REP012;
import static uk.gov.laa.gpfd.utils.TokenUtils.ID_REP013;

@ExtendWith(MockitoExtension.class)
class ReportAccessCheckerServiceTest {

    private final String rep000Permission = "34fdsfh324-fdsfsdaf324-ds";
    private final String submissionRecPermission = "hdscv2343rvf";
    private final String someOtherPermission = "mkfgj34534f-2344r-rfe";

    @Mock
    private S3Config s3Config;

    @InjectMocks
    private ReportAccessCheckerService reportAccessCheckerService;

    @BeforeEach
    void beforeEach() {
        reset(s3Config);
    }

    @AfterEach
    void afterEach() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAllowAccessIfReportHasNoChecksAgainstItPermissionsList() {
        var reportID = UUID.fromString("8b9f0484-819f-4e0f-b60a-0b3f9d30d9ba");
        var auth = mock(Authentication.class);
        var principal = mock(DefaultOidcUser.class);

        when(auth.getPrincipal()).thenReturn(principal);
        when(principal.getClaimAsStringList("groups")).thenReturn(List.of(someOtherPermission));
        var mockSecurityContext = mock(SecurityContext.class);
        when(mockSecurityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(mockSecurityContext);

        assertTrue(reportAccessCheckerService.checkUserCanAccessReport(reportID));

    }

    @Test
    void shouldAllowAccessToRep000IfUserHasPermission() {
        mockRep000Config();
        setupAuthMocks(List.of(rep000Permission));
        assertTrue(reportAccessCheckerService.checkUserCanAccessReport(ID_REP000));
    }

    @Test
    void shouldBlockAccessToRep000IfUserLacksPermission() {
        mockRep000Config();
        // Give the user wrong permission for this report
        setupAuthMocks(List.of(submissionRecPermission));

        var ex = assertThrows(ReportAccessException.class, () -> reportAccessCheckerService.checkUserCanAccessReport(ID_REP000));
        assertEquals(ID_REP000, ex.getReportId());
    }

    @ParameterizedTest
    @MethodSource("submissionReconciliationReports")
    void shouldAllowAccessToSubmissionReconciliationReportsIfUserHasPermission(UUID testReport) {
        mockSubmissionReconciliationConfig();
        setupAuthMocks(List.of(submissionRecPermission));
        assertTrue(reportAccessCheckerService.checkUserCanAccessReport(ID_REP012));
    }

    @ParameterizedTest
    @MethodSource("submissionReconciliationReports")
    void shouldBlockAccessToSubmissionReconciliationReportsIfUserLacksPermission(UUID testReport) {
        mockSubmissionReconciliationConfig();
        // Give the user wrong permission for this report
        setupAuthMocks(List.of(rep000Permission));

        var ex = assertThrows(ReportAccessException.class, () -> reportAccessCheckerService.checkUserCanAccessReport(testReport));
        assertEquals(testReport, ex.getReportId());
    }

    @ParameterizedTest
    @MethodSource("allReports")
    void shouldAllowAccessToAllReportsIfAllPermissionsAreHad(UUID testReport) {

        // To stop it complaining that unused things are stubbed...
        if (testReport == ID_REP000) {
            mockRep000Config();
        } else {
            mockSubmissionReconciliationConfig();
        }

        setupAuthMocks(List.of(rep000Permission, submissionRecPermission, someOtherPermission));

        assertTrue(reportAccessCheckerService.checkUserCanAccessReport(testReport));

    }

    @ParameterizedTest
    @MethodSource("allReports")
    void shouldNotAllowAccessToReportsIfUserGotDifferentPermissionOnly(UUID testReport) {

        // To stop it complaining that unused things are stubbed...
        if (testReport == ID_REP000) {
            mockRep000Config();
        } else {
            mockSubmissionReconciliationConfig();
        }

        setupAuthMocks(List.of(someOtherPermission));

        var ex = assertThrows(ReportAccessException.class, () -> reportAccessCheckerService.checkUserCanAccessReport(testReport));
        assertEquals(testReport, ex.getReportId());

    }

    @ParameterizedTest
    @MethodSource("allReports")
    void shouldNotAllowAccessToReportsIfUserHasNoPermissions(UUID testReport) {

        // To stop it complaining that unused things are stubbed...
        if (testReport == ID_REP000) {
            mockRep000Config();
        } else {
            mockSubmissionReconciliationConfig();
        }

        setupAuthMocks(List.of());

        var ex = assertThrows(ReportAccessException.class, () -> reportAccessCheckerService.checkUserCanAccessReport(testReport));
        assertEquals(testReport, ex.getReportId());

    }

    private void setupAuthMocks(List<String> groups) {
        var mockAuth = mock(Authentication.class);
        var mockPrincipal = mock(DefaultOidcUser.class);

        when(mockAuth.getPrincipal()).thenReturn(mockPrincipal);
        when(mockPrincipal.getClaimAsStringList("groups")).thenReturn(groups);
        var mockSecurityContext = mock(SecurityContext.class);
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuth);
        SecurityContextHolder.setContext(mockSecurityContext);

    }

    private void mockRep000Config() {
        when(s3Config.getRep000GroupId()).thenReturn(rep000Permission);
    }

    private void mockSubmissionReconciliationConfig() {
        when(s3Config.getSubmissionReconciliationGroupId()).thenReturn(submissionRecPermission);
    }

    private static Stream<Arguments> submissionReconciliationReports() {
        return Stream.of(
                Arguments.of(ID_REP012),
                Arguments.of(ID_REP013)
        );
    }

    private static Stream<Arguments> allReports() {
        return Stream.of(
                Arguments.of(ID_REP000),
                Arguments.of(ID_REP012),
                Arguments.of(ID_REP013)
        );
    }

}