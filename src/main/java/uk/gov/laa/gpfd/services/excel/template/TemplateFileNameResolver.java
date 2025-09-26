package uk.gov.laa.gpfd.services.excel.template;

import uk.gov.laa.gpfd.exception.TemplateResourceException;

import java.util.UUID;

/**
 * Allows us to go from a template id (a UUID from the database) to a file name, which the associated
 * {@link TemplateClient} can then use to fetch the template.
 */
public class TemplateFileNameResolver {

    /**
     * Get the template file-name from the ID
     * @param id the unique identifier of the template
     * @return the filename, or null if there is none for that report.
     */
    String getFileNameFromId(UUID id){
        if (id == null) {
            throw new IllegalArgumentException("Template ID cannot be null or blank");
        }

        return switch(id.toString()) {
            case "eee30b23-2c8d-4b4b-bb11-8cd67d07915c" -> "CCMS_AND_CIS_BANK_ACCOUNT_REPORT_W_CATEGORY_CODE_YTD.xlsx";
            case "a017241a-359f-4fdb-a0cd-7f28f1946ef1" -> "CCMS_AND_CIS_BANK_ACCOUNT_REPORT_W_CATEGORY_CODE_MNTH.xlsx";
            case "f46b4d3d-c100-429a-bf9a-223305dbdbfb" -> null;
            case "00000000-0000-0000-0000-000000000000" -> "CCMS_invoice analysis_template_v1_1.xlsx";
            case "7073dd13-e325-4863-a05c-a049a815d1f7" -> "LEGAL_HELP_CONTRACT_BALANCES.xlsx";
            case "7bda9aa4-6129-4c71-bd12-7d4e46fdd882" -> "LATE_PROCESSED_BILLS.xlsx";
            case "b380e788-2096-46dc-b58a-21bf771669dc" -> "MEDIATION_CONTRACT_BALANCES_TEMPLATE.xlsx";
            case "8b9f0484-819f-4e0f-b60a-0b3f9d30d9ba" -> "CCMS_THIRD_PARTY_REPORT.xlsx";
            case "22fe2b17-eea8-4c74-929d-9c69503f25d3" -> "C12_LATE_PROCESSED_BILLS_CIS_TEMPLATE.xlsx";
            default -> throw new TemplateResourceException.TemplateNotFoundException("Template not found in resources for ID: " + id);
        };

    }
}
