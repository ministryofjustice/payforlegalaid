package uk.gov.laa.gpfd.config;

enum JwtTokenComponents {
    HEADER_TYPE("Authorization"),
    TOKEN_PREFIX("bearer "),
    TENANT_ID_KEY("tid"),
    APPLICATION_ID_KEY("appid"),
    SCOPE_KEY("scp"),
    SCOPE_VALUE("User.Read");


    public final String value;

    JwtTokenComponents(String value) {
        this.value = value;
    }
}
