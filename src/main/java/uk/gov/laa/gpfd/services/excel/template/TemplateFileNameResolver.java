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

        /*
        Code removed as we want to remove all references to the old reports and templates.
        Leaving the code here in case we decide to reuse it for future Excel file generation.
        */
        throw new TemplateResourceException.TemplateNotFoundException("Template not found in resources for ID: " + id);

    }
}
