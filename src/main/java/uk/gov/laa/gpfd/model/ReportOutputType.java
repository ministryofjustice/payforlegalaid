package uk.gov.laa.gpfd.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.immutables.value.Value.Immutable;

import static org.immutables.value.Value.Derived;

@Immutable
public abstract class ReportOutputType implements Identifiable {

    abstract FileExtension getFileExtension();

    @Derived
    @NotBlank
    @Size(max = 10)
    public String getExtension() {
        return getFileExtension().getExtension();
    }

    @Derived
    @NotBlank
    @Size(max = 10)
    public String getSubPath() {
        return getFileExtension().getSubPath();
    }

    @NotBlank
    @Size(max = 255)
    public abstract String getDescription();
}
