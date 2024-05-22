package ar.edu.itba.paw.model;

import javax.persistence.*;

@Entity
public class Nbservice {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "services_id_seq")
    @SequenceGenerator(name = "services_id_seq", sequenceName = "services_id_seq", allocationSize = 1)
    @Column(name = "insertid")
    private long id;

    @Column(name = "serviceid")
    private long serviceId;

    @Column(name = "neighbourhood")
    private String neighbourhood;

    public Nbservice() {

    }

    public Nbservice(long serviceId, String neighbourhood) {
        this.serviceId = serviceId;
        this.neighbourhood = neighbourhood;
    }

    public long getId() {
        return id;
    }

    public long getServiceId() {
        return serviceId;
    }

    public String getNeighbourhood() {
        return neighbourhood;
    }

    public void setNeighbourhood(String neighbourhood) {
        this.neighbourhood = neighbourhood;
    }

    public void setServiceId(long serviceId) {
        this.serviceId = serviceId;
    }
}
