package com.britesnow.samplesocial.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "contact")
@javax.persistence.SequenceGenerator(name = "SEQ_STORE", allocationSize = 1, sequenceName = "contact_id_seq")
public class Contact extends BaseEntity {
    private String name;
    private String fbid;
    private String fbtoken;

    private String email;
    private String hometownname;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFbid() {
        return fbid;
    }

    public void setFbid(String fbid) {
        this.fbid = fbid;
    }

    public String getFbtoken() {
        return fbtoken;
    }

    public void setFbtoken(String fbtoken) {
        this.fbtoken = fbtoken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHometownname() {
        return hometownname;
    }

    public void setHometownname(String hometownname) {
        this.hometownname = hometownname;
    }
}
