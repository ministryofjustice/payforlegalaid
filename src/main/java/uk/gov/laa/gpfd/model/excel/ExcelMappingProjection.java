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
    @Override
    public abstract String getSourceName();

    @Nullable
    @Value.Derived
    @Override
    public String getMappedName() {
        return getExcelColumn().getName();
    }

    @Nullable
    @Value.Derived
    public String getFormat() {
        var format = getExcelColumn().getFormat();
        if (format == null) return null;
        return format.getFormat();
    }

    @Nullable
    @Value.Derived
    public String getFormatType() {
        var format = getExcelColumn().getFormat();
        if (format == null) return null;
        return format.getFormatType();
    }

    @Value.Derived
    public double getColumnWidth() {
        var format = getExcelColumn().getFormat();
        if (format == null) return 0;
        return format.getColumnWidth();
    }
}
