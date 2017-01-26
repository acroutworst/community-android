package com.android.community;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.otto.Produce;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by adamc on 1/21/17.
 */

public class Communicator {
    private static final String TAG = "Communicator";
    private static final String SERVER_URL = "https://community-ci.herokuapp.com";
    private static String API_TOKEN = "";
    private static String USER_TOKEN = "";
    private static String CLIENT_ID = "gBP4u4xwAiB1WaaDZGbNxGCPS8upLQsdkXP1avE4";
    private static String CLIENT_SECRET = "pemkNdWdYU4rrJ6AQxvKsJAivx9Gz1fh0zRVBSYkDMofahmGxDUO4vEF5dBAmU5mwrXLkp6BVZO5iK5irszy4CWKpcrdY3f1511q9nZH68vkkrFl59GU8rGqx5fwK34U";
    private static String GRANT_TYPE = "client_credentials";
    private static String USERCLIENT_ID = "m062TLGH5WOdApYD3jXcM6jA5OnRleVmAoTw3zfu";
    private static String USERCLIENT_SECRET = "6jtLHhKuefsPogv8J8PBcPUzVrqOkcYRuPFmAqml6gukbStgl0bjSUPHFFoaDbWKZ1tCDlOrQo69irklmFTEKsYE2kCrUGZmgeLSM37yDLJhhviLujmgeZdMATHSx90a";
    private static String USER_GRANT_TYPE = "password";

    public APIAuthResponse apiAuthResponse = null;
    public ServerResponse serverResponse = null;
    public boolean serverResponseCode = false;
    public ClientType client = ClientType.BASECLIENT;

    public enum ClientType{
        BASECLIENT(CLIENT_ID, CLIENT_SECRET, GRANT_TYPE), USERCLIENT(USERCLIENT_ID, USERCLIENT_SECRET, USER_GRANT_TYPE);
        private String clientID;
        private String clientSecret;
        private String grantType;

        private ClientType(String clientID, String clientSecret, String grantType){
            this.clientID = clientID;
            this.clientSecret = clientSecret;
            this.grantType = grantType;
        }
    }

    public boolean getResponseCode() { return serverResponseCode; }
    public int getServerResponseCode(){
        return serverResponse.getResponseCode();
    }

    public void loginPost(String username, String password) {
        //Here a logging interceptor is created
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        //The logging interceptor will be added to the http client
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(30, TimeUnit.SECONDS); // connect timeout
        httpClient.readTimeout(30, TimeUnit.SECONDS);    // socket timeout
        httpClient.addInterceptor(logging);

        //The Retrofit builder will have the client attached, in order to get connection logs
        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(SERVER_URL)
                .build();
        ServerRequestInterface service = retrofit.create(ServerRequestInterface.class);

        GetToken(service, username, password);
    }

    /*public void queryPost(String username, String password, String email) {
        //Here a logging interceptor is created
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        //The logging interceptor will be added to the http client
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        //The Retrofit builder will have the client attached, in order to get connection logs
        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(SERVER_URL)
                .build();
        ServerRequestInterface service = retrofit.create(ServerRequestInterface.class);

        GetToken(service, "" , ""); // change later

        Call<ServerResponse> call = service.apiPost(API_TOKEN, makeQuery(username, password, email));

        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                BusProvider.getInstance().post(new ServerEvent(response.body()));
                Log.e(TAG, "Success");

                Gson gson = new Gson();
                apiAuthResponse = gson.fromJson(response.message(), APIAuthResponse.class);

                API_TOKEN = apiAuthResponse.getToken();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                // handle execution failures like no internet connectivity
                BusProvider.getInstance().post(new ErrorEvent(-2, t.getMessage()));
                Log.e(TAG, "Failure");

            }
        });
    }*/

    private boolean GetToken(ServerRequestInterface service, String username, String password) {
        Call<ServerResponse> apiCall = null;
        if(client == ClientType.USERCLIENT){
            apiCall = service.postUserToken(client.clientID, client.clientSecret, client.grantType, username, password);
        } else{
            apiCall = service.postClientToken(CLIENT_ID, CLIENT_SECRET, GRANT_TYPE);
        }

        apiCall.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                Log.d(TAG, "response.message(): " + response.message());
                Log.d(TAG, "response.isSuccessful(): " + response.isSuccessful());
                serverResponseCode = response.isSuccessful(); //is true if the response code is 2xx
                Log.d(TAG, "serverResponseCode in apiCall: " + serverResponseCode);
                BusProvider.getInstance().post(new ServerEvent(response.body()));
                Log.d("LOGIN_POST_SUCCESS", "THE LOGIN POST WAS SUCCESSFUL");
                Log.e(TAG, "Success");
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                // handle execution failures like no internet connectivity
                BusProvider.getInstance().post(new ErrorEvent(-2, t.getMessage()));
                Log.e(TAG, "Failure");
            }
        });

//        Gson gson = new GsonBuilder().create();
//
//        Log.d(TAG, "apiCall.toString(): " + apiCall.toString());
//        apiAuthResponse = gson.fromJson(apiCall.toString(), APIAuthResponse.class);
//        serverResponse = gson.fromJson(apiCall.toString(), ServerResponse.class);
//
//        USER_TOKEN = apiAuthResponse.getToken();

        return true;
    }

    //private boolean checkStaleToken() { return false; }

    private String makeQuery(String user, String pass, String email) {
        return String.format("mutation{\nloginUser (input: {\n    username: \"{0}\"\n    password: \"{1}\"\n    email: \"{2}\"\n  }) {\n    ok\n    user {\n    token\n    }\n  }\n}\n",
                user,
                pass,
                email
        );
    }

    @Produce
    public ServerEvent produceServerEvent(ServerResponse serverResponse) {
        return new ServerEvent(serverResponse);
    }

    @Produce
    public ErrorEvent produceErrorEvent(int errorCode, String errorMsg) {
        return new ErrorEvent(errorCode, errorMsg);
    }
}