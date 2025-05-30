package uk.gov.laa.gpfd.model;

import jakarta.annotation.Nullable;

import java.util.UUID;

/**
 * Represents an entity that can be uniquely identified by a {@link UUID}.
 * <p>
 * Implementing this interface indicates that the class has a distinct identity, which may be
 * {@code null} for newly created entities that haven't been persisted yet.
 *
 * @see Nullable
 * @see UUID
 */
public interface Identifiable {

    /**
     * Gets the unique identifier for this entity.
     * <p>
     * The identifier may be {@code null} if the entity has not been persisted
     * (i.e., doesn't yet have a database-assigned ID).
     */
    @Nullable
    UUID getId();

    /**
     * Returns a string representation of the entity's identifier.
     * <p>
     * If the entity has no ID (null), returns "null" (as a string).
     * Otherwise returns the UUID's string representation.
     * </p>
     *
     * @return string representation of the ID, never null
     */
    default String getIdAsString() {
        var id = getId();
        return id == null ? "null" : id.toString();
    }
}