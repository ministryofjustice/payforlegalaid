package uk.gov.laa.pfla.util;

import com.fasterxml.jackson.core.TokenStreamFactory;
import lombok.SneakyThrows;
import org.springframework.http.HttpEntity;

/**
 * Provides JSON deserialization capabilities using Jackson's {@link TokenStreamFactory}.
 * <p>
 * This interface defines methods to deserialize JSON content from either raw strings or
 * Spring {@link HttpEntity} objects into Java objects of specified types.
 * </p>
 */
public interface JsonDeserializer {

    /**
     * Gets the {@link TokenStreamFactory} instance used for JSON processing.
     *
     * @return a non-null {@link TokenStreamFactory} instance
     */
    TokenStreamFactory tokenStreamFactory();

    /**
     * Deserializes the body of a Spring {@link HttpEntity} into the specified Java type.
     *
     * @param <T>       the target type to deserialize to
     * @param response  the HTTP response containing JSON content (body must not be null)
     * @param valueType the {@link Class} representing the target type
     * @return the deserialized object of type T
     * @throws NullPointerException if either the response or its body is null
     */
    default <T> T deserializeFromResponse(HttpEntity<String> response, Class<T> valueType) {
        return deserialize(response.getBody(), valueType);
    }

    /**
     * Deserializes a raw JSON string into the specified Java type.
     *
     * @param <T>       the target type to deserialize to
     * @param json      the JSON string to parse (must not be null)
     * @param valueType the {@link Class} representing the target type
     * @return the deserialized object of type T
     * @throws NullPointerException if the json parameter is null
     */
    @SneakyThrows
    default <T> T deserialize(String json, Class<T> valueType) {
        try (var parser = tokenStreamFactory().createParser(json)) {
            return parser.readValueAs(valueType);
        }
    }
}