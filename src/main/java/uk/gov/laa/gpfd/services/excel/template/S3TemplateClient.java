package uk.gov.laa.gpfd.services.excel.template;

import uk.gov.laa.gpfd.exception.TemplateResourceException;

import java.io.InputStream;
import java.util.UUID;

public record S3TemplateClient() implements TemplateClient {

    @Override
    public InputStream findTemplateById(UUID id) {

        var filename = getFileNameFromId(id);

        if (filename == null) {
            return null;
        }

        // todo - use s3 client

        return null;
    }

}
