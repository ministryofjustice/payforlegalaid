package uk.gov.laa.gpfd.exception.sds;

/** The exception thrown from Token provider. */
public class TokenProviderException extends RuntimeException {

    public TokenProviderException(String message) {
        super(message);
    }
}