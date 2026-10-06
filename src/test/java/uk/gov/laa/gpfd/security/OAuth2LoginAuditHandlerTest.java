package uk.gov.laa.gpfd.security;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.WebAttributes;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import uk.gov.laa.gpfd.utils.RequestLogUtils;

class OAuth2LoginAuditHandlerTest {

    private static final String SUBJECT = "entra-subject-123";
    private static final String OID = "11111111-2222-3333-4444-555555555555";
    private static final String EMAIL = "someone@justice.gov.uk";

    private final OAuth2LoginAuditHandler handler = new OAuth2LoginAuditHandler();
    private final Logger logger = (Logger) LoggerFactory.getLogger(OAuth2LoginAuditHandler.class);
    private final ListAppender<ILoggingEvent> appender = new ListAppender<>();
    private Level originalLevel;

    @BeforeEach
    void attachAppender() {
        originalLevel = logger.getLevel();
        appender.setContext(logger.getLoggerContext());
        appender.start();
        logger.addAppender(appender);
        logger.setLevel(Level.INFO);
    }

    @AfterEach
    void detachAppender() {
        logger.detachAppender(appender);
        appender.stop();
        logger.setLevel(originalLevel);
    }

    @Test
    void successLogsStructuredAuditEventAndRedirectsHome() throws Exception {
        var request = callbackRequest("/login/oauth2/code/gpfd-azure-dev");
        var response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(request, response, oidcAuthentication("gpfd-azure-dev"));

        assertThat(response.getRedirectedUrl()).isEqualTo("/");
        var event = singleEvent();
        assertThat(event.getLevel()).isEqualTo(Level.INFO);
        assertThat(event.getFormattedMessage()).isEqualTo("Entra ID login succeeded");
        assertThat(keyValues(event)).containsExactlyInAnyOrderEntriesOf(Map.of(
                RequestLogUtils.EVENT_ACTION, OAuth2LoginAuditHandler.EVENT_ACTION,
                RequestLogUtils.EVENT_OUTCOME, "success",
                "event.type", "authentication",
                "event.category", "authentication",
                OAuth2LoginAuditHandler.SOURCE_IP, "203.0.113.10",
                OAuth2LoginAuditHandler.REGISTRATION_ID, "gpfd-azure-dev",
                RequestLogUtils.USER_ID, RequestLogUtils.extractUserId(oidcAuthentication("gpfd-azure-dev"))));
    }

    @Test
    void successDoesNotLogRawIdentifiersOrClaims() throws Exception {
        handler.onAuthenticationSuccess(callbackRequest("/login/oauth2/code/entra"),
                new MockHttpServletResponse(), oidcAuthentication("entra"));

        var event = singleEvent();
        var logged = event.getFormattedMessage() + keyValues(event).values();
        assertThat(logged).doesNotContain(SUBJECT, OID, EMAIL, "LAA_APP_ROLES", "id-token-value");
    }

    @Test
    void oauth2FailureLogsErrorCodeAndRedirectsToLoginError() throws Exception {
        var request = callbackRequest("/login/oauth2/code/gpfd-azure-dev");
        var response = new MockHttpServletResponse();
        var exception = new OAuth2AuthenticationException(
                new OAuth2Error("invalid_id_token", "AADSTS50011: sensitive IdP description", null));

        handler.onAuthenticationFailure(request, response, exception);

        assertThat(response.getRedirectedUrl()).isEqualTo("/login?error");
        assertThat(request.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION)).isSameAs(exception);
        var event = singleEvent();
        assertThat(event.getLevel()).isEqualTo(Level.WARN);
        assertThat(event.getFormattedMessage()).isEqualTo("Entra ID login failed");
        assertThat(keyValues(event)).containsExactlyInAnyOrderEntriesOf(Map.of(
                RequestLogUtils.EVENT_ACTION, OAuth2LoginAuditHandler.EVENT_ACTION,
                RequestLogUtils.EVENT_OUTCOME, "failure",
                "event.type", "authentication",
                "event.category", "authentication",
                OAuth2LoginAuditHandler.SOURCE_IP, "203.0.113.10",
                OAuth2LoginAuditHandler.REGISTRATION_ID, "gpfd-azure-dev",
                OAuth2LoginAuditHandler.ERROR_TYPE, "OAuth2AuthenticationException",
                OAuth2LoginAuditHandler.ERROR_CODE, "invalid_id_token"));
        assertThat(keyValues(event).values().toString()).doesNotContain("AADSTS50011");
    }

    @Test
    void nonOAuth2FailureLogsTypeWithoutErrorCode() throws Exception {
        var response = new MockHttpServletResponse();

        handler.onAuthenticationFailure(callbackRequest("/login/oauth2/code/entra"), response,
                new BadCredentialsException("bad"));

        assertThat(response.getRedirectedUrl()).isEqualTo("/login?error");
        var keyValues = keyValues(singleEvent());
        assertThat(keyValues)
                .containsEntry(OAuth2LoginAuditHandler.ERROR_TYPE, "BadCredentialsException")
                .doesNotContainKey(OAuth2LoginAuditHandler.ERROR_CODE)
                .doesNotContainKey(RequestLogUtils.USER_ID);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/login/oauth2/code/bad%0Aid", "/login/oauth2/code/a/b", "/somewhere-else"})
    void failureOmitsUntrustedRegistrationId(String uri) throws Exception {
        handler.onAuthenticationFailure(callbackRequest(uri), new MockHttpServletResponse(),
                new OAuth2AuthenticationException("authorization_request_not_found"));

        assertThat(keyValues(singleEvent())).doesNotContainKey(OAuth2LoginAuditHandler.REGISTRATION_ID);
    }

    private static MockHttpServletRequest callbackRequest(String uri) {
        var request = new MockHttpServletRequest("GET", uri);
        request.setRemoteAddr("203.0.113.10");
        return request;
    }

    private static OAuth2AuthenticationToken oidcAuthentication(String registrationId) {
        var now = Instant.now();
        var idToken = new OidcIdToken("id-token-value", now, now.plusSeconds(300), Map.of(
                "sub", SUBJECT,
                "oid", OID,
                "preferred_username", EMAIL,
                "LAA_APP_ROLES", List.of("Financial")));
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        return new OAuth2AuthenticationToken(new DefaultOidcUser(authorities, idToken), authorities, registrationId);
    }

    private ILoggingEvent singleEvent() {
        assertThat(appender.list).hasSize(1);
        return appender.list.getFirst();
    }

    private static Map<String, Object> keyValues(ILoggingEvent event) {
        return event.getKeyValuePairs().stream()
                .collect(Collectors.toMap(kv -> kv.key, kv -> kv.value));
    }
}
