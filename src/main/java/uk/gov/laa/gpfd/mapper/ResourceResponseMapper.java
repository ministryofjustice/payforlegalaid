package uk.gov.laa.gpfd.mapper;

/**
 * Defines a contract for mapping domain entities to API response DTOs with resource URLs.
 *
 * @param <T> the domain entity type
 * @param <R> the response DTO type
 */
public sealed interface ResourceResponseMapper<T, R> permits
        GetReportById200ResponseMapper,
        ReportsGet200ResponseReportListInnerMapper
{

    /**
     * Maps a domain entity to a API response DTO including resource URLs.
     *
     * @param entity the domain entity to map, must not be {@code null}
     * @return the fully mapped response DTO
     * @throws IllegalArgumentException if entity is {@code null} or contains invalid data
     * @throws IllegalStateException if URL construction fails due to invalid configuration
     */
    R map(T entity);
}
