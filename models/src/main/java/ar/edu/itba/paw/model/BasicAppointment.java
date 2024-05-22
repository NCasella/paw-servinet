package ar.edu.itba.paw.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Entity
@Table(name = "appointments")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class BasicAppointment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "appointments_appointmentid_seq")
    @SequenceGenerator(name = "appointments_appointmentid_seq",sequenceName = "appointments_appointmentid_seq",allocationSize = 1)
    @Column(name = "appoinmentid")
    private long id;
    @Column(name = "serviceid", nullable = false )    
    private long serviceid;
    @Column(name = "startDate", nullable = false)
    private LocalDateTime startDate;
    @Column(name = "endDate")
    private LocalDateTime endDate;
    @Column(name = "location")
    private String location;
    @Column(name = "confirmed")
    private boolean confirmed;

    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("EEE dd MMMM");
    private static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("EEE dd MMMM yyyy, HH:mm");
    private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
    private String startDateString;
    private final String HOMESERVICE="-";


    public BasicAppointment(long serviceid, LocalDateTime startDate, LocalDateTime endDate, String location, boolean confirmed) {
        this.serviceid = serviceid;
        this.startDate = startDate;
        this.endDate = endDate;
        this.confirmed = confirmed;
        this.location = location;
        this.startDateString = startDate.format(dateFormat);
    }

    public BasicAppointment() {

    }

    public long getId() {
        return id;
    }

    public long getServiceid() {
        return serviceid;
    }
    public LocalDateTime getStartDate() {
        return startDate;
    }
    public String getStartDateString() {
        return startDate.format(dateFormat);
    }
    public String getStartDateWithTimeString(){
        return startDate.format(dateTimeFormat);
    }
    public String getStartDateTimeString(){
        return startDate.format(timeFormat);
    }
    public String getEndDateTimeString(){
        return endDate.format(timeFormat);
    }
    public LocalDateTime getEndDate() {
        return endDate;
    }

    public boolean getConfirmed() {
        return confirmed;
    }
    public String getLocation() {
        return location;
    }

    public void setEndDate(){}

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public void setConfirmed() {
        this.confirmed = true;
    }

    public boolean getHomeService(){
        return !Objects.equals(location, HOMESERVICE);
    }

    public boolean getDuration(){
        return !startDate.equals(endDate);
    }
}
