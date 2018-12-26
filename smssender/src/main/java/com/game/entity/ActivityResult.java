package com.game.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:
 * @Author: Jason
 * @CreateDate: 2018/11/25 22:57
 */
@Entity
@Table(name="activity_result")
public class ActivityResult {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "activity_code")
    private String activityCode;

    private String phone;

    @Column(name = "created_time")
    private Date createdTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
