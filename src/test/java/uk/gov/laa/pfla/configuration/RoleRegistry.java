package uk.gov.laa.pfla.configuration;

import java.util.List;

public class RoleRegistry {

    private static List<String> roles = List.of();

    public static synchronized void setRoles(List<String> roleList) {
        roles = roleList;
    }

    public static synchronized List<String> getRoles() {
        return roles;
    }

    public static synchronized void clear() {
        roles = List.of();
    }
}