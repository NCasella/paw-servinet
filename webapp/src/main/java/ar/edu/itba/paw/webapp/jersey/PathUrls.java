package ar.edu.itba.paw.webapp.jersey;


public enum PathUrls {
    USERS_URL("users"),
    BUSINESSES_URL("businesses"),
    BUSINESSES_STATISTICS_URL("statistics"),
    SERVICES_URL("services"),
    SERVICES_QUESTIONS("questions");

    private final String url;
    PathUrls(String url){this.url=url;}

    public String getUrl(){return this.url;}
}
