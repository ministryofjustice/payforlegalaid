package uk.gov.laa.gpfd.services.s3;

import org.junit.jupiter.api.Test;
import uk.gov.laa.gpfd.exception.ReportNotSupportedForDownloadException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.laa.gpfd.utils.TokenUtils.ID_REP000;
import static uk.gov.laa.gpfd.utils.TokenUtils.ID_REP012;
import static uk.gov.laa.gpfd.utils.TokenUtils.ID_REP013;

class ReportFileNameResolverTest {

    private final ReportFileNameResolver reportFileNameResolver = new ReportFileNameResolver();

    @Test
    void shouldReturnFileNameForRep000() {
        assertEquals("report_000.csv", reportFileNameResolver.getFileNameFromId(ID_REP000));
    }

    @Test
    void shouldReturnFileNameForRep012() {
        assertEquals("report_012.csv", reportFileNameResolver.getFileNameFromId(ID_REP012));
    }

    @Test
    void shouldReturnFileNameForRep013() {
        assertEquals("report_013.csv", reportFileNameResolver.getFileNameFromId(ID_REP013));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionIfBlankUUIDSupplied() {
        assertThrows(IllegalArgumentException.class, () -> reportFileNameResolver.getFileNameFromId(UUID.fromString("")));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionIfNullUUIDSupplied() {
        assertThrows(IllegalArgumentException.class, () -> reportFileNameResolver.getFileNameFromId(null));
    }

    @Test
    void shouldThrowNotSupportedExceptionIfIdIsNotInList() {
        assertThrows(ReportNotSupportedForDownloadException.class, () -> reportFileNameResolver.getFileNameFromId(UUID.fromString("bda2120c-8f82-45a8-a682-8dedfb7997a7")));
    }

}