package uk.gov.laa.gpfd;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class StartupLogger implements ApplicationRunner {

  private static final Logger logger = LoggerFactory.getLogger(StartupLogger.class);

  @Value("${gpfd.url}")
  private String appName;

  @Override
  public void run(ApplicationArguments args) {
    logger.info("vvvv GPFD URL: {}", appName);
  }
}