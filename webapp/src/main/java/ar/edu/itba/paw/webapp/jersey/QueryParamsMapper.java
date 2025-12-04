package ar.edu.itba.paw.webapp.jersey;

import ar.edu.itba.paw.model.Categories;
import ar.edu.itba.paw.model.Neighbourhoods;
import ar.edu.itba.paw.model.ServicesOrderFilters;

import java.util.Arrays;

public final class QueryParamsMapper {

    private QueryParamsMapper() {}

    public static Neighbourhoods[] mapNeighbourhoods(String raw) {
        if (raw == null || raw.isBlank()) {
            return new Neighbourhoods[0];
        }

        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .map(Neighbourhoods::fromName)
                .toArray(Neighbourhoods[]::new);
    }

    public static Categories mapCategory(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return Categories.fromName(raw);
    }

    public static ServicesOrderFilters mapOrderFilter(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return ServicesOrderFilters.fromValue(raw);
    }
}

