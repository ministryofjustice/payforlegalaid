package uk.gov.laa.pfla.scenario;

import io.cucumber.spring.ScenarioScope;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import uk.gov.laa.pfla.util.JsonDeserializer;

import static java.util.Optional.ofNullable;
import static uk.gov.laa.pfla.scenario.AuthenticationState.AUTHENTICATED;
import static uk.gov.laa.pfla.scenario.AuthenticationState.INVALID_CREDENTIALS;

@Data
@Component
@ScenarioScope
public class ScenarioContext {

    @Setter(AccessLevel.NONE)
    private AuthenticationState authenticationState;
    private ResponseEntity<?> response;
    private final JsonDeserializer jsonDeserializer;

    public <T> T deserialize(Class<T> valueType) {
        return jsonDeserializer.deserializeFromResponse(getResponseAs(String.class), valueType);
    }

    public <T> ResponseEntity<T> getResponseAs(Class<T> responseType) {
        return ofNullable(response)
                .filter(res -> responseType.isInstance(res.getBody()))
                .map(res -> (ResponseEntity<T>) res)
                .orElseThrow(() -> new IllegalArgumentException("Response is not of type " + responseType.getSimpleName()));
    }

    public void validAuthenticationCookieProvided() {
        this.authenticationState = AUTHENTICATED;
    }

    public void invalidAuthenticationCookieProvided() {
        this.authenticationState = INVALID_CREDENTIALS;
    }
}