package uk.gov.laa.gpfd.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;

public abstract class BaseMvcTest {

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
