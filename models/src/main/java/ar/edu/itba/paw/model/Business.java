package ar.edu.itba.paw.model;

import javax.persistence.*;

@Entity
@Table(name="business")
public class Business {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "business_businessid_seq")
    @SequenceGenerator(sequenceName = "business_businessid_seq",name="business_businessid_seq",allocationSize = 1)
    private long businessid;
    @Column(nullable = false)
    private String businessName;
    @ManyToOne(optional = false)
    private User user;
    @Column
    private String telephone;
    @Column
    private String email;
    @Column
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

    public User getUser() {
        return user;
    }

    protected Business() {}

    public Business(String businessName, User user,String telephone, String email, String location) {
        this.user = user;
        this.businessName = businessName;
        this.telephone = telephone;
        this.email = email;
        this.location = location;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUser(User user) {
        this.user = user;
    }

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
