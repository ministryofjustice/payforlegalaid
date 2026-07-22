package uk.gov.laa.pfla;

import org.junit.platform.suite.api.ExcludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

//Suppressing warning to prevent Sonarcloud asking to test this file.
@SuppressWarnings("java:S2187")
@Suite
@SelectPackages("features")
@ExcludeTags("NotReady")
public class RunCucumberTest {
}
