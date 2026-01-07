package uk.gov.laa.gpfd.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("testauth")
class AppConfigTest {

    @Autowired
    ApplicationContext applicationContext;

    @Test
    void shouldReadOnlyDataSourceBeanWithQualifier() {
        // Given
        // When
        var dataSource = applicationContext.getBean("readOnlyDataSource", DataSource.class);

        // Then
        assertNotNull(dataSource, "ReadOnlyDataSource bean should be created.");
    }

    @Test
    void shouldWriteDataSourceBeanWithQualifier() {
        // Given
        // When
        var dataSource = applicationContext.getBean("writeDataSource", DataSource.class);

        // Then
        assertNotNull(dataSource, "WriteDataSource bean should be created.");
    }

    @Test
    void shouldJdbcTemplateWithQualifier() {
        // Given
        // When
        var jdbcTemplate = applicationContext.getBean("readOnlyJdbcTemplate", JdbcTemplate.class);

        // Then
        assertNotNull(jdbcTemplate, "JdbcTemplate bean should be created.");
    }

    @Test
    void shouldIncorrectBeanNameThrowsException() {
        assertThrows(NoSuchBeanDefinitionException.class, () ->
                applicationContext.getBean("incorrectBeanName"));
    }

    @Test
    void shouldMultipleJdbcTemplates() {
        // Given
        // When
        var readOnlyJdbcTemplate = applicationContext.getBean("readOnlyJdbcTemplate", JdbcTemplate.class);
        var writeJdbcTemplate = applicationContext.getBean("writeJdbcTemplate", JdbcTemplate.class);

        // Then
        assertNotNull(readOnlyJdbcTemplate, "Read-only JdbcTemplate bean should be created.");
        assertNotNull(writeJdbcTemplate, "Write-enabled JdbcTemplate bean should be created.");
    }

    @Test
    void shouldHaveARestTemplateBeanAndMessageConverters() {
        // Given
        // When
        var restTemplate = applicationContext.getBean(RestTemplate.class);

        // THen
        assertNotNull(restTemplate, "RestTemplate bean should be created.");
        assertEquals(2, restTemplate.getMessageConverters().size(), "RestTemplate should have two message converters.");
    }

    @Test
    void shouldRestTemplateTimeout() {
        // Given
        // When
        var restTemplate = applicationContext.getBean(RestTemplate.class);

        // Then
        assertNotNull(restTemplate.getRequestFactory(), "Request factory should be configured.");
    }

    @Test
    void shouldWriteDataSourceBean() {
        // Given
        // When
        var dataSource = applicationContext.getBean("writeDataSource", DataSource.class);

        // Then
        assertNotNull(dataSource, "WriteDataSource bean should be created.");
        assertInstanceOf(DriverManagerDataSource.class, dataSource, "DataSource should be of type DriverManagerDataSource.");
    }

    @Test
    void shouldReadOnlyJdbcTemplateBean() {
        // Given
        // When
        var jdbcTemplate = applicationContext.getBean("readOnlyJdbcTemplate", JdbcTemplate.class);

        // Then
        assertNotNull(jdbcTemplate, "ReadOnlyJdbcTemplate bean should be created.");
    }

    @Test
    void shouldWriteJdbcTemplateBean() {
        // Given
        // When
        var jdbcTemplate = applicationContext.getBean("writeJdbcTemplate", JdbcTemplate.class);
        assertNotNull(jdbcTemplate, "WriteJdbcTemplate bean should be created.");
    }

    @Test
    void shouldRestTemplateBeanNotNull() {
        // Given
        // When
        var restTemplate = applicationContext.getBean(RestTemplate.class);

        // Then
        assertNotNull(restTemplate, "RestTemplate bean should be created and should not be null.");
    }

    @Test
    void shouldRestTemplateStringHttpMessageConverter() {
        // Given
        // When
        var restTemplate = applicationContext.getBean(RestTemplate.class);

        // Then
        assertTrue(restTemplate.getMessageConverters().stream()
                        .anyMatch(converter -> converter instanceof StringHttpMessageConverter),
                "RestTemplate should contain StringHttpMessageConverter.");
    }

    @Test
    void shouldRestTemplateByteArrayHttpMessageConverter() {
        // Given
        // When
        var restTemplate = applicationContext.getBean(RestTemplate.class);

        // Then
        assertTrue(restTemplate.getMessageConverters().stream()
                        .anyMatch(converter -> converter instanceof ByteArrayHttpMessageConverter),
                "RestTemplate should contain ByteArrayHttpMessageConverter.");
    }

    @Test
    void shouldRestTemplateRequestInterceptor() {
        // Given
        // When
        var restTemplate = applicationContext.getBean(RestTemplate.class);
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("X-Test-Header", "TestValue");
            return execution.execute(request, body);
        });

        // Then
        assertTrue(restTemplate.getInterceptors().stream()
                        .anyMatch(i -> i instanceof ClientHttpRequestInterceptor),
                "RestTemplate should have interceptors.");
    }
}
