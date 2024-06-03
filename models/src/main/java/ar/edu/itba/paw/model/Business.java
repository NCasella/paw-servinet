package ar.edu.itba.paw.model;

import org.hibernate.annotations.Formula;

import javax.persistence.*;

@Entity
@Table(name="business")
public class Business {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "business_businessid_seq")
    @SequenceGenerator(sequenceName = "business_businessid_seq",name="business_businessid_seq",allocationSize = 1)
    private Long businessid;
    @Column(nullable = false)
    private String businessName;
    @ManyToOne(optional = false)
    @JoinColumn(name = "userid")
    private User ownedBy;
    @Column(name="businesstelephone")
    private String telephone;
    @Column(name="businessemail")
    private String email;
    @Column(name="businesslocation")
    private String location;
    @Formula("( select coalesce(round(avg(r.rating), 1), 0) from services s join ratings r on s.id=r.serviceid where s.businessid = businessid )")
    private double businessRatingAvg;

    public long getBusinessid() {
        return businessid;
    }

    public String getBusinessName() {
        return businessName;
    }

    public String getName(){
        return businessName;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getEmail() {
        return email;
    }

    public String getLocation() {
        return location;
    }

    public User getOwnedBy() {
        return ownedBy;
    }
    public long getUserId(){
        return ownedBy.getUserId();
    }
    protected Business() {}

    public Business(String businessName, User ownedBy,String telephone, String email, String location) {
        this.ownedBy = ownedBy;
        this.businessName = businessName;
        this.telephone = telephone;
        this.email = email;
        this.location = location;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getBusinessRatingAvg(){return businessRatingAvg;}

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
