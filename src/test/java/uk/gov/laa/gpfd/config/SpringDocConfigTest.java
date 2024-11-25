package uk.gov.laa.gpfd.config;

import org.junit.jupiter.api.Test;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class SpringDocConfigTest {

    @Autowired
    ApplicationContext applicationContext;

    @Test
    void shouldSpringDocConfigurationBean() {
        var springDocConfiguration = applicationContext.getBean(SpringDocConfiguration.class);
        assertNotNull(springDocConfiguration, "SpringDocConfiguration bean should be created.");
    }

    @Test
    void shouldSpringDocConfigPropertiesBean() {
        var springDocConfigProperties = applicationContext.getBean(SpringDocConfigProperties.class);
        assertNotNull(springDocConfigProperties, "SpringDocConfigProperties bean should be created.");
    }

    @Test
    void shouldObjectMapperProviderBean() {
        var objectMapperProvider = applicationContext.getBean(ObjectMapperProvider.class);
        assertNotNull(objectMapperProvider, "ObjectMapperProvider bean should be created.");
    }

    @Test
    void shouldBeanPresence() {
        assertNotNull(applicationContext.getBean(SpringDocConfiguration.class), "SpringDocConfiguration bean should be available.");
        assertNotNull(applicationContext.getBean(SpringDocConfigProperties.class), "SpringDocConfigProperties bean should be available.");
        assertNotNull(applicationContext.getBean(ObjectMapperProvider.class), "ObjectMapperProvider bean should be available.");
    }

}
