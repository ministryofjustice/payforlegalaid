package uk.gov.laa.gpfd.config;

import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Configuration class to set up the Springdoc OpenAPI integration for the Spring Boot application.
 * This class registers the necessary Springdoc beans for API documentation generation
 * and the Swagger UI configuration.
 */
@Configuration
public class SpringDocConfig {

    /**
     * Bean configuration for SpringDoc's core configuration.
     * <p>
     * This bean is responsible for setting up the core settings of Springdoc OpenAPI
     * which includes details like API documentation generation and OpenAPI spec.
     * </p>
     *
     * @return a new instance of {@link SpringDocConfiguration}.
     */
    @Bean
    SpringDocConfiguration springDocConfiguration() {
        return new SpringDocConfiguration();
    }

    /**
     * Bean configuration for SpringDoc configuration properties.
     * <p>
     * This bean provides a SpringDoc-specific configuration properties object
     * which is used for customization of the OpenAPI specification generation.
     * </p>
     *
     * @return a new instance of {@link SpringDocConfigProperties}.
     */
    @Bean
    SpringDocConfigProperties springDocConfigProperties() {
        return new SpringDocConfigProperties();
    }

    /**
     * Bean configuration for an {@link ObjectMapperProvider} that integrates
     * with SpringDoc to provide customized JSON object mapping for OpenAPI spec.
     * <p>
     * The {@link ObjectMapperProvider} is configured with SpringDoc's properties
     * to ensure that custom serialization and deserialization settings are applied
     * to the OpenAPI specification.
     * </p>
     *
     * @param springDocConfigProperties the SpringDoc configuration properties used
     *                                  for initializing the object mapper.
     * @return a new instance of {@link ObjectMapperProvider}.
     */
    @Bean
    ObjectMapperProvider objectMapperProvider(SpringDocConfigProperties springDocConfigProperties) {
        return new ObjectMapperProvider(springDocConfigProperties);
    }

}
