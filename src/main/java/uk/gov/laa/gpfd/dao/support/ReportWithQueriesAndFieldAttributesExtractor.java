package uk.gov.laa.gpfd.dao.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import uk.gov.laa.gpfd.model.FieldAttributes;
import uk.gov.laa.gpfd.model.ImmutableFieldAttributes;
import uk.gov.laa.gpfd.model.ImmutableReport;
import uk.gov.laa.gpfd.model.ImmutableReportOutputType;
import uk.gov.laa.gpfd.model.ImmutableReportQuery;
import uk.gov.laa.gpfd.model.Report;
import uk.gov.laa.gpfd.model.ReportQuery;
import uk.gov.laa.gpfd.model.ReportQuerySql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import static uk.gov.laa.gpfd.exception.DatabaseReadException.MappingException;

/**
 * A {@link ResultSetExtractor} implementation that extracts a collection of {@link Report} objects
 * from a {@link ResultSet}. Each {@link Report} contains a list of {@link ReportQuery} objects,
 * and each {@link ReportQuery} contains a list of {@link FieldAttributes}.
 *
 * <p>This class is responsible for mapping database rows to the corresponding Java objects,
 * handling relationships between reports, queries, and field attributes.
 */
@Slf4j
@Component
public class ReportWithQueriesAndFieldAttributesExtractor implements ResultSetExtractor<Collection<Report>> {

    /**
     * Extracts data from the provided {@link ResultSet} and maps it to a collection of {@link Report} objects.
     * Each report contains a list of {@link ReportQuery} objects, and each query contains a list of {@link FieldAttributes}.
     *
     * @param rs the {@link ResultSet} containing the data to be extracted
     * @return a collection of {@link Report} objects
     * @throws SQLException if a database access error occurs
     * @throws RuntimeException if an error occurs during the mapping process
     */
    @Override
    public Collection<Report> extractData(ResultSet rs) throws SQLException {
        log.debug("Starting extraction of reports from ResultSet");
        var reportMap = new HashMap<UUID, Report>();
        var queryMap = new HashMap<UUID, ReportQuery>();

        while (rs.next()) {
            var reportId = UUID.fromString(rs.getString("ID"));
            log.debug("Processing report with ID: {}", reportId);
            var report = reportMap.computeIfAbsent(reportId, id -> {
                try {
                    log.debug("Mapping data for report with ID: {}", id);
                    var description = rs.getString("DESCRIPTION");
                    return ImmutableReport.builder()
                            .id(id)
                            .name(rs.getString("NAME"))
                            .templateSecureDocumentId(rs.getString("TEMPLATE_SECURE_DOCUMENT_ID"))
                            .reportCreationTime(rs.getTimestamp("REPORT_CREATION_DATE"))
                            .lastDatabaseRefreshDate(rs.getTimestamp("LAST_DATABASE_REFRESH_DATETIME"))
                            .description(description)
                            .numDaysToKeep(rs.getInt("NUM_DAYS_TO_KEEP"))
                            .reportOwnerId(UUID.fromString(rs.getString("REPORT_OWNER_ID")))
                            .reportOwnerName(rs.getString("REPORT_OWNER_NAME"))
                            .active(rs.getString("ACTIVE").equals("Y"))
                            .reportOwnerEmail(rs.getString("REPORT_OWNER_EMAIL"))
                            .fileName(rs.getString("FILE_NAME"))
                            .reportOutputType(ImmutableReportOutputType.builder()
                                    .extension(rs.getString("EXTENSION"))
                                    .description(description)
                                    .build())
                            .queries(new ArrayList<>())
                            .build();
                } catch (SQLException e) {
                    log.error("Error mapping Report data for ID: {}", id, e);
                    throw new MappingException("Error mapping Report data");
                }
            });

            var queryId = rs.getString("QUERY_ID");
            if (queryId == null) {
                log.debug("No query found for report with ID: {}", reportId);
                continue;
            }

            var queryUUID = UUID.fromString(queryId);
            log.debug("Processing query with ID: {} for report with ID: {}", queryUUID, reportId);
            var query = queryMap.computeIfAbsent(queryUUID, id -> {
                try {
                    log.debug("Mapping data for query with ID: {}", id);
                    return ImmutableReportQuery.builder()
                            .id(queryUUID)
                            .reportId(reportId)
                            .query(ReportQuerySql.of(rs.getString("QUERY")))
                            .sheetName(rs.getString("TAB_NAME"))
                            .fieldAttributes(new ArrayList<>())
                            .build();
                } catch (SQLException e) {
                    log.error("Error mapping ReportQuery data for ID: {}", id, e);
                    throw new MappingException("Error mapping ReportQuery data");
                }
            });

            var fieldAttributeId = rs.getString("FIELD_ATTRIBUTE_ID");
            if (fieldAttributeId != null) {
                log.debug("Processing field attribute with ID: {} for query with ID: {}", fieldAttributeId, queryUUID);
                var fieldAttribute = ImmutableFieldAttributes.builder()
                        .id(UUID.fromString(fieldAttributeId))
                        .reportQueryId(queryUUID)
                        .sourceName(rs.getString("SOURCE_NAME"))
                        .mappedName(rs.getString("MAPPED_NAME"))
                        .format(rs.getString("FORMAT"))
                        .formatType(rs.getString("FORMAT_TYPE"))
                        .columnWidth(rs.getDouble("COLUMN_WIDTH"))
                        .build();
                query.getFieldAttributes().add(fieldAttribute);
            }

            if (!report.getQueries().contains(query)) {
                log.debug("Adding query with ID: {} to report with ID: {}", queryUUID, reportId);
                report.getQueries().add(query);
            }
        }

        log.debug("Completed extraction of reports. Total reports extracted: {}", reportMap.size());
        return reportMap.values();
    }
}
