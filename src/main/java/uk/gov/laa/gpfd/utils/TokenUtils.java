package uk.gov.laa.gpfd.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import uk.gov.laa.gpfd.exception.UnableToParseAuthDetailsException.AuthenticationIsNullException;
import uk.gov.laa.gpfd.exception.UnableToParseAuthDetailsException.PrincipalIsNullException;
import uk.gov.laa.gpfd.exception.UnableToParseAuthDetailsException.UnexpectedAuthClassException;

import java.util.List;
import java.util.UUID;

public abstract class TokenUtils {

    private TokenUtils() {
     // Can't instantiate utility class
    }

    public static final UUID ID_REP000 = UUID.fromString("523f38f0-2179-4824-b885-3a38c5e149e8");
    public static final UUID ID_REP012 = UUID.fromString("cc55e276-97b0-4dd8-a919-26d4aa373266");
    public static final UUID ID_REP013 = UUID.fromString("aca2120c-8f82-45a8-a682-8dedfb7997a7");
    public static final UUID ID_REP014 = UUID.fromString("55daf3c1-28f0-4260-9396-2ee6d537abab");

}
