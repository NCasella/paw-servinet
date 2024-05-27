package ar.edu.itba.paw.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "users_userid_seq")
    @SequenceGenerator(sequenceName = "users_userid_seq",name="users_userid_seq",allocationSize = 1)
    private Long userId;

    @Column(unique = true,nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(nullable = false)
    private String email;

    @OneToMany(mappedBy = "ownedBy")
    private List<Business> businessOwned;

    private String telephone;

    @Column(nullable = false)
    private boolean isProvider;


    private String locale;
    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getFullName(){
        return name + ' ' + surname;
    }

    public String getEmail() {
        return email;
    }

    public String getTelephone() {
        return telephone;
    }

    public boolean getProvider() {
        return isProvider;
    }

    public boolean isProvider() {
        return isProvider;
    }

    protected User(){}

    public User(String username,String password ,String name, String surname, String email, String telephone, boolean isProvider, String locale) {
        this.username = username;
        this.password=password;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.telephone = telephone;
        this.isProvider = isProvider;
        this.locale = locale;
    }
    public String getPassword() {
        return password;
    }

    public long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getLocale() {
        return locale;
    }


    public void setPassword(String password) {
        this.password = password;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setProvider(boolean provider) {
        isProvider = provider;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
    public void setUsername(String username){
        this.username=username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public void setEmail(String email){this.email=email;}
}
