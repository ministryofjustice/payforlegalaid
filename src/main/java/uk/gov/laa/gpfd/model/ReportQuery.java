package uk.gov.laa.gpfd.model;

import jakarta.annotation.Nullable;
import uk.gov.laa.gpfd.model.excel.ExcelSheet;

import static org.immutables.value.Value.Immutable;


@Immutable
public abstract class ReportQuery implements Mapping, Identifiable {

    @Nullable
    public abstract ReportQuerySql getQuery();

    public abstract ExcelSheet getExcelSheet();

}
