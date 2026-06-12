package uk.gov.laa.pfla;

import io.cucumber.java.BeforeAll;
import org.junit.platform.suite.api.ExcludeTags;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.SpringApplication;

@Suite
@SelectClasspathResource("features")
@ExcludeTags("NotReady")
public class RunCucumberTest {
    @BeforeAll
    public static void setup() {
        SpringApplication.run(Main.class);
    }
}
