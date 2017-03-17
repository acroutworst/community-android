package com.croutworst.community.authentication;

/**
 * Created by adamc on 1/21/17.
 */

public class ErrorEvent {
    private int errorCode = -1;
    private String errorMsg = null;

    public ErrorEvent(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}