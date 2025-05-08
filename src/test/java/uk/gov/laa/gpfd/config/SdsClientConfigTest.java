package uk.gov.laa.gpfd.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = SdsClientConfig.class)
class SdsClientConfigTest {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    SdsClientConfig classUnderTest;

    @Test
    void shouldModelMapperIsNotNull() {
        var restClientBuilder = applicationContext.getBean(RestClient.Builder.class);
        assertNotNull(restClientBuilder, "RestClient.Builder bean should not be null.");
    }
}
