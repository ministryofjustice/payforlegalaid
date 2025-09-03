package uk.gov.laa.gpfd.services.excel.template;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.gpfd.exception.TemplateResourceException.TemplateResourceNotFoundException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocalTemplateClientTest {

    @Mock
    private FileNameResolver fileNameResolver;

    @InjectMocks
    private LocalTemplateClient localTemplateClient;

    private final UUID testUUID = UUID.randomUUID();

    @BeforeEach
    void resetMocks() {
        reset(fileNameResolver);
    }

    @Test
    void shouldReturnInputStreamForValidId() {
        when(fileNameResolver.getFileNameFromId(testUUID)).thenReturn("testTemplate.xlsx");
        assertNotNull(localTemplateClient.findTemplateById(testUUID));
    }

    @Test
    void shouldReturnNullForNullFilename() {
        when(fileNameResolver.getFileNameFromId(testUUID)).thenReturn(null);
        assertNull(localTemplateClient.findTemplateById(testUUID));
    }

    @Test
    void shouldThrowsExceptionForFileThatDoesNotExist() {
        when(fileNameResolver.getFileNameFromId(testUUID)).thenReturn("notATestTemplate.xlsx");
        assertThrows(TemplateResourceNotFoundException.class, () -> localTemplateClient.findTemplateById(testUUID));
    }

}