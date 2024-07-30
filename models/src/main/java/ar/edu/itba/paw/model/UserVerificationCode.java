package ar.edu.itba.paw.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "userverificationcodes")
public class UserVerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "userverification_codeid_seq")
    @SequenceGenerator(sequenceName = "userverification_codeid_seq",name="userverification_codeid_seq",allocationSize = 1)
    private long id;
    @OneToOne(optional = false)
    @JoinColumn(name="userid")
    private User requestedBy;
    @Column(nullable = false)
    private String verificationCode;
    @Column(nullable = false)
    private LocalDateTime expirationDate;

    protected UserVerificationCode(){

    }
    public UserVerificationCode(User requestedBy, String verificationCode, LocalDateTime expirationDate){
        this.requestedBy = requestedBy;
        this.verificationCode = verificationCode;
        this.expirationDate = expirationDate;
    }

    public long getId() {
        return id;
    }

    public User getRequestedBy() {
        return requestedBy;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

}
