package uk.gov.laa.gpfd.data;

import com.microsoft.graph.models.User;

import java.util.Arrays;

/**
 * A factory class for creating test data instances of {@link com.microsoft.graph.models.User}.
 * <p>
 * This class provides methods to create valid and customized {@link User} objects for testing purposes.
 * </p>
 */
public class AzureGraphUserTestDataFactory {

    /**
     * Creates a valid {@link User} instance populated with realistic default data.
     *
     * @return a fully populated {@link User} object with default test data.
     */
    public static User aValidUser() {
        return new User() {{
            id = "user123";
            displayName = "Foo Bar";
            userPrincipalName = "foo.bar@example.com";
            givenName = "Foo";
            surname = "Bar";
            mail = "foo.bar@example.com";
            city = "London";
            state = "Active";
            country = "UK";
            employeeId = "E12345";
            companyName = "FooBar";
            department = "Engineering";
            businessPhones = Arrays.asList("123-456-7890", "987-654-3210");
            mobilePhone = "555-123-4567";
            officeLocation = "Office 101";
            onPremisesSamAccountName = "foo_bar";
            onPremisesSecurityIdentifier = "S-1-5-21-1234567890";
            accountEnabled = true;
            mailNickname = "foo.bar";
            jobTitle = "Software Engineer";
            usageLocation = "GB";
            otherMails = Arrays.asList("alt.email1@example.com", "alt.email2@example.com");
            passwordPolicies = "None";
            passwordProfile = null;
            preferredLanguage = "en-GB";
            preferredDataLocation = "GB";
            onPremisesSyncEnabled = true;
        }};
    }

    /**
     * Creates a valid {@link User} instance with customizable principal name and display name.
     *
     * @param userPrincipalName the principal name of the user (e.g., email address or unique identifier).
     * @param displayName       the display name of the user.
     * @return a {@link User} object populated with the specified user principal name and display name.
     */
    public static User aValidUserWithCustomData(String userPrincipalName, String displayName) {
        return new User() {{
            userPrincipalName = userPrincipalName;
            displayName = displayName;
            givenName = "Foo";
            surname = "bar";
            mail = "foo.bar@example.com";
            jobTitle = "Software Engineer";
            city = "London";
            state = "Active";
            country = "UK";
            accountEnabled = true;
        }};
    }

    /**
     * Creates a minimal {@link User} instance with only basic fields populated.
     *
     * @return a {@link User} object populated with minimal data for testing.
     */
    public static User aValidUserSmallUser() {
        return new User() {{
            userPrincipalName = "testPrincipalName";
            givenName = "testGivenName";
            surname = "testSurname";
            preferredName = "testPreferredName";
            mail = "testMail";
        }};
    }
}
