package ar.edu.itba.paw.model;

import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "pricingtype")
    private PricingTypes pricing;

    @Column(name = "price", length = 255)
    private String price;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Categories category;

    @Column(name = "additionalcharges")
    private boolean additionalCharges;

    @OneToMany(mappedBy = "service")
    private List<Question> questions;
    @OneToMany(mappedBy = "service")
    private List<Rating> ratings;

    @Formula("(select coalesce(avg(r.rating),0) from ratings r where r.serviceid = id)")
    private double ratingAvg;
    @Formula("(select count(r.rating) from ratings r where r.serviceid = id)")
    private int ratingsCount;
    @Formula("(select count(q.questionid) from questions q where q.serviceid = id)")
    private int questionsCount;
    public Service() {
    }

    public Service(long businessid, String name, String description, boolean homeService, String location, Categories category, int duration, PricingTypes pricingType, String price, boolean additionalCharges,long imageId) {
        super(businessid, name, location, imageId);
        this.description = description;
        this.homeService = homeService;
        this.category = category;
        this.duration = duration;
        this.pricing = pricingType;
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
        return category;
    }

    public void setCategory(String category) {
        this.category = Categories.findByValue(category);
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

    public List<String> getNeighbourhoodAvailable() {
        List<String> list=new ArrayList<>();
        for(Nbservices n: neighbourhoodAvailable){
            list.add(n.getNeighbourhood().getValue());
        }
        return list;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public PricingTypes getPricing() {
        return pricing;
    }

    public void setPricing(String pricing) {
        this.pricing = PricingTypes.findByValue(pricing);
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
}
