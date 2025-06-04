package uk.gov.laa.gpfd.services.excel.template;

import lombok.SneakyThrows;

import java.io.InputStream;
import java.util.UUID;

import static uk.gov.laa.gpfd.exception.TemplateResourceException.TemplateNotFoundException;
import static uk.gov.laa.gpfd.exception.TemplateResourceException.TemplateResourceNotFoundException;

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
    public InputStream findTemplateById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Template ID cannot be null or blank");
        }

        var filename = switch(id.toString()) {
            case "eee30b23-2c8d-4b4b-bb11-8cd67d07915c" -> "CCMS_AND_CIS_BANK_ACCOUNT_REPORT_W_CATEGORY_CODE_YTD.xlsx";
            case "f46b4d3d-c100-429a-bf9a-223305dbdbfb" -> "CCMS_GENERAL_LEDGER_EXTRACTOR_SMALL_MANUAL_BATCHES.xlsx";
            case "00000000-0000-0000-0000-000000000000" -> "CCMS_invoice analysis_template_v1_1.xlsx";
            case "7073dd13-e325-4863-a05c-a049a815d1f7" -> "LEGAL_HELP_CONTRACT_BALANCES.xlsx";
            case "7bda9aa4-6129-4c71-bd12-7d4e46fdd882" -> "LATE_PROCESSED_BILLS.xlsx";
            case "b380e788-2096-46dc-b58a-21bf771669dc" -> "MEDIATION_CONTRACT_BALANCES_TEMPLATE.xlsx";
            case "8b9f0484-819f-4e0f-b60a-0b3f9d30d9ba" -> "CCMS_THIRD_PARTY_REPORT.xlsx";
            default -> throw new TemplateNotFoundException("Template not found in resources for ID: " + id);
        };

        var resourceAsStream = getClass().getClassLoader().getResourceAsStream(filename);

        if (resourceAsStream == null) {
            throw new TemplateResourceNotFoundException("Template file '%s' not found in resources for ID: %s".formatted(filename, id));
        }

        return resourceAsStream;
    }

}
