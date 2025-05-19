package uk.gov.laa.gpfd.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.immutables.value.Value.Immutable;

import jakarta.annotation.Nullable;
import java.util.UUID;

@Immutable
public abstract class ReportOutputType {
    @Nullable
    public abstract UUID getId();

    @NotBlank
    @Size(max = 10)
    public abstract String getExtension();

    @NotBlank
    @Size(max = 255)
    public abstract String getDescription();
}
