package ar.edu.itba.paw.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class Appointment extends BasicAppointment{

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(name="userid")
    private User appointedBy;

    public Appointment(Service service, User appointedBy, LocalDateTime startDate, LocalDateTime endDate, String location, boolean confirmed, String description) {
        super(service, startDate, endDate, location, confirmed, description);
        this.appointedBy=appointedBy;
    }
    public Appointment(Service service, User appointedBy, LocalDateTime startDate, LocalDateTime endDate, String location, boolean confirmed) {
        super(service, startDate, endDate, location, confirmed, "");
        this.appointedBy=appointedBy;
    }

    public Appointment() {
        super();
    }

    public User getAppointedBy(){return appointedBy;}
    public long getUserid() {
        return appointedBy.getUserId();
    }

}
