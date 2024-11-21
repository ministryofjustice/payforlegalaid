package uk.gov.laa.gpfd.beans;

import lombok.Data;

/**
 * A bean to hold the basic details of a User as retrieved
 * from an external User Directory.
 */
@Data
public class UserDetails {

    /**
     * The User Principal Name
     */
    private String userPrincipalName;

    /**
     * The given name for a User
     */
    private String givenName;

    /**
     * The User's surname
     */
    private String surname;

    /**
     * The preferred name for the User
     */
    private String preferredName;

    /**
     * The User's email address
     */
    private String email;


    @Override
    public String toString() {
        return "UserDetails{" +
                "userPrincipalName='" + userPrincipalName + '\'' +
                ", givenName='" + givenName + '\'' +
                ", surname='" + surname + '\'' +
                ", preferredName='" + preferredName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
