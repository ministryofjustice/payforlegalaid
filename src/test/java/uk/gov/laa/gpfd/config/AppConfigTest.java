package uk.gov.laa.gpfd.config;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(classes = AppConfig.class)
@TestPropertySource(properties = {"gpfd.url=http://localhost", "gpfd.allowed.redirect-uri: http://localhost, http://localhos:3000"})
class AppConfigTest {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    AppConfig classUnderTest;

    @Test
    void shouldModelMapperIsNotNull() {
        // Given
        // When
        var modelMapper = applicationContext.getBean(ModelMapper.class);

        // Then
        assertNotNull(modelMapper, "ModelMapper bean should not be null.");
    }

    @Test
    void shouldFetchModelMapperConfiguration() {
        // Given
        // When
        var modelMapper = applicationContext.getBean(ModelMapper.class);

        // Then
        assertDoesNotThrow(() -> modelMapper.map(new Object(), Object.class), "ModelMapper should not throw an exception.");
    }

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
    void shouldRestTemplateMessageConverters() {
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
    void shouldJdbcTemplateDataSource() {
        // Given
        // When
        var jdbcTemplate = applicationContext.getBean("readOnlyJdbcTemplate", JdbcTemplate.class);

        // Then
        assertInstanceOf(DriverManagerDataSource.class, jdbcTemplate.getDataSource(), "JdbcTemplate should be using the correct DataSource.");
    }

    @Test
    void shouldModelMapperBean() {
        // Given
        // When
        var modelMapper = applicationContext.getBean(ModelMapper.class);

        // Then
        assertNotNull(modelMapper, "ModelMapper bean should be created.");
    }

    @Test
    void shouldReadOnlyDataSourceBean() {
        // Given
        // When
        var dataSource = applicationContext.getBean("readOnlyDataSource", DataSource.class);

        // Then
        assertNotNull(dataSource, "ReadOnlyDataSource bean should be created.");
        assertInstanceOf(DriverManagerDataSource.class, dataSource, "DataSource should be of type DriverManagerDataSource.");
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
    void shouldApplicationContextContainsAllBeans() {
        assertNotNull(applicationContext.getBean(ModelMapper.class), "ModelMapper should be available in the application context.");
        assertNotNull(applicationContext.getBean("readOnlyDataSource", DataSource.class), "Read-only DataSource should be available.");
        assertNotNull(applicationContext.getBean("writeDataSource", DataSource.class), "Write-enabled DataSource should be available.");
    }

    @Test
    void shouldBeanRetrievalByType() {
        // Given
        // When
        var modelMapper = applicationContext.getBean(ModelMapper.class);

        // Then
        assertNotNull(modelMapper, "ModelMapper bean should be available by type.");
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
    void shouldRestTemplateBean() {
        // Given
        // When
        var restTemplate = applicationContext.getBean(RestTemplate.class);
        assertNotNull(restTemplate, "RestTemplate bean should be created.");

        // Then
        assertEquals(2, restTemplate.getMessageConverters().size(), "RestTemplate should have two message converters.");
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

    @Test
    void shouldReturnServiceUrl() {

        assertTrue(classUnderTest.getServiceUrl().contentEquals("http://localhost"));
    }

    @Test
    void shouldReturnTrueIfUriIsInWhiteList() {

        assertTrue(classUnderTest.isValidRedirectUri("http://localhos:3000"));

    }

    @Test
    void shouldReturnFalseIfUriIsNotInWhiteList() {

        assertFalse(classUnderTest.isValidRedirectUri("http://localhost:3000"));

    }
}
