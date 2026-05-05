package uk.gov.laa.gpfd.model;

import jakarta.annotation.Nullable;

import static org.immutables.value.Value.Immutable;


@Immutable
public abstract class ReportQuery implements Mapping, Identifiable {

    @Nullable
    @Override
    public abstract ReportQuerySql getQuery();

}
