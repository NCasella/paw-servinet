package ar.edu.itba.paw.model;

public enum ServicesOrderFilters {
    RATE_ASC("rate_asc", "asc", "reviews.date-asc"),
    RATE_DESC("rate_desc","desc", "reviews.date-desc");

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

    public static ServicesOrderFilters findByValue(String orderFilter) {
        for (ServicesOrderFilters serviceOrderFilter: values()) {
            if (serviceOrderFilter.getType().equalsIgnoreCase(orderFilter)) {
                return serviceOrderFilter;
            }
        }
        return null;
    }


}
