package com.android.community.authentication;

import com.android.community.AccountService;
import com.android.community.DataAdapter;
import com.android.community.deserializer.EventDeserializer;
import com.android.community.deserializer.GroupDeserializer;
import com.android.community.deserializer.MeetupDeserializer;
import com.android.community.deserializer.ProfileDeserializer;
import com.android.community.deserializer.UserDeserializer;
import com.android.community.models.AccountRegistration;
import com.android.community.deserializer.AccountDeserializer;
import com.android.community.models.Account;
import com.android.community.models.Event;
import com.android.community.models.EventResponse;
import com.android.community.models.Meetup;
import com.android.community.models.Profile;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.otto.Produce;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

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

        Timber.d("Username: " + USERNAME);
        Timber.d("Password: " + PASSWORD);

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
            AccountService.Instance().mAuthToken = "";

            Timber.d("Response isSuccessful(): " + successful);
            Timber.d("USER_TOKEN: " + USER_TOKEN);
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

            AccountService.Instance().mAuthToken = USER_TOKEN;

            Timber.d("AccountService: " + AccountService.Instance().mAuthToken);

            Timber.d("Response isSuccessful(): " + response.isSuccessful());
            Timber.d("Access Token: " + response.body().getToken());
            Timber.d("Refresh Token: " + response.body().getRefreshToken());
            Timber.d("Token Type: " + response.body().getAccessType());
            Timber.d("Scope: " + response.body().getScope());
            Timber.d("Expire Time: " + response.body().getExpireTime());
            Timber.d("USER_TOKEN: " + USER_TOKEN);
            Timber.d("GET_TOKEN_SUCCESSFUL_1: " + successful);
        } catch(IOException e){
            e.printStackTrace();
        }
        Timber.d("GET_TOKEN_SUCCESSFUL_2: " + successful);
    }

    public void queryProfilePost() {
        successful = false;

        //Here a logging interceptor is created
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        //The logging interceptor will be added to the http client
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        Gson gson = new GsonBuilder()
            .registerTypeAdapter(Account.class, new UserDeserializer())
            .create();

        API_TOKEN = "Bearer " + USER_TOKEN;

        Retrofit retrofit = new Retrofit.Builder()
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(SERVER_URL)
            .build();
        ServerRequestInterface service = retrofit.create(ServerRequestInterface.class);

        Call<Account> user_call = null;
        user_call = service.apiAccountPost(API_TOKEN, makeUserQuery());

        try {
            Response<Account> response = user_call.execute();

            successful = response.isSuccessful();

            AccountService.Instance().mAccount.email = response.body().getEmail();
            AccountService.Instance().mAccount.lastName = response.body().getLastName();
            AccountService.Instance().mAccount.firstName = response.body().getFirstName();
            AccountService.Instance().mAccount.id = response.body().getId();
            AccountService.Instance().mAccount.username = response.body().getUsername();
            AccountService.Instance().mAccount.isActive = response.body().getIsActive();

            Timber.d("Response isSuccessful: " + response.isSuccessful());
            Timber.d("AccountService Email: " + AccountService.Instance().mAccount.email);
            Timber.d("Response Email: " + response.body().getEmail());
            Timber.d("Response FName: " + response.body().getFirstName());
            Timber.d("Response ID: " + response.body().getId());
            Timber.d("Response LName: " + response.body().getLastName());

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

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(AccountRegistration.class, new AccountDeserializer())
                .create();

        //The Retrofit builder will have the client attached, in order to get connection logs
        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(SERVER_URL)
                .build();

        ServerRequestInterface service = retrofit.create(ServerRequestInterface.class);

        Call<AccountRegistration> call = null;
        API_TOKEN = "Bearer " + USER_TOKEN;
        call = service.postAccountRegistration(API_TOKEN, registerUserQuery(username, email, firstName, lastName, password));

        try {
            Response<AccountRegistration> response = call.execute();

            successful = response.isSuccessful();

            Timber.d("Response isSuccessful: " + response.isSuccessful());
            Timber.d("Response username: " + response.body().getUsername());
            Timber.d("Response Email: " + response.body().getEmail());
            Timber.d("Response FName: " + response.body().getFirstName());
            Timber.d("Response LName: " + response.body().getLastName());
            Timber.d("Response Email: " + response.body().getEmail());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addGroupPost(String title, String description) {
        //Here a logging interceptor is created
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        //The logging interceptor will be added to the http client
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        Gson gson = new GsonBuilder()
            .registerTypeAdapter(AccountRegistration.class, new GroupDeserializer())
            .create();

        //The Retrofit builder will have the client attached, in order to get connection logs
        Retrofit retrofit = new Retrofit.Builder()
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(SERVER_URL)
            .build();

        ServerRequestInterface service = retrofit.create(ServerRequestInterface.class);

        Call<ResponseBody> call = null;
        API_TOKEN = "Bearer " + USER_TOKEN;
        call = service.apiEventPost(API_TOKEN, registerGroup(title, description));

        try {
            Response<ResponseBody> response = call.execute();

            successful = response.isSuccessful();

            Timber.d("Response Group: " + response.body().toString());
            Timber.d("Response isSuccessful: " + response.isSuccessful());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addEventPost(String title, String description, String location) {
        //Here a logging interceptor is created
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        //The logging interceptor will be added to the http client
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        Gson gson = new GsonBuilder()
            .registerTypeAdapter(AccountRegistration.class, new EventDeserializer())
            .create();

        //The Retrofit builder will have the client attached, in order to get connection logs
        Retrofit retrofit = new Retrofit.Builder()
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(SERVER_URL)
            .build();

        ServerRequestInterface service = retrofit.create(ServerRequestInterface.class);

        Call<ResponseBody> call = null;
        API_TOKEN = "Bearer " + USER_TOKEN;
        call = service.apiEventPost(API_TOKEN, registerEvent(title, description, location));

        try {
            Response<ResponseBody> response = call.execute();

            successful = response.isSuccessful();

            Timber.d("Response Event: " + response.body().toString());
            Timber.d("Response isSuccessful: " + response.isSuccessful());
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
      return "{myProfile {id, interests }}";
    }

    private String makeUserQuery() {
        return "{myProfile { user {lastLogin, username, firstName, lastName, email, isActive, dateJoined }}}";
    }

    private String makeMeetupQuery() {
        return "{allMeetups { edges { node { id, createdDate, duration, name, description, maxAttendees, private, active, creator { username }, community { title, acronym }}}}}";
    }

    private String registerGroup(String title, String description) {
        return String.format("mutation{\nregisterGroup(community:\"%s\", title:\"%s\", description:\"%s\"){ok, group{id, title, description}}}", "Q29tbXVuaXR5Tm9kZTox", title, description);
    }

    private String registerEvent(String title, String description, String location) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("dd-MM-yyy HH:mm:ss z");
        date.setTimeZone(TimeZone.getTimeZone("GMT"));
        String localTime = date.format(currentLocalTime);
        return String.format("mutation{\nregisterGroup(community:\"%s\", title:\"%s\", description:\"%s\", location:\"%s\", private:\"%s\", startDatetime:\"%s\", endDatetime:\"%s\"){ok, event{id, title, description, location}}}", "Q29tbXVuaXR5Tm9kZTox", title, description, location, false, localTime, localTime);
    }

    private String registerUserQuery(String username, String email, String firstName, String lastName, String password) {
        return String.format("mutation{\nregisterAccount(lastName:\"%s\", firstName:\"%s\", email:\"%s\", username:\"%s\", password:\"%s\"){ok, account{username, email, firstName, lastName}}}", lastName, firstName, email, username, password);
    }

    private String registerMeetup(String name, String community, String description, int maxAttendees, int duration, boolean mPrivate) {
        return String.format("mutation{\nregisterMeetup(name:\"%s\", community:\"%s\", description:\"%s\", maxAttendees:\"%s\", duration:\"%s\", private:\"%s\"){ok, meetup{name, community, description, maxAttendees, duration, private}}}", name, community, description, maxAttendees, duration, mPrivate);
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