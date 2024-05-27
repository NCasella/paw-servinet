package ar.edu.itba.paw.model;

import javax.persistence.*;

@Entity
public class Nbservices {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "services_id_seq")
    @SequenceGenerator(name = "services_id_seq", sequenceName = "services_id_seq", allocationSize = 1)
    @Column(name = "insertid")
    private Long id;

    @ManyToOne
    @JoinColumn(name="serviceid")
    private Service serviceIn;

    @Column(name = "neighbourhood")
    @Enumerated(EnumType.STRING)
    private Neighbourhoods neighbourhood;


    public Nbservices() {
    }

    public Nbservices(Service serviceIn,Neighbourhoods neighbourhood) {
        this.serviceIn=serviceIn;
        this.neighbourhood = neighbourhood;
    }

    public long getId() {
        return id;
    }


    public Neighbourhoods getNeighbourhood() {
        return neighbourhood;
    }

    public void setNeighbourhood(Neighbourhoods neighbourhood) {
        this.neighbourhood = neighbourhood;
    }

}
