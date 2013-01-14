package com.britesnow.samplesocial.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "principal")
@javax.persistence.SequenceGenerator(name = "SEQ_STORE", allocationSize = 1, sequenceName = "principal_id_seq")
public class User extends BaseEntity {
    
    private String username;
    private String password;
    
    
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
