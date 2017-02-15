package com.android.community.authentication;

import android.util.Log;

import com.android.community.models.Account;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.otto.Produce;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by adamc on 1/21/17.
 */

public class Communicator {
    private static final String TAG = "Communicator";
    private static final String SERVER_URL = "https://community-ci.herokuapp.com";
    private static String USER_TOKEN = "";
    private static String API_TOKEN = "";
    private static String CLIENT_ID = "gBP4u4xwAiB1WaaDZGbNxGCPS8upLQsdkXP1avE4";
    private static String CLIENT_SECRET = "pemkNdWdYU4rrJ6AQxvKsJAivx9Gz1fh0zRVBSYkDMofahmGxDUO4vEF5dBAmU5mwrXLkp6BVZO5iK5irszy4CWKpcrdY3f1511q9nZH68vkkrFl59GU8rGqx5fwK34U";
    private static String GRANT_TYPE = "client_credentials";
    private static String USERCLIENT_ID = "m062TLGH5WOdApYD3jXcM6jA5OnRleVmAoTw3zfu";
    private static String USERCLIENT_SECRET = "6jtLHhKuefsPogv8J8PBcPUzVrqOkcYRuPFmAqml6gukbStgl0bjSUPHFFoaDbWKZ1tCDlOrQo69irklmFTEKsYE2kCrUGZmgeLSM37yDLJhhviLujmgeZdMATHSx90a";
    private static String USER_GRANT_TYPE = "password";
    private static String USERNAME = "";
    private static String PASSWORD = "";

    public APIAuthResponse apiAuthResponse = null;
    public ServerResponse serverResponse = null;
    public boolean successful = false;
    public String email = "";
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

    public int getServerResponseCode(){
        return serverResponse.getResponseCode();
    }

    public void loginPost(String username, String password) {
        USERNAME = username;
        PASSWORD = password;

        Log.d(TAG, "Username: " + USERNAME);
        Log.d(TAG, "Password: " + PASSWORD);

        //Here a logging interceptor is created
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        //The logging interceptor will be added to the http client
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        httpClient.connectTimeout(60, TimeUnit.SECONDS);

        //Gson object
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        //The Retrofit builder will have the client attached, in order to get connection logs
        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(SERVER_URL)
                .build();
        ServerRequestInterface service = retrofit.create(ServerRequestInterface.class);

        getToken(service, username, password);
    }

    public void clientLoginPost() {

        //Here a logging interceptor is created
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        //The logging interceptor will be added to the http client
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        httpClient.connectTimeout(60, TimeUnit.SECONDS);

        //Gson object
        Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .create();

        //The Retrofit builder will have the client attached, in order to get connection logs
        Retrofit retrofit = new Retrofit.Builder()
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(SERVER_URL)
            .build();
        ServerRequestInterface service = retrofit.create(ServerRequestInterface.class);

        getToken(service, "", "");
    }


    public void signoutPost() {
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

        Call<Void> apiCall = null;

        apiCall = service.postRevokeToken(USER_TOKEN, CLIENT_ID, CLIENT_SECRET);

        try {
            Response<Void> response = apiCall.execute();

            successful = response.isSuccessful();
            USER_TOKEN = "";
            Log.d(TAG, "Response isSuccessful(): " + successful);
            Log.d(TAG, "USER_TOKEN: " + USER_TOKEN);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void getToken(ServerRequestInterface service, String username, String password) {
        Call<APIAuthResponse> apiCall = null;
        if(client == ClientType.USERCLIENT){
            apiCall = service.postUserToken(client.clientID, client.clientSecret, client.grantType, username, password);
        } else{
            apiCall = service.postClientToken(CLIENT_ID, CLIENT_SECRET, GRANT_TYPE);
        }

        //synchronous call
        try {
            Response<APIAuthResponse> response = apiCall.execute();

            successful = response.isSuccessful();
            USER_TOKEN = response.body().getToken();
            Log.d(TAG, "Response isSuccessful(): " + response.isSuccessful());
            Log.d(TAG, "Access Token: " + response.body().getToken());
            Log.d(TAG, "Refresh Token: " + response.body().getRefreshToken());
            Log.d(TAG, "Token Type: " + response.body().getAccessType());
            Log.d(TAG, "Scope: " + response.body().getScope());
            Log.d(TAG, "Expire Time: " + response.body().getExpireTime());
            Log.d(TAG, "USER_TOKEN: " + USER_TOKEN);
            Log.d(TAG, "GET_TOKEN_SUCCESSFUL_1: " + successful);
        } catch(IOException e){
            e.printStackTrace();
        }
        Log.d(TAG, "GET_TOKEN_SUCCESSFUL_2: " + successful);
    }

    public void queryPost() {
        successful = false;

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

        Call<Account> call = null;
        API_TOKEN = "Bearer " + USER_TOKEN;
        call = service.apiPost(API_TOKEN, makeProfileQuery());

        try {
            Response<Account> response = call.execute();

            successful = response.isSuccessful();
            email = response.body().getEmail();

            Log.d(TAG, "Response isSuccessful: " + response.isSuccessful());
            Log.d(TAG, "Response Token: " + response.body().getToken());
            Log.d(TAG, "Response Message: " + response.message());
            Log.d(TAG, "Response Email: " + response.body().getEmail());
            Log.d(TAG, "Response FName: " + response.body().getFirstName());
            Log.d(TAG, "Response ID: " + response.body().getId());
            Log.d(TAG, "Response Interests: " + response.body().getInterests());
            Log.d(TAG, "Response LName: " + response.body().getLastName());
            Log.d(TAG, "Response Phone Number: " + response.body().getPhoneNumber());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registerUserPost(String username, String email, String firstName, String lastName, String password) {
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

        Call<Account> call = null;
        API_TOKEN = "Bearer " + USER_TOKEN;
        call = service.apiPost(API_TOKEN, registerUserQuery(username, email, firstName, lastName, password));

        try {
            Response<Account> response = call.execute();

            successful = response.isSuccessful();
            email = response.body().getEmail();

            Log.d(TAG, "Response isSuccessful: " + response.isSuccessful());
            Log.d(TAG, "Response FName: " + response.body().getFirstName());
            Log.d(TAG, "Response LName: " + response.body().getLastName());
            Log.d(TAG, "Response Token: " + response.body().getToken());
            Log.d(TAG, "Response Email: " + response.body().getEmail());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String makeQuery(String user, String pass, String email) {
        return String.format("mutation{\nloginUser (input: {\n    username: \"{0}\"\n    password: \"{1}\"\n    email: \"{2}\"\n  }) {\n    ok\n    user {\n    token\n    }\n  }\n}\n",
                user,
                pass,
                email
        );
    }

    private String makeProfileQuery() {
//      return "{\n  myProfile {\n    id\n    user {\n      email\n    }\n    interests\n    phoneNumber \n  }\n}";
      return "{myProfile {id, user{email}, interests, phoneNumber }}";
//      return "{\n  myProfile {\n    id\n    user {\n      email\n    user{\n firstname\n lastname\n}}\n    interests\n    phoneNumber \n  }\n}";
    }

    private String registerUserQuery(String username, String email, String firstName, String lastName, String password) {
        return String.format("mutation{\nregisterAccount(lastName:\"{0}\", firstName:\"{1}\", email:\"{2}\", username:\"{3}\", password:\"{4}\"){ok, account{username, email, firstName, lastName}}}", lastName, firstName, email, username, password);
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