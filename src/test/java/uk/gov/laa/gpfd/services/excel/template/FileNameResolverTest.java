package uk.gov.laa.gpfd.services.excel.template;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FileNameResolverTest {

    private final FileNameResolver fileNameResolver = new FileNameResolver();

    @Test
    void shouldReturnFilenameForGivenId(){
        var result = fileNameResolver.getFileNameFromId(UUID.fromString("eee30b23-2c8d-4b4b-bb11-8cd67d07915c"));
        assertEquals("CCMS_AND_CIS_BANK_ACCOUNT_REPORT_W_CATEGORY_CODE_YTD.xlsx", result);
    }

    @Test
    void shouldThrowErrorIfGivenNull(){
        assertThrows(IllegalArgumentException.class, () -> fileNameResolver.getFileNameFromId(null));
    }

}