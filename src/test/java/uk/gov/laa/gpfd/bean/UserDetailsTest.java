package uk.gov.laa.gpfd.bean;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserDetailsTest {

    @Test
    void shouldCreateUserDetailsWithValidData() {
        // Given
        var user = new UserDetails(
                "user1@example.com", "John", "Doe", "Johnny", "john.doe@example.com"
        );

        // When
        // Then
        assertNotNull(user);
        assertEquals("user1@example.com", user.userPrincipalName());
        assertEquals("John", user.givenName());
        assertEquals("Doe", user.surname());
        assertEquals("Johnny", user.preferredName());
        assertEquals("john.doe@example.com", user.email());
    }

    @Test
    void shouldCreateUserDetailsWithEmptyStrings() {
        // Given
        var user = new UserDetails("", "", "", "", "");

        // When
        // Then
        assertEquals("", user.userPrincipalName());
        assertEquals("", user.givenName());
        assertEquals("", user.surname());
        assertEquals("", user.preferredName());
        assertEquals("", user.email());
    }

    @Test
    void shouldCreateUserDetailsWithNullFields() {
        // Given
        var user = new UserDetails(
                null, null, null, null, null
        );

        // When
        // Then
        assertNull(user.userPrincipalName());
        assertNull(user.givenName());
        assertNull(user.surname());
        assertNull(user.preferredName());
        assertNull(user.email());
    }

    @Test
    void shouldCreateUserDetailsEquality() {
        // Given
        var user1 = new UserDetails(
                "user1@example.com", "John", "Doe", "Johnny", "john.doe@example.com"
        );
        var user2 = new UserDetails(
                "user1@example.com", "John", "Doe", "Johnny", "john.doe@example.com"
        );

        // When
        // Then
        assertEquals(user1, user2);
    }

    @Test
    void shouldCreateUserDetailsInequality() {
        // Given
        var user1 = new UserDetails(
                "user1@example.com", "John", "Doe", "Johnny", "john.doe@example.com"
        );
        var user2 = new UserDetails(
                "user2@example.com", "Jane", "Smith", "Janey", "jane.smith@example.com"
        );

        // When
        // Then
        assertNotEquals(user1, user2);
    }

    @Test
    void shouldCreateUserDetailsHashCode() {
        // Given
        var user1 = new UserDetails(
                "user1@example.com", "John", "Doe", "Johnny", "john.doe@example.com"
        );
        var user2 = new UserDetails(
                "user1@example.com", "John", "Doe", "Johnny", "john.doe@example.com"
        );

        // When
        // Then
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void shouldCreateUserDetailsToString() {
        // Given
        var expected = "UserDetails[userPrincipalName=user1@example.com, givenName=John, " +
                "surname=Doe, preferredName=Johnny, email=john.doe@example.com]";
        var user = new UserDetails(
                "user1@example.com", "John", "Doe", "Johnny", "john.doe@example.com"
        );

        // When
        // Then
        assertEquals(expected, user.toString());
    }

    @Test
    void shouldCreateUserDetailsWithLongUserPrincipalName() {
        // Given
        var longUserPrincipalName = "a".repeat(1000) + "@example.com"; // Generate a long userPrincipalName
        var user = new UserDetails(
                longUserPrincipalName, "John", "Doe", "Johnny", "john.doe@example.com"
        );

        // When
        // Then
        assertEquals(longUserPrincipalName, user.userPrincipalName());
    }

    @Test
    void shouldCreateUserDetailsWithLongGivenName() {
        // Given
        var longGivenName = "a".repeat(500); // Generate a long given name
        var user = new UserDetails(
                "user1@example.com", longGivenName, "Doe", "Johnny", "john.doe@example.com"
        );

        // When
        // Then
        assertEquals(longGivenName, user.givenName());
    }

    @Test
    void shouldCreateUserDetailsWithLongSurname() {
        // Given
        var longSurname = "a".repeat(500); // Generate a long surname
        var user = new UserDetails(
                "user1@example.com", "John", longSurname, "Johnny", "john.doe@example.com"
        );

        // When
        // Then
        assertEquals(longSurname, user.surname());
    }

    @Test
    void shouldCreateUserDetailsWithNullPreferredName() {
        // Given
        var user = new UserDetails(
                "user1@example.com", "John", "Doe", null, "john.doe@example.com"
        );

        // When
        // Then
        assertNull(user.preferredName());
    }

    @Test
    void shouldCreateUserDetailsWithEmptyPreferredName() {
        // Given
        var user = new UserDetails(
                "user1@example.com", "John", "Doe", "", "john.doe@example.com"
        );

        // When
        // Then
        assertEquals("", user.preferredName());
    }

    @Test
    void shouldCreateUserDetailsWithInvalidEmailFormat() {
        // Given
        var user = new UserDetails(
                "user1@example", "John", "Doe", "Johnny", "invalid-email"
        );

        // When
        // Then
        assertEquals("user1@example", user.userPrincipalName());
        assertEquals("invalid-email", user.email());
    }

    @Test
    void shouldCreateUserDetailsWithNullEmail() {
        // Given
        var user = new UserDetails(
                "user1@example.com", "John", "Doe", "Johnny", null
        );

        // When
        // Then
        assertNull(user.email());
    }

    @Test
    void shouldCreateUserDetailsWithWhitespaceInFields() {
        // Given
        var user = new UserDetails(
                " user1@example.com ", " John ", " Doe ", " Johnny ", " john.doe@example.com "
        );

        // When
        // Then
        assertEquals(" user1@example.com ", user.userPrincipalName());
        assertEquals(" John ", user.givenName());
        assertEquals(" Doe ", user.surname());
        assertEquals(" Johnny ", user.preferredName());
        assertEquals(" john.doe@example.com ", user.email());
    }

    @Test
    void shouldCreateUserDetailsWithSpecialCharactersInGivenName() {
        // Given
        var specialCharsGivenName = "John@Doe#";
        var user = new UserDetails(
                "user1@example.com", specialCharsGivenName, "Doe", "Johnny", "john.doe@example.com"
        );

        // When
        // Then
        assertEquals(specialCharsGivenName, user.givenName());
    }

    @Test
    void shouldCreateUserDetailsWithSpecialCharactersInSurname() {
        // Given
        var specialCharsSurname = "Doe@Smith#";
        var user = new UserDetails(
                "user1@example.com", "John", specialCharsSurname, "Johnny", "john.doe@example.com"
        );

        // When
        // Then
        assertEquals(specialCharsSurname, user.surname());
    }

    @Test
    void shouldCreateUserDetailsWithSpecialCharactersInPreferredName() {
        // Given
        var specialCharsPreferredName = "Johnny@Doe#";
        var user = new UserDetails(
                "user1@example.com", "John", "Doe", specialCharsPreferredName, "john.doe@example.com"
        );

        // When
        // Then
        assertEquals(specialCharsPreferredName, user.preferredName());
    }

    @Test
    void shouldCreateUserDetailsWithSpecialCharactersInEmail() {
        // Given
        var specialCharsEmail = "john.doe+test@example.com";
        var user = new UserDetails(
                "user1@example.com", "John", "Doe", "Johnny", specialCharsEmail
        );

        // When
        // Then
        assertEquals(specialCharsEmail, user.email());
    }

    @Test
    void shouldCreateUserDetailsWithInvalidUserPrincipalName() {
        // Given
        var user = new UserDetails(
                "invalid-email-format", "John", "Doe", "Johnny", "john.doe@example.com"
        );

        // When
        // Then
        assertEquals("invalid-email-format", user.userPrincipalName());
    }
}
