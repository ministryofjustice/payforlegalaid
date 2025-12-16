package uk.gov.laa.gpfd.services.s3;

import org.junit.jupiter.api.Test;
import uk.gov.laa.gpfd.exception.FileDownloadException.ReportNotSupportedForDownloadException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.laa.gpfd.utils.TokenUtils.ID_REP000;
import static uk.gov.laa.gpfd.utils.TokenUtils.ID_REP012;
import static uk.gov.laa.gpfd.utils.TokenUtils.ID_REP013;

class ReportFileNameResolverTest {

    private final ReportFileNameResolver reportFileNameResolver = new ReportFileNameResolver();

    @Test
    void shouldReturnS3PrefixForRep000() {
        assertEquals("reports/monthly/report_000", reportFileNameResolver.getS3PrefixFromId(ID_REP000));
    }

    @Test
    void shouldReturnS3PrefixForRep012() {
        assertEquals("reports/daily/report_012", reportFileNameResolver.getS3PrefixFromId(ID_REP012));
    }

    @Test
    void shouldReturnS3PrefixForRep013() {
        assertEquals("reports/daily/report_013", reportFileNameResolver.getS3PrefixFromId(ID_REP013));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionIfBlankUUIDSupplied() {
        assertThrows(IllegalArgumentException.class, () -> reportFileNameResolver.getS3PrefixFromId(UUID.fromString("")));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionIfNullUUIDSupplied() {
        assertThrows(IllegalArgumentException.class, () -> reportFileNameResolver.getS3PrefixFromId(null));
    }

    @Test
    void shouldThrowNotSupportedExceptionIfIdIsNotInList() {
        assertThrows(ReportNotSupportedForDownloadException.class, () -> reportFileNameResolver.getS3PrefixFromId(UUID.fromString("bda2120c-8f82-45a8-a682-8dedfb7997a7")));
    }

}