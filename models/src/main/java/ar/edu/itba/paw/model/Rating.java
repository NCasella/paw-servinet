package ar.edu.itba.paw.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ratings")
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "ratings_ratingid_seq")
    @SequenceGenerator(sequenceName = "ratings_ratingid_seq",name="ratings_ratingid_seq",allocationSize = 1)
    private long ratingid;
    @ManyToOne(optional = false)
    @JoinColumn(name="serviceid")
    private Service service;
    @ManyToOne(optional = false)
    @JoinColumn(name="userid")
    private User user;
    @Column(nullable = false)
    private int rating;
    @Column
    private String comment;
    @Column(nullable = false)
    private LocalDate date;
    protected Rating() {}

    public Rating(Service service, User user, int rating, String comment, LocalDate date) {
        this.service = service;
        this.user = user;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
    }

    public long getId() {
        return ratingid;
    }

    public long getServiceid() {
        return service.getId();
    }

    public long getUserid() {
        return user.getUserId();
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
