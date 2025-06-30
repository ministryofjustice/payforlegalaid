package uk.gov.laa.gpfd.model;

import org.immutables.value.Value.Immutable;
import uk.gov.laa.gpfd.model.excel.ExcelSheet;


@Immutable
public abstract class ReportQuery implements Mapping, Identifiable {

    public abstract ReportQuerySql getQuery();

    public abstract ExcelSheet getExcelSheet();

}
