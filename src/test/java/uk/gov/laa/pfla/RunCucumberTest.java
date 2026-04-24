package uk.gov.laa.pfla;

import org.junit.platform.suite.api.ExcludeTags;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasspathResource("features")
@ExcludeTags("NotReady")
public class RunCucumberTest {
}
