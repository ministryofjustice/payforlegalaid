package uk.gov.laa.gpfd;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class StartupLogger implements ApplicationRunner {

  private static final Logger logger = LoggerFactory.getLogger(StartupLogger.class);

  @Value("${gpfd.url}")
  private String gpfdUrl;
  @Value("${gpfd.redirect-uri-template}")
  private String redirectUri;


  @Bean
  public OAuth2AuthorizationRequestResolver customAuthorizationRequestResolver(
      ClientRegistrationRepository clientRegistrationRepository) {

    DefaultOAuth2AuthorizationRequestResolver defaultResolver =
        new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization");

    return new OAuth2AuthorizationRequestResolver() {
      @Override
      public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest req = defaultResolver.resolve(request);
        logRedirectUri(req);
        return req;
      }

      @Override
      public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest req = defaultResolver.resolve(request, clientRegistrationId);
        logRedirectUri(req);
        return req;
      }

      private void logRedirectUri(OAuth2AuthorizationRequest req) {
        if (req != null) {
          logger.info("vvvv Redirect URI from OAuth: " + req.getRedirectUri());
        }
      }
    };
  }
  @Override
  public void run(ApplicationArguments args) {
    logger.info("vvvv GPFD URL: {}, redirect URI: {}", gpfdUrl, redirectUri);
  }
}