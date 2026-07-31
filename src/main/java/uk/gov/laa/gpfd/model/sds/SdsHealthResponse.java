package uk.gov.laa.gpfd.model.sds;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.Objects;

/**
 * SdsHealthResponse
 */
public class SdsHealthResponse implements Serializable {

  private static final long serialVersionUID = 1L;

  private @Nullable String status;

  private @Nullable String detail;

  public SdsHealthResponse status(@Nullable String status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  @Schema(name = "status", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public @Nullable String getStatus() {
    return status;
  }

  @JsonProperty("status")
  public void setStatus(@Nullable String status) {
    this.status = status;
  }

  public SdsHealthResponse detail(@Nullable String detail) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SdsHealthResponse sdsHealthResponse = (SdsHealthResponse) o;
    return Objects.equals(this.status, sdsHealthResponse.status) &&
        Objects.equals(this.detail, sdsHealthResponse.detail);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, detail);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SdsHealthResponse {\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    detail: ").append(toIndentedString(detail)).append("\n");
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

    private SdsHealthResponse instance;

    public Builder() {
      this(new SdsHealthResponse());
    }

    protected Builder(SdsHealthResponse instance) {
      this.instance = instance;
    }

    protected Builder copyOf(SdsHealthResponse value) {
      this.instance.setStatus(value.status);
      this.instance.setDetail(value.detail);
      return this;
    }

    public SdsHealthResponse.Builder status(String status) {
      this.instance.status(status);
      return this;
    }

    public SdsHealthResponse.Builder detail(String detail) {
      this.instance.detail(detail);
      return this;
    }

    /**
     * returns a built SdsHealthResponse instance.
     *
     * The builder is not reusable (NullPointerException)
     */
    public SdsHealthResponse build() {
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
  public static SdsHealthResponse.Builder builder() {
    return new SdsHealthResponse.Builder();
  }

  /**
   * Create a builder with a shallow copy of this instance.
   */
  public SdsHealthResponse.Builder toBuilder() {
    SdsHealthResponse.Builder builder = new SdsHealthResponse.Builder();
    return builder.copyOf(this);
  }
}

