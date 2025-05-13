package uk.gov.laa.gpfd.services.excel.template;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

import static java.lang.Thread.currentThread;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;
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

    private static final Map<String, InputStream> TEMPLATES = stream(Template.values())
            .collect(toMap(t -> t.id, t -> currentThread().getContextClassLoader().getResourceAsStream(t.resourcePath)));

    @Override
    @SneakyThrows
    public InputStream findTemplateById(String id) {
        return Optional.ofNullable(TEMPLATES.get(id))
                .orElseThrow(() -> new LocalTemplateReadException("Template not found for ID: " + id));
    }

    /**
     * Enumeration of available templates with their metadata.
     * Each template defines:
     * <ul>
     *   <li>A unique identifier (UUID string)</li>
     *   <li>A classpath resource path to the Excel template file</li>
     * </ul>
     */
    @AllArgsConstructor
    private enum Template {
        INVOICE_ANALYSIS("00000000-0000-0000-0000-000000000000", "CCMS_invoice analysis_template_v1_1.xlsx"),
        GENERAL_LEDGER("f46b4d3d-c100-429a-bf9a-223305dbdbfb", "CCMS_GENERAL_LEDGER_EXTRACTOR_SMALL_MANUAL_BATCHES.xlsx");

        private final String id;
        private final String resourcePath;
    }

}