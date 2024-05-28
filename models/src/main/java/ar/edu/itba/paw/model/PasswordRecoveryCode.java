package ar.edu.itba.paw.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "passwordrecoverycodes")
public class PasswordRecoveryCode {
    @OneToOne(optional = false)
    private User requestedBy;
    @Column(nullable = false)
    private UUID code;
    @Column(nullable = false)
    private LocalDateTime expirationDate;

    public PasswordRecoveryCode(User requestedBy, UUID code, LocalDateTime expirationDate) {
        this.requestedBy = requestedBy;
        this.code = code;
        this.expirationDate = expirationDate;
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
}
