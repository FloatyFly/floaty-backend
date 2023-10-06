package ch.floaty.domain;

import javax.persistence.*;

@Entity()
@Table(name = "t_flight")
public class Flight {
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userid")
    private User user;

    private String takeoff;

    private Long duration;

    public Long getDuration(){return duration;}
    public void setDuration (Long duration) {this.duration = duration;}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTakeoff() {
        return takeoff;
    }

    public void setTakeoff(String takeoff) {
        this.takeoff = takeoff;
    }
}

