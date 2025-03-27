package uk.gov.laa.gpfd.config;

import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@Profile("testauth")
public class TestAuthConfig {

    //Mock the JwtDecoder to stop it trying to go off and load a JWKS file from the internet
    @MockitoBean
    JwtDecoder jwtDecoder;

}
