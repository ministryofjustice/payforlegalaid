package uk.gov.laa.gpfd.model.excel;

import jakarta.annotation.Nullable;
import org.immutables.value.Value;
import uk.gov.laa.gpfd.model.FieldProjection;
import uk.gov.laa.gpfd.model.Identifiable;

import static org.immutables.value.Value.Immutable;

@Immutable
public abstract class ExcelMappingProjection implements Identifiable, FieldProjection {

    abstract ExcelColumn getExcelColumn();

    @Nullable
    public abstract String getSourceName();

    @Nullable
    @Value.Derived
    public String getMappedName() {
        return getExcelColumn().getName();
    }

    @Nullable
    @Value.Derived
    public String getFormat() {
        return getExcelColumn().getFormat().getFormat();
    }

    @Nullable
    @Value.Derived
    public String getFormatType() {
        return getExcelColumn().getFormat().getFormatType();
    }

    @Value.Derived
    public double getColumnWidth() {
        return getExcelColumn().getFormat().getColumnWidth();
    }
}
