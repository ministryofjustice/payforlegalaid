package uk.gov.laa.gpfd.enums;

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
  XLSX("xlsx", "excel");

  private final String extension;
  private final String subPath;
  private static final Map<String, FileExtension> EXTENSION_MAP =
      Stream.of(values()).collect(Collectors.toMap(FileExtension::getExtension, e -> e));

  public static String getSubPathForExtension(String extension) {
    FileExtension fileExtension = EXTENSION_MAP.get(extension);
    if (fileExtension == null) {
      throw new ReportOutputTypeNotFoundException("Invalid file extension: " + extension);
    }
    return fileExtension.getSubPath();
  }
}