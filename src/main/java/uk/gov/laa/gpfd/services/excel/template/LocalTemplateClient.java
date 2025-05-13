package uk.gov.laa.gpfd.services.excel.template;

import lombok.SneakyThrows;

import java.io.InputStream;

import static uk.gov.laa.gpfd.exception.TemplateResourceException.LocalTemplateReadException;

/**
 * A local implementation of {@link TemplateClient} that provides template resources
 * from the classpath. This client loads templates during initialization and serves
 * them from memory.
 * <p>
 * Templates are identified by UUID strings and mapped to Excel file resources packaged
 * with the application.
 */
public record LocalTemplateClient() implements TemplateClient {

    @Override
    @SneakyThrows
    public InputStream findTemplateById(String id) {
        var filename = switch(id) {
            case "eee30b23-2c8d-4b4b-bb11-8cd67d07915c" -> "CCMS_AND_CIS_BANK_ACCOUNT_REPORT_W_CATEGORY_CODE_YTD.xlsx";
            case "f46b4d3d-c100-429a-bf9a-223305dbdbfb" -> "CCMS_GENERAL_LEDGER_EXTRACTOR_SMALL_MANUAL_BATCHES.xlsx";
            default -> "CCMS_invoice analysis_template_v1_1.xlsx";
        };

        var resourceAsStream = getClass().getClassLoader().getResourceAsStream(filename);

        if (resourceAsStream == null) {
            throw new LocalTemplateReadException("Template not found in resources for ID: " + id);
        }

        return resourceAsStream;
    }

}