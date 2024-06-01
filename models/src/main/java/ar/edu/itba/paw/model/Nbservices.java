package ar.edu.itba.paw.model;

import javax.persistence.*;

@Entity
public class Nbservices {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "nbservices_insertid_seq")
    @SequenceGenerator(name = "nbservices_insertid_seq", sequenceName = "nbservices_insertid_seq", allocationSize = 1)
    @Column(name = "insertid")
    private Long id;

    @ManyToOne
    @JoinColumn(name="serviceid")
    private Service serviceIn;

    @Column(name = "neighbourhood")
    private String neighbourhood;


    public Nbservices() {
    }

    public Nbservices(Service serviceIn,Neighbourhoods neighbourhood) {
        this.serviceIn=serviceIn;
        this.neighbourhood = neighbourhood.getValue();
    }

    public long getId() {
        return id;
    }


    public String getNeighbourhood() {
        return neighbourhood;
    }

    public void setNeighbourhood(Neighbourhoods neighbourhood) {
        this.neighbourhood = neighbourhood.getValue();
    }

}
