package com.android.community.models;

/**
 * Created by adamc on 1/26/17.
 */

public class Account {
    private String id = null;
    private String firstName = null;
    private String lastName = null;
    private String username = null;
    private String email = null;
    private String token = null;
    private String interests = null;
    private String phoneNumber = null;

    public String getToken() {
        return this.token;
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
    public String getInterests() {
        return this.interests;
    }
    public String getPhoneNumber() {
        return this.phoneNumber;
    }
    public String getUsername() {
        return this.username;
    }
}
