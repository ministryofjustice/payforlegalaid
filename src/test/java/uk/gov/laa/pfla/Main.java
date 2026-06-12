package uk.gov.laa.pfla;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"uk.gov.laa.gpfd", "uk.gov.laa.pfla"})
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

