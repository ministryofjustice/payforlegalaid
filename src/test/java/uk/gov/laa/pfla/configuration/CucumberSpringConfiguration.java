package uk.gov.laa.pfla.configuration;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import uk.gov.laa.gpfd.Application;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@CucumberContextConfiguration
@ActiveProfiles("testat")
@TestPropertySource(locations = "classpath:application-testat.yml", properties = "spring.liquibase.enabled=true")
@Import({uk.gov.laa.pfla.configuration.TestConfig.class, uk.gov.laa.pfla.scenario.ScenarioContext.class})
public class CucumberSpringConfiguration {
}