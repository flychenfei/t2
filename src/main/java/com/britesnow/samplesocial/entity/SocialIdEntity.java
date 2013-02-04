package com.britesnow.samplesocial.entity;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.britesnow.samplesocial.oauth.ServiceType;

@Entity
@Table(name = "social_id_entity")
@javax.persistence.SequenceGenerator(name = "SEQ_STORE", allocationSize = 1, sequenceName = "social_id_entity_id_seq")
public class SocialIdEntity extends BaseEntity {
    private Long   user_id;
    @Column(length = 2048)
    private String token;
    @Column(name="token_date")
    private Date tokenDate;
    @Enumerated(EnumType.STRING)
    private ServiceType service;
    private String email;
    private String secret;
    private String fbid;
    @Transient
    private boolean isValid = false;
    
    public Long getUser_id() {
        return user_id;
    }
    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public Date getTokenDate() {
        return tokenDate;
    }
    public void setTokenDate(Date tokenDate) {
        this.tokenDate = tokenDate;
    }
    public ServiceType getService() {
        return service;
    }
    public void setService(ServiceType service) {
        this.service = service;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
    public String getFbid() {
        return fbid;
    }
    public void setFbid(String fbid) {
        this.fbid = fbid;
    }
}
