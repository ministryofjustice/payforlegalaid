package uk.gov.laa.gpfd.services.excel.tempalte;

import org.apache.poi.ss.usermodel.Workbook;

import java.io.InputStream;

/**
 * The interface is a functional interface designed to retrieve an Excel template
 * as an {@link InputStream} based on a unique identifier. It provides a single method, {@link #findTemplateById(String)},
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
    InputStream findTemplateById(String id);
}