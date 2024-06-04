package ar.edu.itba.paw.model;

public enum RatingsFilters {
    DATE_ASC("date_asc", "date", "asc", "reviews.date-asc"),
    DATE_DESC("date_desc", "date", "desc", "reviews.date-desc"),
    RATING_ASC("rating_asc", "rating", "asc", "reviews.rating-asc"),
    RATING_DESC("rating_desc", "rating", "desc", "reviews.rating-desc");

    private final String filter;
    private final String type;
    private final String order;
    private final String codeMsg;

    RatingsFilters(String filter, String type, String order, String codeMsg){
        this.type = type;
        this.filter = filter;
        this.order = order;
        this.codeMsg = codeMsg;
    }

    public String getType() {
        return type;
    }

    public String getFilter() {
        return filter;
    }

    public String getCodeMsg() {
        return codeMsg;
    }

    public String getOrder() {
        return order;
    }

    public static RatingsFilters findByValue(String filter) {
        for (RatingsFilters ratingsFilter: values()) {
            if (ratingsFilter.getFilter().equalsIgnoreCase(filter)) {
                return ratingsFilter;
            }
        }
        return null;
    }

    public boolean isDateType(RatingsFilters filter) {
        return filter.type.equals("date");
    }
}
