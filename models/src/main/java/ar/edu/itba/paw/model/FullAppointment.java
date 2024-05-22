package ar.edu.itba.paw.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Entity
@Table(name = "appointments")
public class FullAppointment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "appointments_appointmentid_seq")
    @SequenceGenerator(name = "appointments_appointmentid_seq",sequenceName = "appointments_appointmentid_seq",allocationSize = 1)
    @Column(name = "appoinmentid", nullable = false)
    private long id;

    @ManyToOne(optional = false)                        // orphan removal va aca o solo cuando estas creando, y cascade?)
    @JoinColumn(name = "serviceid")                     // si @Column => va a poner todo Service en la tabla
    private Service service;

    @ManyToOne(optional = false)
    @JoinColumn(name = "userid", nullable = false)
    private User user;

    @Column(name = "startDate", nullable = false)
    private LocalDateTime startDate;
    @Column(name = "endDate")
    private LocalDateTime endDate;
    @Column(name = "location")
    private String location;
    @Column(name = "confirmed")
    private boolean confirmed;


    //! businessid lo obtiene de service, eso es posible?
    @ManyToOne()                                        //no uso @Column asi no lo agrega a table
    @JoinColumn(name = "businessid", nullable = false)
    private Business business;

    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("EEE dd MMMM");
    private static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("EEE dd MMMM yyyy, HH:mm");
    private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
    private String startDateString;
    private final String HOMESERVICE="-";

    //! para q sea lazy tengo q construirlo con Hibernate.initialize(Object obj); ?
    //! tengo q pasar Service en vez de serviceid en el constructor?
    public FullAppointment(long serviceid, LocalDateTime startDate, LocalDateTime endDate, String location, boolean confirmed) {
        this.serviceid = serviceid;
        this.startDate = startDate;
        this.endDate = endDate;
        this.confirmed = confirmed;
        this.location = location;
        this.startDateString = startDate.format(dateFormat);
    }

    public FullAppointment() {

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

    public boolean getHomeService(){
        return !Objects.equals(location, HOMESERVICE);
    }

    public boolean getDuration(){
        return !startDate.equals(endDate);
    }
}
