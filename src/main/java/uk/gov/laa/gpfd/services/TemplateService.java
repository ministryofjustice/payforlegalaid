package uk.gov.laa.gpfd.services;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import uk.gov.laa.gpfd.services.excel.tempalte.TemplateClient;

import java.io.InputStream;

/**
 * The interface is a functional interface designed to retrieve and load Excel templates
 * as Apache POI {@link Workbook} objects. It provides methods to locate a template by its unique identifier
 * and load it into a {@link Workbook} for further processing.
 */
@FunctionalInterface
public interface TemplateService {

    /**
     * Retrieves an Excel template as a {@link Workbook} based on the provided unique identifier.
     * This method is responsible for locating and loading the template into a format suitable for
     * further processing.
     *
     * @param id the unique identifier of the template to retrieve
     * @return the {@link Workbook} representing the template
     */
    Workbook findTemplateById(String id);

    /**
     * Retrieves an Excel template as a {@link Workbook} using the provided {@link TemplateClient} and unique identifier.
     * This default method uses the {@link TemplateClient} to fetch the template as an {@link InputStream}, then loads it
     * into a {@link Workbook} using Apache POI's {@link XSSFWorkbook}. If an error occurs during loading, a
     * {@link RuntimeException} is thrown with a descriptive message.
     *
     * @param templateClient the {@link TemplateClient} used to retrieve the template
     * @param id            the unique identifier of the template to retrieve
     * @return the {@link Workbook} representing the template
     * @throws RuntimeException if the template cannot be loaded
     */
    default Workbook findTemplateById(TemplateClient templateClient, String id) {
        try (var inputStream = templateClient.findTemplateById(id)) {
            return new XSSFWorkbook(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load template for ID: " + id, e);
        }
    }
}