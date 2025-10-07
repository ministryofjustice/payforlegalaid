package uk.gov.laa.gpfd.services.s3;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReportFileNameResolverTest {

    private final ReportFileNameResolver reportFileNameResolver = new ReportFileNameResolver();

    @Test
    void shouldReturnFileNameForRep000() {
        assertEquals("report_000.csv", reportFileNameResolver.getFileNameFromId(UUID.fromString("523f38f0-2179-4824-b885-3a38c5e149e8")));
    }

    @Test
    void shouldReturnFileNameForRep012() {
        assertEquals("report_012.csv", reportFileNameResolver.getFileNameFromId(UUID.fromString("cc55e276-97b0-4dd8-a919-26d4aa373266")));
    }

    @Test
    void shouldReturnFileNameForRep013() {
        assertEquals("report_013.csv", reportFileNameResolver.getFileNameFromId(UUID.fromString("aca2120c-8f82-45a8-a682-8dedfb7997a7")));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionIfBlankUUIDSupplied() {
        assertThrows(IllegalArgumentException.class, () -> reportFileNameResolver.getFileNameFromId(UUID.fromString("")));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionIfNullUUIDSupplied() {
        assertThrows(IllegalArgumentException.class, () -> reportFileNameResolver.getFileNameFromId(null));
    }


}