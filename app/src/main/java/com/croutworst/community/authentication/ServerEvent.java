package com.croutworst.community.authentication;

/**
 * Created by adamc on 1/21/17.
 */

public class ServerEvent {
    private ServerResponse serverResponse = null;

    public ServerEvent(ServerResponse serverResponse) {
        this.serverResponse = serverResponse;
    }

    public ServerResponse getServerResponse() {
        return serverResponse;
    }

    public void setServerResponse(ServerResponse serverResponse) {
        this.serverResponse = serverResponse;
    }
}
