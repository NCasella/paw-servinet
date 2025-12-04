package ar.edu.itba.paw.model;

import java.util.Arrays;

public enum ServicesOrderFilters {
    RATE_ASC("rate_asc", "asc", "reviews.rating-asc"),
    RATE_DESC("rate_desc","desc", "reviews.rating-desc");

    private final String type;
    private final String order;
    private final String codeMsg;

    ServicesOrderFilters(String type, String order, String codeMsg){
        this.type = type;
        this.order = order;
        this.codeMsg = codeMsg;
    }

    public String getCodeMsg() {
        return codeMsg;
    }

    public String getOrder() {
        return order;
    }

    public String getType() {
        return type;
    }

    public static ServicesOrderFilters fromValue(String value) {
        return Arrays.stream(values())
                .filter(v -> v.type.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid order filter: " + value));
    }
}
