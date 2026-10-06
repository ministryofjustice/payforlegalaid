package uk.gov.laa.pfla;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

//Suppressing warning to prevent Sonarcloud asking to remove the unused 'args' variable below.
@SuppressWarnings("java:S1172")
@SpringBootApplication
@ComponentScan(
    basePackages = {"uk.gov.laa.gpfd", "uk.gov.laa.pfla"},
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.REGEX,
        pattern = "uk\\.gov\\.laa\\.gpfd\\.config\\.MockAuthSecurityConfigTest\\$TestConfiguration"
    )
)
public class Main {

    public static void main(String[] args) {
        String[] cucumberArgs = {
                "--glue", "uk.gov.laa.pfla",
                "--plugin", "pretty",
                "classpath:features"
        };
        byte exitStatus = io.cucumber.core.cli.Main.run(cucumberArgs, Thread.currentThread().getContextClassLoader());
        if (exitStatus == 0) {
            System.exit(exitStatus);
        } else {
            System.exit(-1);
        }
    }
}

