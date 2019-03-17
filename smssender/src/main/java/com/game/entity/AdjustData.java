package com.game.entity;


import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="adjust_data")
public class AdjustData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "activity_kind")
    private String activityKind;

    private String event;

    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "event_name")
    private String eventName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActivityKind() {
        return activityKind;
    }

    public void setActivityKind(String activityKind) {
        this.activityKind = activityKind;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}
