package uk.gov.laa.gpfd.graph.stubs;

import com.microsoft.graph.models.User;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Component;
import uk.gov.laa.gpfd.graph.GraphClient;

@Component
@Profile("local")
public class StubbedGraphClientHelper implements GraphClient {

    @Override
    public User getGraphUserDetails(OAuth2AuthorizedClient graphAuthorizedClient) {
        return new User() {
            {
                id = "12ff941e-3653-405e-988d-449137e39b55";
                userPrincipalName = "foo-bar@foo.bar.gov.uk";
                givenName = "Foo";
                surname = "Bar";
                preferredName = "testPreferredName";
                mail = "foo-bar@foo.bar.gov.uk";
            }
        };
    }
}
