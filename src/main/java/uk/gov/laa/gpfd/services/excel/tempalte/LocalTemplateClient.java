package uk.gov.laa.gpfd.services.excel.tempalte;

import lombok.SneakyThrows;

import java.io.InputStream;

public record LocalTemplateClient() implements TemplateClient {

    @Override
    @SneakyThrows
    public InputStream findTemplateById(String id) {
        var resourceAsStream = getClass().getClassLoader().getResourceAsStream("CCMS_invoice analysis_template_v1_1.xlsx");

        if (resourceAsStream == null) {
            throw new RuntimeException("Template not found in resources for ID: " + id);
        }

        return resourceAsStream;
    }

}