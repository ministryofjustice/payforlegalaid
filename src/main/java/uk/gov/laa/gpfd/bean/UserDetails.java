package uk.gov.laa.gpfd.bean;

import lombok.Builder;

/**
 * A bean to hold the basic details of a User as retrieved
 * from an external User Directory.
 */
@Builder
public record UserDetails(
        String userPrincipalName,
        String givenName,
        String surname,
        String preferredName,
        String email
) {}