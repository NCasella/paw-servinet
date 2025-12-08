package ar.edu.itba.paw.webapp.jersey;

import ar.edu.itba.paw.model.Categories;
import ar.edu.itba.paw.model.Neighbourhoods;
import ar.edu.itba.paw.model.ServicesOrderFilters;
import ar.edu.itba.paw.model.exceptions.InvalidFilterException;

import java.util.Arrays;

public final class QueryParamsMapper {

    private QueryParamsMapper() {}

    public static Neighbourhoods[] mapNeighbourhoods(String raw) {
        if (raw == null || raw.isBlank()) {
            return new Neighbourhoods[0];
        }

        try {
            return Arrays.stream(raw.split(","))
                    .map(String::trim)
                    .map(Neighbourhoods::fromName)
                    .toArray(Neighbourhoods[]::new);
        } catch (IllegalArgumentException e) {
            throw new InvalidFilterException("Invalid neighbourhood value");
        }
    }

    public static Categories mapCategory(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }

        try {
            return Categories.fromName(raw);
        } catch (IllegalArgumentException e) {
            throw new InvalidFilterException("Invalid category " + raw);
        }
    }

    public static ServicesOrderFilters mapOrderFilter(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return ServicesOrderFilters.fromValue(raw);
        } catch (IllegalArgumentException e) {
            throw new InvalidFilterException("Invalid order filter " + raw);
        }
    }
}

