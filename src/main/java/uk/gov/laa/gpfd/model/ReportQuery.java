package uk.gov.laa.gpfd.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportQuery {
    private UUID id;
    private UUID reportId;
    private String query;
    private String tabName;
    private Collection<FieldAttributes> fieldAttributes;
}
