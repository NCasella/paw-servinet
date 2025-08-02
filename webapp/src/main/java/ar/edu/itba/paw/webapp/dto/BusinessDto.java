package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.Business;
import ar.edu.itba.paw.webapp.jersey.PathUrls;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Objects;

public class BusinessDto {
    private String businessName;
    private String email;
    private String telephone;
    private String address;
    private double rating;
    private URI userOwnerPath;
    private URI businessStatistics;
    private URI reviewsPath;
    private URI questionsPath;

    public static BusinessDto fromBusiness(Business business, UriInfo uriInfo){
        BusinessDto toReturn=new BusinessDto();
        toReturn.setAddress(business.getLocation());
        toReturn.setBusinessName(business.getName());
        toReturn.setEmail(business.getEmail());
        toReturn.setTelephone(business.getTelephone());
        toReturn.setQuestionsPath(uriInfo.getBaseUriBuilder().path(PathUrls.SERVICES_QUESTIONS.getUrl()).queryParam("forBusiness",business.getUserId()).build());
        toReturn.setReviewsPath(uriInfo.getBaseUriBuilder().path(PathUrls.SERVICES_URL.getUrl()).queryParam("providedBy",business.getUserId()).build());
        toReturn.setRating(business.getBusinessRatingAvg());
        toReturn.setUserOwnerPath(uriInfo.getBaseUriBuilder().path("users").path(String.valueOf(business.getUserId())).build());
        toReturn.setBusinessStatistics(uriInfo.getBaseUriBuilder().path(PathUrls.BUSINESSES_URL.getUrl()).path(String.valueOf(business.getBusinessid())).path(PathUrls.BUSINESSES_STATISTICS_URL.getUrl()).build());
        return toReturn;
    }

    public URI getReviewsPath(){return this.reviewsPath;}
    public URI getQuestionsPath(){return this.questionsPath;}
    public void setUserOwnerPath(URI path){this.userOwnerPath=path;}
    public void setBusinessStatistics(URI path){this.businessStatistics=path;}
    public URI getBusinessStatistics(){return this.businessStatistics;}
    public String getBusinessName() {
        return businessName;
    }
    public URI getUserOwnerPath(){return this.userOwnerPath;}
    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }
    public void setReviewsPath(URI path){this.reviewsPath=path;}
    public void setQuestionsPath(URI path){this.questionsPath=path;}
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
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
    @Override
    public int hashCode(){
        return Objects.hash(businessName,email,telephone,address,rating);
    }
}
