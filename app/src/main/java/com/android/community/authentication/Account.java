package com.android.community.authentication;

/**
 * Created by adamc on 1/26/17.
 */

public class Account {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String token;
    private String interests;
    private String phoneNumber;

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

}
