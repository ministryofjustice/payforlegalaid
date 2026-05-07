package uk.gov.laa.gpfd.exception;

import lombok.Getter;

/**
 * Abstract exception class to indicate that the necessary tokens cannot be obtained from the authentication Spring has returned
 */
@Getter
public abstract sealed class UnableToParseAuthDetailsException extends RuntimeException {

    /**
     * Constructs a new {@code UnexpectedAuthTypeException} with the specified error message.
     *
     * @param message the detail message describing the exception.
     */
    protected UnableToParseAuthDetailsException(String message) {
        super(message);
    }


    /**
     * Exception thrown when we can't get an authentication context from Spring
     */
    public static final class AuthenticationIsNullException extends UnableToParseAuthDetailsException {

        /**
         * Constructs a new {@code AuthenticationIsNullException} with the specified error message.
         */
        public AuthenticationIsNullException() {
            super("Authentication object is null");
        }
    }

    /**
     * Exception thrown when we can't get a principal from the authentication context in Spring
     */
    public static final class PrincipalIsNullException extends UnableToParseAuthDetailsException {

        /**
         * Constructs a new {@code PrincipalIsNullException} with the specified error message.
         */
        public PrincipalIsNullException() {
            super("Principal object is null");
        }
    }

    /**
     * Exception throw when we get an authentication class that we haven't setup to parse a group from / doesn't support groups.
     */
    public static final class UnexpectedAuthClassException extends UnableToParseAuthDetailsException {

        /**
         * Constructs a new {@code UnexpectedAuthClassException} with the specified error message.
         */
        public UnexpectedAuthClassException(String authClass) {
            super("Unexpected auth type returned by Spring, could not extract groups. Got an instance of class " + authClass);
        }
    }

    /**
     * Exception throw when we can't get an oid out of the auth token sent via SiLAS.
     */
    public static final class NoOidSetOnTokenException extends UnableToParseAuthDetailsException {

        /**
         * Constructs a new {@code NoOidSetOnTokenException}.
         */
        public NoOidSetOnTokenException() {
            super("No oid token supplied");
        }
    }

    /**
     * Exception throw when we can't get attributes out of the auth token sent via SiLAS.
     */
    public static final class NoAttributesOnTokenException extends UnableToParseAuthDetailsException {

        /**
         * Constructs a new {@code NoAttributesOnTokenException}.
         */
        public NoAttributesOnTokenException() {
            super("Could not parse attributes from token");
        }
    }

    /**
     * Exception throw when user has no roles returned in the token.
     * This isn't really a valid setup in SiLAS so it suggests SiLAS isn't sending us the roles.
     */
    public static final class NoRolesInAttributeException extends UnableToParseAuthDetailsException {

        /**
         * Constructs a new {@code NoRolesException}.
         */
        public NoRolesInAttributeException() {
            super("No roles were sent in the token");
        }
    }

    /**
     * Exception throw when roles key is returned in the token attributes but the user has none assigned.
     */
    public static final class NoRolesException extends UnableToParseAuthDetailsException {

        /**
         * Constructs a new {@code NoRolesException}.
         */
        public NoRolesException() {
            super("No roles are assigned to the user");
        }
    }

}