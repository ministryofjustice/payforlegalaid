package uk.gov.laa.pfla.configuration;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@CucumberContextConfiguration
@SpringBootTest(
        classes = uk.gov.laa.pfla.Main.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Import(CucumberTestBeans.class)
public class CucumberSpringConfiguration {
}