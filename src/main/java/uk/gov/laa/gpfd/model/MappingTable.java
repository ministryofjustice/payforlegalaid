package uk.gov.laa.gpfd.model;

import java.util.Optional;

/**
 * A class representing the data in the MOJFIN 'CSV - SQL Mapping' Table. A subset of this data will eventually be
 * returned to the user via the /reports endpoint, in the form of a ReportListResponse
 */
public record MappingTable(
        Optional<Integer> id,
        Optional<String>reportName,
        Optional<String>excelReport,
        Optional<String>csvName,
        Optional<Integer> excelSheetNum,
        Optional<String>sqlQuery,
        Optional<String>baseUrl,
        Optional<String>reportOwner,
        Optional<String>reportCreator,
        Optional<String>description,
        Optional<String>ownerEmail
) {
    public MappingTable(
            Integer id,
            String reportName,
            String excelReport,
            String csvName,
            Integer excelSheetNum,
            String sqlQuery,
            String baseUrl,
            String reportOwner,
            String reportCreator,
            String description,
            String ownerEmail
    ) {
        this(
                Optional.ofNullable(id),
                Optional.ofNullable(reportName),
                Optional.ofNullable(excelReport),
                Optional.ofNullable(csvName),
                Optional.ofNullable(excelSheetNum),
                Optional.ofNullable(sqlQuery),
                Optional.ofNullable(baseUrl),
                Optional.ofNullable(reportOwner),
                Optional.ofNullable(reportCreator),
                Optional.ofNullable(description),
                Optional.ofNullable(ownerEmail)
        );
    }

    @Override
    public String toString() {
        return String.format(
                "MappingTable[id=%s, reportName=%s, excelReport=%s, csvName=%s, excelSheetNum=%s, sqlQuery=%s, baseUrl=%s, reportOwner=%s, reportCreator=%s, description=%s, ownerEmail=%s]",
                id.orElse(null),
                reportName.orElse(null),
                excelReport.orElse(null),
                csvName.orElse(null),
                excelSheetNum.orElse(null),
                sqlQuery.orElse(null),
                baseUrl.orElse(null),
                reportOwner.orElse(null),
                reportCreator.orElse(null),
                description.orElse(null),
                ownerEmail.orElse(null)
        );
    }
}
