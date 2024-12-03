package uk.gov.laa.gpfd.data;

import uk.gov.laa.gpfd.bean.UserDetails;

/**
 * A factory class for generating test data for {@link UserDetails} objects.
 * <p>
 * This class provides static methods to generate predefined and valid {@link UserDetails} objects
 * with typical values for use in unit tests. The generated user details can be used in scenarios where
 * mock user data is required, ensuring consistency and reducing redundancy in test code.
 * </p>
 */
public class UserDetailsTestDataFactory {

    /**
     * Generates a valid {@link UserDetails} object with default values.
     * <p>
     * This method creates a {@link UserDetails} object with the following predefined values:
     * <ul>
     *     <li>userPrincipalName: "foo.bar@example.com"</li>
     *     <li>givenName: "Foo"</li>
     *     <li>surname: "Bar"</li>
     *     <li>preferredName: "F. Bar"</li>
     *     <li>email: "foo.bar@example.com"</li>
     * </ul>
     * These values represent a mock user that can be used in unit tests where the exact user details
     * do not need to be customized.
     * </p>
     *
     * @return a {@link UserDetails} object populated with default values.
     */
    public static UserDetails aValidUserDetails() {
        return new UserDetails("foo.bar@example.com", "Foo", "Bar", "F. Bar", "foo.bar@example.com");
    }
}