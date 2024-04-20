package ch.floaty.domain;

import javax.persistence.*;

@Entity()
@Table(name = "t_flight")
public class Flight {

    public Flight(User user, String takeoff, Long duration, String date, String description) {
        this.user = user;
        this.takeoff = takeoff;
        this.duration = duration;
        this.date = date;
        this.description = description;
    }

    public Flight() {
    }

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "userid")
    private User user;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String date;

    private String takeoff;

    private Long duration;

    private String description;

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

