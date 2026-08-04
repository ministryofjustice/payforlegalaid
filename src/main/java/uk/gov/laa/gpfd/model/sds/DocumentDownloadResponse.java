package uk.gov.laa.gpfd.model.sds;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * DocumentDownloadResponse
 */
public class DocumentDownloadResponse implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private @Nullable String fileUrl;

  public DocumentDownloadResponse fileUrl(@Nullable String fileUrl) {
    this.fileUrl = fileUrl;
    return this;
  }

  /**
   * Get fileUrl
   * @return fileUrl
   */
  @Schema(name = "fileUrl", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("fileURL")
  public @Nullable String getFileUrl() {
    return fileUrl;
  }

  @JsonProperty("fileURL")
  public void setFileUrl(@Nullable String fileUrl) {
    this.fileUrl = fileUrl;
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
    return Objects.equals(this.fileUrl, documentDownloadResponse.fileUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fileUrl);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DocumentDownloadResponse {\n");
    sb.append("    fileUrl: ").append(toIndentedString(fileUrl)).append("\n");
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
      return this;
    }

    public DocumentDownloadResponse.Builder fileUrl(String fileUrl) {
      this.instance.fileUrl(fileUrl);
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

