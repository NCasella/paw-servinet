package ar.edu.itba.paw.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "questions_questionid_seq")
    @SequenceGenerator(sequenceName = "questions_questionid_seq",name="questions_questionid_seq",allocationSize = 1)
    private long questionid;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="serviceid")
    private Service service;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userid")
    private User user;
    @Column(nullable = false)
    private String question;
    @Column
    private String response;
    @Column(nullable = false)
    private LocalDate date;

    protected Question() {}

    public Question( Service service, User user, String question, String response, LocalDate date) {
        this.service = service;
        this.user = user;
        this.question = question;
        this.response = response;
        this.date = date;
    }

    @Override
    public boolean equals(Object o){
        if (this==o)
            return true;
        if(! (o instanceof Question q))
            return false;
        return Objects.equals(this.questionid,q.questionid);

    }

    @Override
    public int hashCode(){
        return Objects.hashCode(questionid);
    }

    public long getId() {
        return questionid;
    }

    public long getServiceid() {
        return service.getId();
    }

    public long getUserid() {
        return user.getUserId();
    }

    public String getQuestion() {
        return question;
    }

    public String getResponse() {
        return response;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setService(Service service) {
        this.service = service;
    }
}
