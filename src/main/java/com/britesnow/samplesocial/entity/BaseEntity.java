package com.britesnow.samplesocial.entity;

import java.util.Date;

public class BaseEntity {
    private Long id;

    private Long createdBy_id;
    private Date createdDate;
    private Long updatedBy_id;
    private Date updatedDate;

    // --------- Persistent Getters & Setters --------- //
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreatedBy_id() {
        return createdBy_id;
    }

    public void setCreatedBy_id(Long createdBy_id) {
        this.createdBy_id = createdBy_id;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Long getUpdatedBy_id() {
        return updatedBy_id;
    }

    public void setUpdatedBy_id(Long updatedBy_id) {
        this.updatedBy_id = updatedBy_id;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
    // --------- /Persistent Getters & Setters --------- //
}
