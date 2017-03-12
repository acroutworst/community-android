package com.android.community.authentication;

import com.android.community.models.AccountRegistration;
import com.android.community.models.Account;
import com.android.community.models.Event;
import com.android.community.models.EventResponse;
import com.android.community.models.Meetup;
import com.android.community.models.Profile;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by adamc on 1/21/17.
 */

public interface ServerRequestInterface {

    @FormUrlEncoded
    @POST("/o/token/")
    Call<APIAuthResponse> postClientToken(
            @Field("client_id") String client_id,
            @Field("client_secret") String client_secret,
            @Field("grant_type") String grant_type
    );

    @FormUrlEncoded
    @POST("/o/token/")
    Call<APIAuthResponse> postUserToken(
            @Field("client_id") String client_id,
            @Field("client_secret") String client_secret,
            @Field("grant_type") String grant_type,
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("/api/")
    Call<APIAuthResponse> postRegisterUser(
            @Field("lastName") String lastName,
            @Field("firstName") String firstName,
            @Field("email") String email,
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("/o/token/")
    Call<APIAuthResponse> postRefreshToken(
            @Field("client_id") String client_id,
            @Field("client_secret") String client_secret,
            @Field("grant_type") String grant_type,
            @Field("refresh_token") String refresh_token
    );

    @FormUrlEncoded
    @POST("/o/revoke_token/")
    Call<Void> postRevokeToken(
            @Field("token") String token,
            @Field("client_id") String client_id,
            @Field("client_secret") String client_secret
    );

    //This method is used for "POST"
    // TODO: Need to make this api post call generic
    @FormUrlEncoded
    @POST("/api/")
    Call<Profile> apiPost(
            @Header("authorization") String token,
            @Field("query") String GraphQLQuery
    );

    //This method is used for "POST"
    @FormUrlEncoded
    @POST("/api/")
    Call<Account> apiAccountPost(
        @Header("authorization") String token,
        @Field("query") String GraphQLQuery
    );

    //This method is used for "POST"
    @FormUrlEncoded
    @POST("/api/")
    Call<ResponseBody> apiMeetupPost(
        @Header("authorization") String token,
        @Field("query") String GraphQLQuery
    );

    //This method is used for "POST"
    @FormUrlEncoded
    @POST("/api/")
    Call<ResponseBody> apiCreateMeetupPost(
            @Header("authorization") String token,
            @Field("query") String GraphQLQuery
    );

    //This method is used for "POST"
    @FormUrlEncoded
    @POST("/api/")
    Call<ResponseBody> apiEventPost(
        @Header("authorization") String token,
        @Field("query") String GraphQLQuery
    );

    Call<ResponseBody> apiCreateEventPost(
            @Header("authorization") String token,
            @Field("query") String GraphQLQuery
    );



    //This method is used for "POST"
    @FormUrlEncoded
    @POST("/api/")
    Call<AccountRegistration> postAccountRegistration(
            @Header("authorization") String token,
            @Field("query") String GraphQLQuery
    );
}