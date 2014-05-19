package com.silva.objects;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by ceosi_000 on 5/19/2014.
 */
public class Message implements Serializable {

    private int id;

    private User user;

    private String text;

    private Date createdDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

}
