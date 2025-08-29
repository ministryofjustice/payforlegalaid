package uk.gov.laa.gpfd.services.excel.template;

import org.apache.poi.ss.usermodel.Workbook;
import uk.gov.laa.gpfd.exception.TemplateResourceException;

import java.io.InputStream;
import java.util.UUID;

/**
 * The interface is a functional interface designed to retrieve an Excel template
 * as an {@link InputStream} based on a unique identifier. It provides a single method, {@link #findTemplateById(UUID)},
 * which is responsible for locating and returning the template.
 */
@FunctionalInterface
public interface TemplateClient {

    /**
     * Retrieves an Excel template as an {@link InputStream} based on the provided unique identifier.
     * The method is responsible for locating the template and returning it in a format suitable for
     * further processing, such as loading into an Apache POI {@link Workbook}.
     *
     * @param id the unique identifier of the template to retrieve
     * @return an {@link InputStream} containing the template data
     */
    InputStream findTemplateById(UUID id);

    /**
     * Get the template file-name from the ID
     * @param id the unique identifier of the template
     * @return the filename, or null if there is none for that report.
     */
    default String getFileNameFromId(UUID id){
        if (id == null) {
            throw new IllegalArgumentException("Template ID cannot be null or blank");
        }

        var filename = switch(id.toString()) {
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

        return filename;

    }
}

/* interface Animal {

   String makeANoise();

}

 class Cat implements Animal{
     String makeANoise(){
        return "meow";
     }
  }

   class Dog implements Animal{
     String makeANoise(){
        return "woof";
     }
  }
 */