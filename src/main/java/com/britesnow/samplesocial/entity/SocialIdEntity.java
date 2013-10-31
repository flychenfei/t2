package com.britesnow.samplesocial.entity;


import java.util.Date;


import com.britesnow.samplesocial.oauth.ServiceType;

public class SocialIdEntity extends BaseEntity {
    private Long   user_id;
    private String token;
    private Date tokenDate;
    private ServiceType service;
    private String email;
    private String secret;
    private String fbid;
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
