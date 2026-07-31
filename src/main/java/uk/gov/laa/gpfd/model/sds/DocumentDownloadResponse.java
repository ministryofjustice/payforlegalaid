package uk.gov.laa.gpfd.model.sds;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.Objects;

/**
 * DocumentDownloadResponse
 */
public class DocumentDownloadResponse implements Serializable {

  private static final long serialVersionUID = 1L;

  private @Nullable String fileUrl;

  private @Nullable String detail;

  private @Nullable String checksum;

  public DocumentDownloadResponse fileUrl(@Nullable String fileUrl) {
    this.fileUrl = fileUrl;
    return this;
  }

  /**
   * Get fileUrl
   * @return fileUrl
   */
  @Schema(name = "fileUrl", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("fileUrl")
  public @Nullable String getFileUrl() {
    return fileUrl;
  }

  @JsonProperty("fileUrl")
  public void setFileUrl(@Nullable String fileUrl) {
    this.fileUrl = fileUrl;
  }

  public DocumentDownloadResponse detail(@Nullable String detail) {
    this.detail = detail;
    return this;
  }

  /**
   * Get detail
   * @return detail
   */
  @Schema(name = "detail", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("detail")
  public @Nullable String getDetail() {
    return detail;
  }

  @JsonProperty("detail")
  public void setDetail(@Nullable String detail) {
    this.detail = detail;
  }

  public DocumentDownloadResponse checksum(@Nullable String checksum) {
    this.checksum = checksum;
    return this;
  }

  /**
   * Get checksum
   * @return checksum
   */
  @Schema(name = "checksum", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("checksum")
  public @Nullable String getChecksum() {
    return checksum;
  }

  @JsonProperty("checksum")
  public void setChecksum(@Nullable String checksum) {
    this.checksum = checksum;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DocumentDownloadResponse documentDownloadResponse = (DocumentDownloadResponse) o;
    return Objects.equals(this.fileUrl, documentDownloadResponse.fileUrl) &&
        Objects.equals(this.detail, documentDownloadResponse.detail) &&
        Objects.equals(this.checksum, documentDownloadResponse.checksum);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fileUrl, detail, checksum);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DocumentDownloadResponse {\n");
    sb.append("    fileUrl: ").append(toIndentedString(fileUrl)).append("\n");
    sb.append("    detail: ").append(toIndentedString(detail)).append("\n");
    sb.append("    checksum: ").append(toIndentedString(checksum)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(@Nullable Object o) {
    return o == null ? "null" : o.toString().replace("\n", "\n    ");
  }

  public static class Builder {

    private DocumentDownloadResponse instance;

    public Builder() {
      this(new DocumentDownloadResponse());
    }

    protected Builder(DocumentDownloadResponse instance) {
      this.instance = instance;
    }

    protected Builder copyOf(DocumentDownloadResponse value) {
      this.instance.setFileUrl(value.fileUrl);
      this.instance.setDetail(value.detail);
      this.instance.setChecksum(value.checksum);
      return this;
    }

    public DocumentDownloadResponse.Builder fileUrl(String fileUrl) {
      this.instance.fileUrl(fileUrl);
      return this;
    }

    public DocumentDownloadResponse.Builder detail(String detail) {
      this.instance.detail(detail);
      return this;
    }

    public DocumentDownloadResponse.Builder checksum(String checksum) {
      this.instance.checksum(checksum);
      return this;
    }

    /**
     * returns a built DocumentDownloadResponse instance.
     *
     * The builder is not reusable (NullPointerException)
     */
    public DocumentDownloadResponse build() {
      try {
        return this.instance;
      } finally {
        // ensure that this.instance is not reused
        this.instance = null;
      }
    }

    @Override
    public String toString() {
      return getClass() + "=(" + instance + ")";
    }
  }

  /**
   * Create a builder with no initialized field (except for the default values).
   */
  public static DocumentDownloadResponse.Builder builder() {
    return new DocumentDownloadResponse.Builder();
  }

  /**
   * Create a builder with a shallow copy of this instance.
   */
  public DocumentDownloadResponse.Builder toBuilder() {
    DocumentDownloadResponse.Builder builder = new DocumentDownloadResponse.Builder();
    return builder.copyOf(this);
  }
}

