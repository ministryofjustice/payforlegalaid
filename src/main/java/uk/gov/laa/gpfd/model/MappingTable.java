package uk.gov.laa.gpfd.model;

public record MappingTable(
        int id,
        String reportName,
        String excelReport,
        String csvName,
        int excelSheetNum,
        String sqlQuery,
        String baseUrl,
        String reportOwner,
        String reportCreator,
        String description,
        String ownerEmail
) {}
