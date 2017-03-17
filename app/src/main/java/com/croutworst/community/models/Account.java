package com.croutworst.community.models;

import java.util.Date;

/**
 * Created by adamc on 1/26/17.
 */

public class Account {
    public String id = null;
    public String firstName = null;
    public String lastName = null;
    public String username = null;
    public String email = null;
    public Date lastLogin = null;
    public Boolean isActive = null;
    public Date dateJoined = null;

    public Date getLastLogin() {
        return this.lastLogin;
    }
    public String getId() {
        return this.id;
    }
    public String getFirstName() {
        return this.firstName;
    }
    public String getLastName() {
        return this.lastName;
    }
    public String getEmail() {
        return this.email;
    }
    public Boolean getIsActive() {
        return this.isActive;
    }
    public Date getDateJoined() {
        return this.dateJoined;
    }
    public String getUsername() {
        return this.username;
    }
}
