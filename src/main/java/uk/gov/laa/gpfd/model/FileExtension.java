package uk.gov.laa.gpfd.model;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.gov.laa.gpfd.exception.ReportOutputTypeNotFoundException;

@RequiredArgsConstructor
@Getter
public enum FileExtension {
    CSV("csv", "csv"),
    XLSX("xlsx", "excel"),
    S3STORAGE("s3storage", "s3storage");

    private static final Map<String, FileExtension> EXTENSION_MAP =
            Stream.of(values()).collect(Collectors.toMap(FileExtension::getExtension, e -> e));
    private static final Map<String, FileExtension> SUBPATH_MAP =
            Stream.of(values()).collect(Collectors.toMap(FileExtension::getSubPath, e -> e));
    private final String extension;
    private final String subPath;

    public static FileExtension fromString(String value) {
        var result = EXTENSION_MAP.get(value);
        if (result == null) {
            result = SUBPATH_MAP.get(value);
        }
        if (result == null) {
            throw new ReportOutputTypeNotFoundException("Unsupported file type: " + value);
        }
        return result;
    }

}