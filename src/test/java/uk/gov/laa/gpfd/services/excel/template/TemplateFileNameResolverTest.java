package uk.gov.laa.gpfd.services.excel.template;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TemplateFileNameResolverTest {

    private final TemplateFileNameResolver templateFileNameResolver = new TemplateFileNameResolver();

    @Test
    void shouldThrowTemplateNotFoundForUnknownId() {
        assertThrows(uk.gov.laa.gpfd.exception.TemplateResourceException.TemplateNotFoundException.class,
                () -> templateFileNameResolver.getFileNameFromId(UUID.randomUUID()));
    }

    @Test
    void shouldThrowErrorIfGivenNull(){
        assertThrows(IllegalArgumentException.class, () -> templateFileNameResolver.getFileNameFromId(null));
    }

}