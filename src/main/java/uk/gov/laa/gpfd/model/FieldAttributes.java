package uk.gov.laa.gpfd.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FieldAttributes {
    private UUID id;
    private UUID reportQueryId;
    private String sourceName;
    private String mappedName;
    private String format;
    private String formatType;
    private double columnWidth;
}
