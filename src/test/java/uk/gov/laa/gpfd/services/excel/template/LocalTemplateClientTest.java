package uk.gov.laa.gpfd.services.excel.template;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.laa.gpfd.exception.TemplateResourceException.LocalTemplateReadException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import java.util.UUID;

class LocalTemplateClientTest {

    private TemplateClient client;

    @BeforeEach
    void setUp() {
        client = new LocalTemplateClient();
    }

    @Test
    @DisplayName("Should return input stream for valid template ID")
    void shouldReturnInputStreamForValidId() {
        assertNotNull(client.findTemplateById("00000000-0000-0000-0000-000000000000"));
        assertNotNull(client.findTemplateById("f46b4d3d-c100-429a-bf9a-223305dbdbfb"));
    }

    @Test
    @DisplayName("Should throw exception for unknown template ID")
    void shouldThrowExceptionForUnknownId() {
        var unknownId = UUID.randomUUID().toString();

        var exception = assertThrows(
                LocalTemplateReadException.class,
                () -> client.findTemplateById(unknownId)
        );

        assertEquals("Template not found for ID: " + unknownId, exception.getMessage());
    }

}