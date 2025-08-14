package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.Service;
import ar.edu.itba.paw.webapp.jersey.PathUrls;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Arrays;
import java.util.Objects;

public class ServiceDto {
    private long serviceId;
    private String serviceName;
    private boolean homeService;
    private URI businessUri;


    private URI self;
    private String[] neighbourhoods;
    private String address;
    private double rating;
    private String description;
    private int duration;
    private boolean additionalCosts;
    private String price;
    private String category;
    private String pricingType;

    public static ServiceDto fromService(Service service, UriInfo uriInfo){
    ServiceDto serviceDto =new ServiceDto();
    serviceDto.setServiceId(service.getId());
    serviceDto.setServiceName(service.getName());
    serviceDto.setHomeService(service.getHomeService());
    serviceDto.setAddress(service.getLocation());
    serviceDto.setDescription(service.getDescription());
    serviceDto.setDuration(service.getDuration());
    serviceDto.setPrice(service.getPrice());
    serviceDto.setCategory(service.getCategory().getValue());//TODO: ver localizacion?
    serviceDto.setPricingType(service.getPricing().getValue());
    serviceDto.setBusinessUri(uriInfo.getBaseUriBuilder().path(PathUrls.BUSINESSES_URL.getUrl()).path(String.valueOf(service.getBusinessid())).build());
    serviceDto.setNeighbourhoods(service.getNeighbourhoodAvailable().toArray(String[]::new));

    serviceDto.setSelf(uriInfo.getAbsolutePathBuilder().path(PathUrls.SERVICES_URL.getUrl()).path(String.valueOf(service.getId())).build());
    return serviceDto;
    }
    private ServiceDto(){}

    public long getServiceId() {
        return serviceId;
    }
    public void setServiceId(long serviceId){
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
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

    public void setBusinessUri(URI uri){this.businessUri=uri;}
    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getBusinessUri() {
        return businessUri;
    }
    @Override
    public int hashCode(){
        return Objects.hash(serviceName,homeService, Arrays.hashCode(neighbourhoods),address,rating,description,duration,additionalCosts,price,pricingType);
    }
}
