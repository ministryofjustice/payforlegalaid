package uk.gov.laa.gpfd.model.excel;

import uk.gov.laa.gpfd.model.Identifiable;

import java.util.UUID;

import static org.immutables.value.Value.Immutable;
import static org.immutables.value.Value.Style;

/**
 * Represents an immutable document template in the system, uniquely identified by a UUID.
 */
@Immutable
@Style(strictBuilder = true)
public abstract class ExcelTemplate implements Identifiable {

    public static ExcelTemplate fromString(String id) {
        return ImmutableExcelTemplate.builder()
                .id(UUID.fromString(id))
                .build();
    }

    @Override
    public String toString() {
        return getIdAsString();
    }
}