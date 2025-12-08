package ar.edu.itba.paw.model;

import ar.edu.itba.paw.model.exceptions.InvalidOperationException;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "services")
public class Service extends BasicService {

    @Column(name = "servicedescription")
    private String description;

    @Column(name = "homeservice")
    private boolean homeService;


    @OneToMany(mappedBy = "serviceIn")
    private List<Nbservices> neighbourhoodAvailable;


    @Column(name = "minimalduration")
    private int duration;

    @Column(name = "pricingtype")
    private String pricing;

    @Column(name = "price", length = 255)
    private String price;


    @Column(name = "category")
    private String category;

    @Column(name = "additionalcharges")
    private boolean additionalCharges;

    @OneToMany(mappedBy = "service")
    private List<Question> questions;
    @OneToMany(mappedBy = "service")
    private List<Rating> ratings;

    @Formula("(select coalesce(round(avg(r.rating), 2), 0) from ratings r where r.serviceid = id)")
    private double ratingAvg;
    @Formula("(select count(r.rating) from ratings r where r.serviceid = id)")
    private int ratingsCount;
    @Formula("(select count(q.questionid) from questions q where q.serviceid = id)")
    private int questionsCount;
    public Service() {
    }

    public Service(Business business, String name, String description, boolean homeService, String location, Categories category, int duration, PricingTypes pricingType, String price, boolean additionalCharges,Long imageId) {
        super(business, name, location, imageId);
        this.description = description;
        this.homeService = homeService;
        this.category = category.getValue();
        this.duration = duration;
        this.pricing = pricingType.getValue();
        this.price = price;
        this.additionalCharges = additionalCharges;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Categories getCategory() {
        return Categories.fromValue(category);
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean getHomeService() {
        return homeService;
    }

    public void setHomeService(boolean homeService) {
        this.homeService = homeService;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean hasNeighbourhoodAvailable(Neighbourhoods neighbourhood) {
        return neighbourhoodAvailable.stream().anyMatch((nbservices -> nbservices.getNeighbourhood().equals(neighbourhood.getValue())));
    }

    public List<String> getNeighbourhoodAvailable() {
        List<String> list=new ArrayList<>();
        for(Nbservices n: neighbourhoodAvailable){
            list.add(n.getNeighbourhood());
        }
        return list;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public PricingTypes getPricing() {
        return PricingTypes.fromValue(pricing);
    }

    public void setPricing(String pricing) {
        this.pricing = pricing;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean getAdditionalCharges() {
        return additionalCharges;
    }

    public void setAdditionalCharges(boolean additionalCharges) {
        this.additionalCharges = additionalCharges;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public double getRatingAvg() {
        return ratingAvg;
    }

    public int getQuestionsCount() {
        return questionsCount;
    }

    public int getRatingsCount() {
        return ratingsCount;
    }
    public void setNeighbourhoodAvailable(List<Nbservices> neighbourhoodAvailable) {
        this.neighbourhoodAvailable = neighbourhoodAvailable;
    }
}
