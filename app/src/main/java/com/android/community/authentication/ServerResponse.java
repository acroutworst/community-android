package com.android.community.authentication;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by adamc on 1/21/17.
 */

public class ServerResponse implements Serializable {
    @SerializedName("message")
    private String message;
    @SerializedName("response_code")
    private int responseCode;

    public ServerResponse(String message, int responseCode){
        this.message = message;
        this.responseCode = responseCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
}
