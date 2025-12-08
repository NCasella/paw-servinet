package ar.edu.itba.paw.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "passwordrecoverycodes")
public class PasswordRecoveryCode {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "passwordrecovery_codeid_seq")
    @SequenceGenerator(sequenceName = "passwordrecovery_codeid_seq",name="passwordrecovery_codeid_seq",allocationSize = 1)
    private long id;
    @OneToOne(optional = false)
    @JoinColumn(name="userid")
    private User requestedBy;
    @Column(nullable = false)
    private UUID code;
    @Column(nullable = false)
    private LocalDateTime expirationDate;

    protected PasswordRecoveryCode() {}
    public PasswordRecoveryCode(User requestedBy, UUID code, LocalDateTime expirationDate) {
        this.requestedBy = requestedBy;
        this.code = code;
        this.expirationDate = expirationDate;
    }
    public long getId() {
        return id;
    }

    public User getRequestedBy(){
        return requestedBy;
    }
    public long getUserId() {
        return requestedBy.getUserId();
    }

    public UUID getCode() {
        return code;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public boolean isExpired(){
        return expirationDate.isBefore(LocalDateTime.now());
    }
}
