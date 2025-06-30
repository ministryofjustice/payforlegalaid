package uk.gov.laa.gpfd.model.excel;

import jakarta.annotation.Nullable;
import org.immutables.value.Value;

import java.util.Collection;

@Value.Immutable
public abstract class ExcelSheet {

    @Nullable
    public abstract String getName();

    @Nullable
    public abstract Collection<ExcelMappingProjection> getFieldAttributes();
}
