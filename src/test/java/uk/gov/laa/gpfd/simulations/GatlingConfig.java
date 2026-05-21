package uk.gov.laa.gpfd.simulations;

import java.io.InputStream;
import java.util.Properties;

public class GatlingConfig {

    public static final String BASE_URL = resolveBaseUrl();

    private static String resolveBaseUrl() {
        try (InputStream is = GatlingConfig.class
                .getClassLoader()
                .getResourceAsStream("gatling.properties")) {

            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                return props.getProperty("gatling.baseUrl", "http://localhost:8080");
            }
        } catch (Exception e) {
            System.err.println("Could not load gatling.properties: " + e.getMessage());
        }
        return "http://localhost:8080";
    }
}
