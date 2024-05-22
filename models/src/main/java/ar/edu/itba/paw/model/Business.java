package ar.edu.itba.paw.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="business")
public class Business {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "business_businessid_seq")
    @SequenceGenerator(sequenceName = "users_userid_seq",name="users_userid_seq",allocationSize = 1)
    private Long businessid;

    private String businessName;
    @ManyToOne
    private User ownedBy;

    @Column(nullable = false)
    private String telephone;

    @Column(nullable = false)
    private String email;

    @OneToMany
    private List<Service> servicesProvided;

    private String location;
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

    public User getOwnedBy(){return ownedBy;}


    protected Business(){}

    public Business(long businessid, String businessName, User ownedBy,String telephone, String email, String location) {
        this.businessid = businessid;
        this.ownedBy=ownedBy;
        this.businessName = businessName;
        this.telephone = telephone;
        this.email = email;
        this.location = location;
    }


}
