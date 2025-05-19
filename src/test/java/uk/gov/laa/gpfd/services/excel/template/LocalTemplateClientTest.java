package uk.gov.laa.gpfd.services.excel.template;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class LocalTemplateClientTest {

    private TemplateClient client;

    @BeforeEach
    void setUp() {
        client = new LocalTemplateClient();
    }

    @ParameterizedTest
    @DisplayName("Should return input stream for valid template ID")
    @ValueSource(strings = {
            "00000000-0000-0000-0000-000000000000",
            "f46b4d3d-c100-429a-bf9a-223305dbdbfb",
            "eee30b23-2c8d-4b4b-bb11-8cd67d07915c"
    })
    void shouldReturnInputStreamForValidId(String id) {
        assertNotNull(client.findTemplateById(id));
    }

    @Test
    void shouldThrowsExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> client.findTemplateById(null));
    }

}