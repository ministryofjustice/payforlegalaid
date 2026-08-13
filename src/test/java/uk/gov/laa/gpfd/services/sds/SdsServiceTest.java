package uk.gov.laa.gpfd.services.sds;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.gpfd.exception.ServiceUnavailableException;
import uk.gov.laa.gpfd.exception.sds.SdsFileNotFoundException;
import uk.gov.laa.gpfd.services.sds.client.ApiException;
import uk.gov.laa.gpfd.services.sds.client.api.FilesApi;
import uk.gov.laa.gpfd.services.sds.client.api.HealthApi;
import uk.gov.laa.gpfd.services.sds.client.model.SdsFileDetail;
import uk.gov.laa.gpfd.services.sds.client.model.SdsFileDetailsResponse;
import uk.gov.laa.gpfd.services.sds.client.model.SdsHealthResponse;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SdsServiceTest {

    @Mock
    private FilesApi filesApi;

    @Mock
    private HealthApi healthApi;

    @Test
    void getFileDetails_shouldMapVersionHistoryIntoDomainModel() throws ApiException {
        var versionOne = mock(SdsFileDetail.class);
        var versionTwo = mock(SdsFileDetail.class);
        when(versionOne.getLastModified()).thenReturn(OffsetDateTime.parse("2026-04-23T07:51:26+00:00"));
        when(versionTwo.getLastModified()).thenReturn(OffsetDateTime.parse("2026-04-24T13:39:38+00:00"));

        var response = mock(SdsFileDetailsResponse.class);
        when(response.getVersionHistory()).thenReturn(List.of(versionOne, versionTwo));
        when(filesApi.getFileDetails("README.md")).thenReturn(response);

        var service = new SdsService(filesApi, healthApi);

        var result = service.getFileDetails("README.md");

        assertEquals(2, result.files().size());
        assertEquals(OffsetDateTime.parse("2026-04-23T07:51:26+00:00"), result.files().get(0).lastModified());
        assertEquals(OffsetDateTime.parse("2026-04-24T13:39:38+00:00"), result.files().get(1).lastModified());
    }

    @Test
    void getFileDetails_shouldThrowNotFoundWhenSdsReturns404() throws ApiException {
        var apiException = mock(ApiException.class);
        when(apiException.getCode()).thenReturn(404);
        when(filesApi.getFileDetails("README.md")).thenThrow(apiException);

        var service = new SdsService(filesApi, healthApi);

        assertThrows(SdsFileNotFoundException.class, () -> service.getFileDetails("README.md"));
    }

    @Test
    void getFileDetails_shouldThrowServiceUnavailableWhenSdsReturns503() throws ApiException {
        var apiException = mock(ApiException.class);
        when(apiException.getCode()).thenReturn(503);
        when(apiException.getMessage()).thenReturn("service unavailable");
        when(filesApi.getFileDetails("README.md")).thenThrow(apiException);

        var service = new SdsService(filesApi, healthApi);

        assertThrows(ServiceUnavailableException.class, () -> service.getFileDetails("README.md"));
    }

    @Test
    void getHealth_shouldReturnHealthResponse() throws ApiException {
        var healthResponse = mock(SdsHealthResponse.class);
        when(healthApi.getHealth()).thenReturn(healthResponse);

        var service = new SdsService(filesApi, healthApi);

        assertEquals(healthResponse, service.getHealth());
    }
}
