package uk.gov.laa.pfla.auth.service.beans;

/**
 * A bean to hold the basic details of a User as retrieved
 * from an external User Directory.
 */
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

  public String getUserPrincipalName() {
    return userPrincipalName;
  }

  public void setUserPrincipalName(String userPrincipalName) {
    this.userPrincipalName = userPrincipalName;
  }

  public String getGivenName() {
    return givenName;
  }

  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }

  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public String getPreferredName() {
    return preferredName;
  }

  public void setPreferredName(String preferredName) {
    this.preferredName = preferredName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

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
