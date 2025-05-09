package uk.gov.laa.gpfd.services.excel.template;

import lombok.SneakyThrows;

import java.io.InputStream;

import static uk.gov.laa.gpfd.exception.TemplateResourceException.LocalTemplateReadException;

public record LocalTemplateClient() implements TemplateClient {

    //TODO need to do stuff in here

    @Override
    @SneakyThrows
    public InputStream findTemplateById(String id) {
        var resourceAsStream = getClass().getClassLoader().getResourceAsStream("CCMS_invoice analysis_template_v1_1.xlsx");

        if (resourceAsStream == null) {
            throw new LocalTemplateReadException("Template not found in resources for ID: " + id);
        }

        return resourceAsStream;
    }

}