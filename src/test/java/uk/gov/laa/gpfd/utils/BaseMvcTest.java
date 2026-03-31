package uk.gov.laa.gpfd.utils;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.laa.gpfd.config.TimeBasedAccessInterceptor;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public abstract class BaseMvcTest {

    @MockitoBean
    TimeBasedAccessInterceptor timeBasedAccessInterceptor;

    @BeforeEach
    void beforeEach() {
        when(timeBasedAccessInterceptor.preHandle(any(), any(), any())).thenReturn(true);
    }

    @Autowired
    protected MockMvc mockMvc;

    protected ResultActions performAuthenticatedGet(String uri, List<String> roles) throws Exception {
        return mockMvc.perform(
                get(uri)
                        .with(oidcLogin()
                                .idToken(token -> token.claim("LAA_APP_ROLES", roles)))
        );
    }

    protected ResultActions performGetRequest(String uriTemplate) throws Exception {
        return mockMvc.perform(
                MockMvcRequestBuilders.get(uriTemplate)
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }
}
