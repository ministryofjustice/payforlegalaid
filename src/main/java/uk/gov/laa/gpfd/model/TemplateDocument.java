package uk.gov.laa.gpfd.model;

import java.util.UUID;

import static org.immutables.value.Value.Immutable;
import static org.immutables.value.Value.Style;

/**
 * Represents an immutable document template in the system, uniquely identified by a UUID.
 */
@Immutable
@Style(strictBuilder = true)
public abstract class TemplateDocument implements Identifiable {

    public static TemplateDocument fromString(String id) {
        return ImmutableTemplateDocument.builder()
                .id(UUID.fromString(id))
                .build();
    }

    @Override
    public String toString() {
        return getIdAsString();
    }
}