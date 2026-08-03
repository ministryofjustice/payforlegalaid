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

  private @Nullable String health;

  @Schema(name = "Health", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("Health")
  public @Nullable String getHealth() {
    return health;
  }

  @JsonProperty("Health")
  public void setHealth(@Nullable String health) {
    this.health = health;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SdsHealthResponse that = (SdsHealthResponse) o;
    return Objects.equals(this.health, that.health);
  }

  @Override
  public int hashCode() {
    return Objects.hash(health);
  }

  @Override
  public String toString() {
    return "class SdsHealthResponse {\n    health: " + health + "\n}";
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
      this.instance.setHealth(value.health);
      return this;
    }

    public SdsHealthResponse.Builder health(String health) {
      this.instance.setHealth(health);
      return this;
    }

    public SdsHealthResponse build() {
      try {
        return this.instance;
      } finally {
        this.instance = null;
      }
    }

    @Override
    public String toString() {
      return getClass() + "=(" + instance + ")";
    }
  }

  public static SdsHealthResponse.Builder builder() {
    return new SdsHealthResponse.Builder();
  }

  public SdsHealthResponse.Builder toBuilder() {
    return new SdsHealthResponse.Builder().copyOf(this);
  }
}

