package uk.gov.laa.gpfd.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtConfigTest {

    @Mock
    private AppConfig appConfig;

    @Mock
    private JwtDecoder mockJwtDecoder;

    @InjectMocks
    private JwtConfig jwtConfig;

    @Test
    void shouldSetUpAJwtDecoder() {

        String jwksUri = "http://localhost";
        when(appConfig.getJwksUri()).thenReturn(jwksUri);

        try (MockedStatic<JwtDecoders> mockedJwtDecoders = Mockito.mockStatic(JwtDecoders.class)) {
            // Mock the JwtDecoders so it does not try and do a call to the internet to create the decoder
            mockedJwtDecoders.when(() -> JwtDecoders.fromIssuerLocation(jwksUri)).thenReturn(mockJwtDecoder);

            JwtDecoder jwtDecoderBean = jwtConfig.jwtDecoder();

            assertEquals(mockJwtDecoder, jwtDecoderBean);

            mockedJwtDecoders.verify(() -> JwtDecoders.fromIssuerLocation(jwksUri));
        }

    }

}
