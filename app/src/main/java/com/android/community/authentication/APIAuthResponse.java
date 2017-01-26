package com.android.community.authentication;

import com.google.gson.Gson;

/**
 * Created by adamc on 1/21/17.
 */

public class APIAuthResponse {
    private String access_token;
    private String access_type;
    private String scope;
    private int expires_in;

    public String getToken() {
        return this.access_token;
    }


}
