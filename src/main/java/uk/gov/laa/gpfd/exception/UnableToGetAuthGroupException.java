package uk.gov.laa.gpfd.exception;

import lombok.Getter;

/**
 * Abstract exception class to indicate that the necessary tokens cannot be obtained from the authentication Spring has returned
 */
@Getter
public abstract sealed class UnableToGetAuthGroupException extends RuntimeException {

    /**
     * Constructs a new {@code UnexpectedAuthTypeException} with the specified error message.
     *
     * @param message the detail message describing the exception.
     */
    protected UnableToGetAuthGroupException(String message) {
        super(message);
    }


    /**
     * Exception thrown when we can't get an authentication context from Spring
     */
    public static final class AuthenticationIsNullException extends UnableToGetAuthGroupException {

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
    public static final class PrincipalIsNullException extends UnableToGetAuthGroupException {

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
    public static final class UnexpectedAuthClassException extends UnableToGetAuthGroupException {

        /**
         * Constructs a new {@code UnexpectedAuthClassException} with the specified error message.
         */
        public UnexpectedAuthClassException(String authClass) {
            super("Unexpected auth type returned by Spring, could not extract groups. Got an instance of class " + authClass);
        }
    }

}