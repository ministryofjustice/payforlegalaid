package uk.gov.laa.gpfd.model.sds;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.Objects;

/**
 * DocumentUploadResponse
 */
public class DocumentUploadResponse implements Serializable {

  private static final long serialVersionUID = 1L;

  private @Nullable String detail;

  private @Nullable String success;

  private @Nullable String checksum;

  public DocumentUploadResponse detail(@Nullable String detail) {
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

  public DocumentUploadResponse success(@Nullable String success) {
    this.success = success;
    return this;
  }

  /**
   * Get success
   * @return success
   */
  @Schema(name = "success", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("success")
  public @Nullable String getSuccess() {
    return success;
  }

  @JsonProperty("success")
  public void setSuccess(@Nullable String success) {
    this.success = success;
  }

  public DocumentUploadResponse checksum(@Nullable String checksum) {
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
    DocumentUploadResponse documentUploadResponse = (DocumentUploadResponse) o;
    return Objects.equals(this.detail, documentUploadResponse.detail) &&
        Objects.equals(this.success, documentUploadResponse.success) &&
        Objects.equals(this.checksum, documentUploadResponse.checksum);
  }

  @Override
  public int hashCode() {
    return Objects.hash(detail, success, checksum);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DocumentUploadResponse {\n");
    sb.append("    detail: ").append(toIndentedString(detail)).append("\n");
    sb.append("    success: ").append(toIndentedString(success)).append("\n");
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

    private DocumentUploadResponse instance;

    public Builder() {
      this(new DocumentUploadResponse());
    }

    protected Builder(DocumentUploadResponse instance) {
      this.instance = instance;
    }

    protected Builder copyOf(DocumentUploadResponse value) {
      this.instance.setDetail(value.detail);
      this.instance.setSuccess(value.success);
      this.instance.setChecksum(value.checksum);
      return this;
    }

    public DocumentUploadResponse.Builder detail(String detail) {
      this.instance.detail(detail);
      return this;
    }

    public DocumentUploadResponse.Builder success(String success) {
      this.instance.success(success);
      return this;
    }

    public DocumentUploadResponse.Builder checksum(String checksum) {
      this.instance.checksum(checksum);
      return this;
    }

    /**
     * returns a built DocumentUploadResponse instance.
     *
     * The builder is not reusable (NullPointerException)
     */
    public DocumentUploadResponse build() {
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
  public static DocumentUploadResponse.Builder builder() {
    return new DocumentUploadResponse.Builder();
  }

  /**
   * Create a builder with a shallow copy of this instance.
   */
  public DocumentUploadResponse.Builder toBuilder() {
    DocumentUploadResponse.Builder builder = new DocumentUploadResponse.Builder();
    return builder.copyOf(this);
  }
}

