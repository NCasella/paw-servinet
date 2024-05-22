package ar.edu.itba.paw.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class Appointment extends BasicAppointment{

    @Column(name = "userid", nullable = false)
    private long userid;

    public Appointment(long serviceid, long userid, LocalDateTime startDate, LocalDateTime endDate, String location, boolean confirmed) {
        super(serviceid, startDate, endDate, location, confirmed);
        this.userid = userid;
    }

    public Appointment() {
        super();
    }

    public long getUserid() {
        return userid;
    }

}
