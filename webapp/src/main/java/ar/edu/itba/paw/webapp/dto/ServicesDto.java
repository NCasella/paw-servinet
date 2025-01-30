package ar.edu.itba.paw.webapp.dto;

public class ServicesDto {
    private long serviceId;
    private String serviceName;
    private long businessId;
    private boolean homeService;
    private String[] neighbourhoods;
    private String address;
    private double rating;
    private String description;
    private int duration;
    private boolean additionalCosts;
    private String price;
    private String category;
    private String pricingType;

    public long getServiceId() {
        return serviceId;
    }
    public void setServiceId(long serviceId){
        serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(long businessId) {
        this.businessId = businessId;
    }
    public boolean getHomeService(){
        return homeService;
    }

    public void setHomeService(boolean homeService) {
        this.homeService = homeService;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getNeighbourhoods() {
        return neighbourhoods;
    }

    public void setNeighbourhoods(String[] neighbourhoods) {
        this.neighbourhoods = neighbourhoods;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean getAdditionalCosts() {
        return additionalCosts;
    }

    public void setAdditionalCosts(boolean additionalCosts) {
        this.additionalCosts = additionalCosts;
    }

    public String getPricingType() {
        return pricingType;
    }

    public void setPricingType(String pricingType) {
        this.pricingType = pricingType;
    }
}
