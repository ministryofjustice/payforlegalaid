package uk.gov.laa.gpfd.model.excel;

import jakarta.annotation.Nullable;

import static org.immutables.value.Value.Immutable;

@Immutable
public abstract class ExcelColumn {

    @Nullable
    public abstract String getName();

    @Nullable
    public abstract ColumnFormat getFormat();

    @Immutable
    public abstract static class ColumnFormat {

        @Nullable
        public abstract String getFormat();

        @Nullable
        public abstract String getFormatType();

        public abstract double getColumnWidth();

    }
}
