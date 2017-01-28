package com.android.community.authentication;

/**
 * Created by adamc on 1/21/17.
 */

public class APIAuthResponse {
    private String access_token = null;
    private String token_type = null;
    private String scope = null;
    private String refresh_token = null;
    private int expires_in = -1;

    public String getToken() {
        return this.access_token;
    }
    public String getRefreshToken() { return this.refresh_token; }
    public String getAccessType() { return this.token_type; }
    public String getScope() { return this.scope; }
    public int getExpireTime() { return this.expires_in; }

}
